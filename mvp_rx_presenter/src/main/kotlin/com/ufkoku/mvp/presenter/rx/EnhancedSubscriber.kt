package com.ufkoku.mvp.presenter.rx

import com.ufkoku.mvp.presenter.BaseAsyncPresenter
import com.ufkoku.mvp_base.view.IMvpView
import rx.Subscriber
import rx.Subscription
import java.lang.ref.WeakReference

/**
 * Use with ViewWraps only;
 * Don't use with subscribeOn(AndroidSchedulers.mainThread()) or observeOn(AndroidSchedulers.mainThread()), all calls from main thread must be excluded.
 * */
abstract class EnhancedSubscriber<T, V : IMvpView>(val presenter: BaseAsyncPresenter<V>) : Subscriber<T>() {

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
        synchronized(presenter.lockObject) {
            setAndCheckThread()
            val v = presenter.waitForViewIfNeeded()
            onCompleted(v)
        }
    }

    final override fun onError(e: Throwable) {
        if (!presenter.checkIfInterruptedException(e)) {
            synchronized(presenter.lockObject) {
                try {
                    setAndCheckThread()
                    val v = presenter.waitForViewIfNeeded()
                    onError(e, v)
                } catch (ex: RuntimeException) {
                    if (presenter.checkIfInterruptedException(ex)) {
                        onInterruptedError(e)
                    }
                }
            }
        } else {
            onInterruptedError(e)
        }
    }

    override fun onNext(t: T) {
        synchronized(presenter.lockObject) {
            setAndCheckThread()
            val v = presenter.waitForViewIfNeeded()
            onNext(t, v)
        }
    }

    abstract fun onCompleted(view: V)

    abstract fun onError(e: Throwable, view: V)

    abstract fun onInterruptedError(e: Throwable)

    abstract fun onNext(value: T, view: V)

}
