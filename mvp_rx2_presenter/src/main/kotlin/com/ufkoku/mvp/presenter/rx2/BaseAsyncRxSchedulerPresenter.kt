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

import com.ufkoku.mvp.presenter.BaseAsyncExecutorPresenter
import com.ufkoku.mvp.presenter.BaseAsyncPresenter
import com.ufkoku.mvp_base.presenter.IAsyncPresenter
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Action
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.atomic.AtomicBoolean

abstract class BaseAsyncRxSchedulerPresenter<T : IAsyncPresenter.ITaskListener> : BaseAsyncExecutorPresenter<T>() {

    var scheduler: Scheduler? = null

    override fun onAttachView(view: T) {
        super.onAttachView(view)
        if (scheduler == null) {
            scheduler = Schedulers.from(executor)
        }
    }

    override fun cancel() {
        if (scheduler != null) {
            scheduler = null
        }
        super.cancel()
    }

    /**
     * Executes and provide results to subscription inside provided schedulers.
     * */
    fun <T> execute(observable: Observable<T>,
                    subscribeOn: Scheduler,
                    observeOn: Scheduler,
                    id: Int,
                    onNext: Consumer<T>? = null,
                    onError: Consumer<Throwable>? = null,
                    onComplete: Action? = null,
                    provideInterruptedException: Boolean = false): Disposable {
        return IdDisposable(observable.subscribeOn(subscribeOn).observeOn(observeOn),
                            id,
                            onNext,
                            onError,
                            onComplete,
                            provideInterruptedException)
    }

    /**
     * Executes and provide results to subscription inside presenter's scheduler.
     *
     * Perfect to use with mvp_view_wrap module.
     *
     * */
    fun <T> execute(observable: Observable<T>,
                    taskId: Int,
                    onNext: Consumer<T>? = null,
                    onError: Consumer<Throwable>? = null,
                    onComplete: Action? = null,
                    provideInterruptedException: Boolean = false): Disposable {
        return execute(observable, scheduler!!, scheduler!!, taskId, onNext, onError, onComplete, provideInterruptedException)
    }

    protected inner class IdDisposable<T>(observable: Observable<T>,
                                          val id: Int,
                                          val onNext: Consumer<T>?,
                                          val onError: Consumer<Throwable>?,
                                          val onComplete: Action?,
                                          val provideInterruptedException: Boolean) : Disposable {

        private var taskFinishedNotified = false

        private val innerDisposable = observable.subscribe(
                {
                    onNext?.accept(it)
                },
                {
                    if (provideInterruptedException || !this@BaseAsyncRxSchedulerPresenter.checkIfInterruptedException(it)) {
                        onError?.accept(it)
                    }
                    notifyTaskFinishedIfNeeded()
                },
                {
                    onComplete?.run()
                    notifyTaskFinishedIfNeeded()
                },
                {
                    this@BaseAsyncRxSchedulerPresenter.notifyTaskAdded(id)
                })

        override fun dispose() {
            innerDisposable.dispose()
            notifyTaskFinishedIfNeeded()
        }

        override fun isDisposed(): Boolean {
            return innerDisposable.isDisposed
        }

        private fun notifyTaskFinishedIfNeeded() {
            synchronized(this) {
                if (!taskFinishedNotified) {
                    taskFinishedNotified = true
                    this@BaseAsyncRxSchedulerPresenter.notifyTaskFinished(id)
                }
            }
        }

    }

}
