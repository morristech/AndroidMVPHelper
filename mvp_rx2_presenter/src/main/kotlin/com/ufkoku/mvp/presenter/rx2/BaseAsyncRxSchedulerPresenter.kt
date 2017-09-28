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

    fun <T> execute(observable: Observable<T>, id: Int, onNext: Consumer<T>?, onError: Consumer<Throwable>?, onComplete: Action?): Disposable {
        return IdDisposable(observable, id, onNext, onError, onComplete)
    }

    private inner class IdDisposable<T>(observable: Observable<T>,
                                        val id: Int,
                                        val onNext: Consumer<T>?,
                                        val onError: Consumer<Throwable>?,
                                        val onComplete: Action?) : Disposable {

        init {
            this@BaseAsyncRxSchedulerPresenter.notifyTaskAdded(id)
        }

        private var taskFinishedNotified = false

        private val innerDisposable = observable.subscribe(
                {
                    onNext?.accept(it)
                },
                {
                    if (!this@BaseAsyncRxSchedulerPresenter.checkIfInterruptedException(it)) {
                        onError?.accept(it)
                    }
                    notifyTaskFinishedIfPossible()
                },
                {
                    onComplete?.run()
                    notifyTaskFinishedIfPossible()
                })

        override fun dispose() {
            innerDisposable.dispose()
            notifyTaskFinishedIfPossible()
        }

        override fun isDisposed(): Boolean {
            return innerDisposable.isDisposed
        }

        private fun notifyTaskFinishedIfPossible() {
            synchronized(this) {
                if (!taskFinishedNotified) {
                    taskFinishedNotified = true
                    this@BaseAsyncRxSchedulerPresenter.notifyTaskFinished(id)
                }
            }
        }

    }

}
