package com.ufkoku.mvp.presenter.rx.utils

import com.ufkoku.mvp.presenter.BaseAsyncExecutorPresenter
import rx.Observable
import rx.Subscriber

abstract class UiWaitingOnSubscribe<T>(val presenter: BaseAsyncExecutorPresenter<*>) : Observable.OnSubscribe<T> {

    final override fun call(t: Subscriber<in T>?) {
        call(UiWaitingOnSubscriber(t!!, presenter))
    }

    abstract fun call(subscriber: UiWaitingOnSubscriber<T>)

}