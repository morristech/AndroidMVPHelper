package com.ufkoku.mvp.presenter.rx.utils

import com.ufkoku.mvp.presenter.BaseAsyncExecutorPresenter
import rx.Observer
import rx.Subscriber
import rx.Subscription

class UiWaitingOnSubscriber<T>(val subscriber: Subscriber<in T>, val presenter: BaseAsyncExecutorPresenter<*>) : Observer<T>, Subscription {

    override fun isUnsubscribed(): Boolean {
        return subscriber.isUnsubscribed
    }

    override fun unsubscribe() {
        subscriber.unsubscribe()
    }

    override fun onNext(t: T) {
        presenter.waitForViewIfNeeded()
        subscriber.onNext(t)
    }

    override fun onCompleted() {
        presenter.waitForViewIfNeeded()
        subscriber.onCompleted()
    }

    override fun onError(e: Throwable?) {
        presenter.waitForViewIfNeeded()
        subscriber.onError(e)
    }

}