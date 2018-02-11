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
import android.support.annotation.MainThread
import android.support.annotation.WorkerThread
import com.ufkoku.mvp_base.presenter.IAsyncPresenter
import java.util.*
import java.util.concurrent.AbstractExecutorService
import java.util.concurrent.Callable
import java.util.concurrent.Future

open class BaseAsyncPresenter<V : BaseAsyncPresenter.ITaskListener> : BasePresenter<V>(), IAsyncPresenter<V> {

    companion object {
        val TASK_ADDED = 0
        val TASK_FINISHED = 1
    }

    /**
     * Variable is used in method waitForView().
     * */
    protected val viewSyncObject = Object()

    private var taskStatusListener: ITaskListener? = null

    private val runningTasks: MutableList<Int> = Collections.synchronizedList(LinkedList())

    private val waitingResults: MutableList<((V) -> Unit)> = LinkedList()

    @CallSuper
    @MainThread
    override fun onAttachView(view: V) {
        synchronized(viewSyncObject) {
            super.onAttachView(view)

            taskStatusListener = view

            //populate waiting results
            //toList prevents crash
            waitingResults.toList().forEach { result -> result.invoke(view) }
            waitingResults.clear()

            notifyLockObject()
        }
    }

    @CallSuper
    @MainThread
    override fun onDetachView() {
        synchronized(viewSyncObject) {
            super.onDetachView()
            taskStatusListener = null
        }
    }

    @CallSuper
    @MainThread
    override fun cancel() {
        runningTasks.clear()
        waitingResults.clear()
        notifyLockObject()
    }

    /**
     * Executes callable with provided executor
     * */
    fun <T> Callable<T>.execute(executor: AbstractExecutorService, id: Int): Future<T> {
        notifyTaskAdded(id)
        return executor.submit(Callable<T> {
            try {
                return@Callable this.call()
            } finally {
                notifyTaskFinished(id)
            }
        })
    }

    /**
     * Executes Runnable with provided executor
     * */
    fun Runnable.execute(executor: AbstractExecutorService, id: Int): Future<Unit> {
        notifyTaskAdded(id)
        return executor.submit(Callable<Unit> {
            try {
                this.run()
            } finally {
                notifyTaskFinished(id)
            }
        })
    }

    /**
     * Executes lambda with provide executor
     * */
    fun <T> execute(task: () -> T, executor: AbstractExecutorService, id: Int): Future<T> {
        notifyTaskAdded(id)
        return executor.submit(Callable<T> {
            try {
                return@Callable task.invoke()
            } finally {
                notifyTaskFinished(id)
            }
        })
    }

    /**
     * Use this method to get attached view for result populating (from worker thread).
     * It will wait for view attach, via calling wait() on viewSyncObject.
     * viewSyncObject.notifyAll() will be called after onAttachView().
     *
     * @return view, attached to presenter
     *
     * @throws RuntimeException with cause InterruptedException, if thread was interrupted
     * */
    @WorkerThread
    fun waitForView(): V {
        synchronized(viewSyncObject) {
            if (Thread.interrupted()) {
                throw RuntimeException(InterruptedException("Thread ${Thread.currentThread().name} is interrupted."))
            }
            if (view == null) {
                try {
                    viewSyncObject.wait()
                } catch (e: InterruptedException) {
                    throw RuntimeException(e)
                }
            }
            return view!!
        }
    }

    private fun notifyLockObject() {
        synchronized(viewSyncObject) {
            try {
                viewSyncObject.notifyAll()
            } catch (ignored: IllegalMonitorStateException) {

            }
        }
    }

    /**
     * Use this method to provide results to view asynchronously from any thread.
     * If view is attached to presenter, result will be passed immediately,
     * otherwise results will passed after view attached.
     * */
    protected fun postResult(body: (V) -> Unit) {
        if (isInMainThread()) {
            val view = view
            if (view != null) {
                body.invoke(view)
            } else {
                waitingResults.add(body)
            }
        } else {
            postOnMainThread { postResult(body) }
        }
    }

    /**
     * @return true if caller thread is main.
     * */
    protected fun isInMainThread(): Boolean = Looper.getMainLooper() == Looper.myLooper()

    /**
     * Executes given expression in main thread.
     * */
    protected inline fun postOnMainThread(crossinline body: () -> Unit) {
        if (isInMainThread()) {
            body()
        } else {
            Handler(Looper.getMainLooper()).post { body() }
        }
    }

    /***
     * Checks if exception or its cause is InterruptedException.
     */
    protected fun Throwable.isInterruptedException(): Boolean {
        var currentLevel: Throwable? = this
        while (currentLevel != null) {
            if (currentLevel.javaClass == InterruptedException::class.java) {
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
        var taskStatusListener: ITaskListener? = null
        synchronized(viewSyncObject) {
            taskStatusListener = this.taskStatusListener
        }
        postOnMainThread { taskStatusListener?.onTaskStatusChanged(task, TASK_ADDED) }
    }

    /**
     * Removes task id from list, notifies attached view if it is possible
     * */
    protected fun notifyTaskFinished(task: Int) {
        runningTasks.remove(task)
        var taskStatusListener: ITaskListener? = null
        synchronized(viewSyncObject) {
            taskStatusListener = this.taskStatusListener
        }
        postOnMainThread { taskStatusListener?.onTaskStatusChanged(task, TASK_FINISHED) }
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

    interface ITaskListener {

        fun onTaskStatusChanged(taskId: Int, status: Int)

    }

}