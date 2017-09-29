/*
 * Copyright 2017 Ufkoku (https://github.com/Ufkoku/AndroidMVPHelper)
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

import android.os.Handler
import android.os.Looper
import android.support.annotation.CallSuper
import android.support.annotation.WorkerThread
import com.ufkoku.mvp_base.presenter.IAsyncPresenter
import java.util.*
import java.util.concurrent.AbstractExecutorService
import java.util.concurrent.Callable
import java.util.concurrent.Future

open class BaseAsyncPresenter<T : IAsyncPresenter.ITaskListener> : BasePresenter<T>(), IAsyncPresenter<T> {

    companion object {

        @JvmField
        val TASK_ADDED = 0

        @JvmField
        val TASK_FINISHED = 1

    }

    /**
     * Variable is used in method waitForViewIfNeeded().
     * */
    protected val lockObject = Object()

    private var taskStatusListener: IAsyncPresenter.ITaskListener? = null

    private val runningTasks: MutableList<Int> = Collections.synchronizedList(LinkedList())

    @CallSuper
    override fun onAttachView(view: T) {
        synchronized(lockObject) {
            super.onAttachView(view)

            if (view is IAsyncPresenter.ITaskListener) {
                taskStatusListener = view
            }

            notifyLockObject()
        }
    }

    @CallSuper
    override fun onDetachView() {
        synchronized(lockObject) {
            super.onDetachView()
            taskStatusListener = null
        }
    }

    @CallSuper
    override fun cancel() {
        runningTasks.clear()
        notifyLockObject()
    }

    private fun notifyLockObject() {
        synchronized(lockObject) {
            try {
                lockObject.notifyAll()
            } catch (ignored: IllegalMonitorStateException) {

            }
        }
    }

    fun <T> execute(callable: Callable<T>, executor: AbstractExecutorService, id: Int): Future<T> {
        notifyTaskAdded(id)
        return executor.submit(Callable<T> {
            try {
                return@Callable callable.call()
            } finally {
                notifyTaskFinished(id)
            }
        })
    }

    fun execute(runnable: Runnable, executor: AbstractExecutorService, id: Int): Future<Unit> {
        notifyTaskAdded(id)
        return executor.submit(Callable<Unit> {
            try {
                runnable.run()
            } finally {
                notifyTaskFinished(id)
            }
        })
    }

    /**
     * Use this method to get attached view for result populating (from worker thread).
     * It will wait for view attach, via calling wait() on lockObject.
     * lockObject.notifyAll() will be called after onAttachView().
     *
     * @return view, attached to presenter
     *
     * @throws RuntimeException with cause InterruptedException, if thread was interrupted
     * */
    @WorkerThread
    fun waitForViewIfNeeded(): T {
        synchronized(lockObject) {
            if (Thread.interrupted()) {
                throw RuntimeException(InterruptedException("Thread ${Thread.currentThread().name} is interrupted."))
            }
            if (view == null) {
                try {
                    lockObject.wait()
                } catch (e: InterruptedException) {
                    throw RuntimeException(e)
                }
            }
            return view!!
        }
    }

    protected fun checkIfInterruptedException(ex: Throwable?): Boolean {
        var currentLevel = ex
        while (currentLevel != null) {
            if (currentLevel is InterruptedException) {
                return true
            } else {
                currentLevel = currentLevel.cause
            }
        }
        return false
    }

    /**
     * Adds task id to list, notifies attached view if it is possible
     * */
    protected fun notifyTaskAdded(task: Int) {
        runningTasks.add(task)
        var taskStatusListener: IAsyncPresenter.ITaskListener? = null
        synchronized(lockObject) {
            taskStatusListener = this.taskStatusListener
        }
        postOnMainThread { taskStatusListener?.onTaskStatusChanged(task, TASK_ADDED) }
    }

    /**
     * Removes task id from list, notifies attached view if it is possible
     * */
    protected fun notifyTaskFinished(task: Int) {
        runningTasks.remove(task)
        var taskStatusListener: IAsyncPresenter.ITaskListener? = null
        synchronized(lockObject) {
            taskStatusListener = this.taskStatusListener
        }
        postOnMainThread { taskStatusListener?.onTaskStatusChanged(task, TASK_FINISHED) }
    }

    protected fun isInMainThread(): Boolean {
        return Looper.getMainLooper() == Looper.myLooper()
    }

    protected inline fun postOnMainThread(crossinline body: () -> Unit) {
        if (isInMainThread()) {
            body()
        } else {
            Handler(Looper.getMainLooper()).post { body() }
        }
    }

    fun isTaskRunning(task: Int): Boolean {
        return runningTasks.contains(task)
    }

    fun isAnyOfTasksRunning(vararg tasks: Int): Boolean {
        return tasks.any { isTaskRunning(it) }
    }

    fun hasRunningTasks(): Boolean {
        return runningTasks.size > 0
    }

}