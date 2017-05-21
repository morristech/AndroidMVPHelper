/*
 * Copyright 2016 Ufkoku (https://github.com/Ufkoku/AndroidMVPHelper)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ufkoku.mvp.presenter.rx

import com.ufkoku.mvp.presenter.BaseAsyncPresenter
import rx.Subscriber
import rx.Subscription
import java.lang.ref.WeakReference

/**
 * Use with ViewWraps only;
 * Don't use with subscribeOn(AndroidSchedulers.mainThread()) or observeOn(AndroidSchedulers.mainThread()), all calls from main thread must be excluded.
 * */
abstract class EnhancedSubscriber<T>(val presenter: BaseAsyncPresenter<*>) : Subscriber<T>() {

    protected var subscriberThread: WeakReference<Thread>? = null

    init {
        add(object : Subscription {

            private var subscribed = true

            override fun isUnsubscribed(): Boolean {
                return !subscribed
            }

            override fun unsubscribe() {
                if (subscribed) {
                    subscriberThread?.get()?.interrupt()
                    subscribed = false
                }
            }
        })
    }

    protected fun setAndCheckThread() {
        if (isUnsubscribed) {
            Thread.currentThread().interrupt()
        } else {
            subscriberThread = WeakReference(Thread.currentThread())
        }
    }

    final override fun onCompleted() {
        setAndCheckThread()
        onCompletedImpl()
    }

    final override fun onError(e: Throwable) {
        if (!presenter.checkIfInterruptedException(e)) {
            try {
                setAndCheckThread()
                onErrorImpl(e)
            } catch (ex: RuntimeException) {
                if (presenter.checkIfInterruptedException(ex)) {
                    onInterruptedErrorImpl(e)
                }
            }
        } else {
            onInterruptedErrorImpl(e)
        }
    }

    final override fun onNext(t: T) {
        setAndCheckThread()
        onNextImpl(t)
    }

    abstract fun onCompletedImpl()

    abstract fun onErrorImpl(e: Throwable)

    abstract fun onInterruptedErrorImpl(e: Throwable)

    abstract fun onNextImpl(value: T)

}
