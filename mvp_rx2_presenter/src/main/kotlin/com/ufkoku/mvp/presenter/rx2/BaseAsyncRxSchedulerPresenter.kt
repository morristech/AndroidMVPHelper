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

package com.ufkoku.mvp.presenter.rx2

import android.support.annotation.MainThread
import com.ufkoku.mvp.presenter.BaseAsyncExecutorPresenter
import com.ufkoku.mvp_base.presenter.IAsyncPresenter
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.ThreadPoolExecutor

abstract class BaseAsyncRxSchedulerPresenter<T : IAsyncPresenter.ITaskListener> : BaseAsyncExecutorPresenter<T>() {

    var scheduler: Scheduler? = null

    @MainThread
    override fun cancel() {
        if (scheduler != null) {
            scheduler?.shutdown()
            scheduler = null
        }
        super.cancel()
    }

    override fun onExecutorCreated(executor: ThreadPoolExecutor) {
        super.onExecutorCreated(executor)
        if (scheduler == null) {
            scheduler = Schedulers.from(executor)
            onSchedulerCreated()
        }
    }

    protected open fun onSchedulerCreated() {

    }

    /**
     *  Adds id to observable
     * */
    open fun <T> Observable<T>.withId(taskId: Int): Observable<T> {
        return this.doOnSubscribe({ notifyTaskAdded(taskId) })
                .doFinally { notifyTaskFinished(taskId) }
    }

    /**
     *  Adds id to flowable
     * */
    open fun <T> Flowable<T>.withId(taskId: Int): Flowable<T> {
        return this.doOnSubscribe({ notifyTaskAdded(taskId) })
                .doFinally { notifyTaskFinished(taskId) }
    }

    /**
     *  Adds id to Single
     * */
    open fun <T> Single<T>.withId(taskId: Int): Single<T> {
        return this.doOnSubscribe({ notifyTaskAdded(taskId) })
                .doFinally { notifyTaskFinished(taskId) }
    }

    /**
     *  Filter interrupted exception
     * */
    open fun <T> Observable<T>.catchInterruptedException(): Observable<T> {
        return this.onErrorResumeNext { t: Throwable ->
            if (t.isInterruptedException()) {
                Observable.empty()
            } else {
                throw t
            }
        }
    }

    /**
     *  Filter interrupted exception
     * */
    open fun <T> Flowable<T>.catchInterruptedException(): Flowable<T> {
        return this.onErrorResumeNext { t: Throwable ->
            if (t.isInterruptedException()) {
                Flowable.empty()
            } else {
                throw t
            }
        }
    }

    open fun interruptedCatcher(consumer: ((Throwable) -> Unit)? = null): (Throwable) -> Unit {
        return { throwable ->
            if (!throwable.isInterruptedException()) consumer?.invoke(throwable)
        }
    }

    open fun interruptedCatcher(consumer: Consumer<in Throwable>?): (Throwable) -> Unit {
        return { throwable ->
            if (!throwable.isInterruptedException()) consumer?.accept(throwable)
        }
    }

}
