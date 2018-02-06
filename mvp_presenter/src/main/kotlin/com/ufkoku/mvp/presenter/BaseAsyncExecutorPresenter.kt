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

package com.ufkoku.mvp.presenter

import android.support.annotation.MainThread
import com.ufkoku.mvp_base.presenter.IAsyncPresenter
import java.util.concurrent.Callable
import java.util.concurrent.Future
import java.util.concurrent.ThreadFactory
import java.util.concurrent.ThreadPoolExecutor

abstract class BaseAsyncExecutorPresenter<T : IAsyncPresenter.ITaskListener> : BaseAsyncPresenter<T>(), IAsyncPresenter<T> {

    protected var executor: ThreadPoolExecutor? = null

    @MainThread
    override fun onAttachView(view: T) {
        var executor = this.executor
        if (executor == null) {
            executor = createExecutor()
            this.executor = executor
            onExecutorCreated(executor)
        }

        super.onAttachView(view)
    }

    @MainThread
    override fun cancel() {
        executor?.shutdownNow()
        executor = null

        super.cancel()
    }

    /**
     * Executes callable with Presenter's executor
     * */
    fun <T> Callable<T>.execute(id: Int): Future<T> {
        return this.execute(executor!!, id)
    }

    /**
     * Executes Runnable with Presenter's executor
     * */
    fun Runnable.execute(id: Int): Future<Unit> {
        return this.execute(executor!!, id)
    }

    /**
     * Executes lambda with Presenter's executor
     * */
    fun <T> execute(task: () -> T, id: Int): Future<T> {
        return execute(task, executor!!, id)
    }

    protected abstract fun createExecutor(): ThreadPoolExecutor

    protected open fun onExecutorCreated(executor: ThreadPoolExecutor) {
        if (useSaveThreadFactory()) {
            if (executor.threadFactory !is SaveThreadFactory) {
                executor.threadFactory = SaveThreadFactory(executor.threadFactory)
            }
        }
    }

    protected open fun useSaveThreadFactory(): Boolean = true

    protected class SaveThreadFactory(private val delegate: ThreadFactory) : ThreadFactory {

        override fun newThread(r: Runnable?): Thread {
            val thread = delegate.newThread(r)
            thread.uncaughtExceptionHandler = Thread.UncaughtExceptionHandler { _, _ -> /*ignored*/ }
            return thread
        }

    }

}
