package com.ufkoku.demo_app.ui.common.presenter

import com.ufkoku.demo_app.entity.AwesomeEntity
import com.ufkoku.demo_app.model.PageEntityModel
import com.ufkoku.demo_app.model.ProcessEntityModel
import com.ufkoku.mvp.presenter.rx2.BaseAsyncRxSchedulerPresenter
import com.ufkoku.mvp.view.wrap.IWrapped
import com.ufkoku.mvp_base.presenter.IAsyncPresenter
import io.reactivex.android.schedulers.AndroidSchedulers
import java.util.*
import java.util.concurrent.ScheduledThreadPoolExecutor
import java.util.concurrent.ThreadPoolExecutor

class StaticListPresenter<V> : BaseAsyncRxSchedulerPresenter<V>() where V : StaticListPresenter.PresenterListener, V : IWrapped {

    companion object {
        val TASK_FETCH_DATA: Int = 1
        val TASK_PROCESS: Int = 2
    }

    override fun createExecutor(): ThreadPoolExecutor = ScheduledThreadPoolExecutor(1)

    fun fetchData() {
        PageEntityModel.createPageObservable(0)
                .withId(TASK_FETCH_DATA)
                .catchInterruptedException()
                .subscribeOn(scheduler)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ data -> postResult { v -> v.onDataLoaded(data) } })
    }

    fun processItem(entity: AwesomeEntity) {
        ProcessEntityModel.createObservable(entity)
                .map {
                    //Wrap above view helps to interact with view synchronously with current thread
                    val viewWidth = waitForView().getViewWidth()
                    ((entity.importantDataField.toFloat() * 1000f) / viewWidth.toFloat()).toInt()
                }
                .withId(TASK_PROCESS)
                .catchInterruptedException()
                .subscribeOn(scheduler)
                .subscribe({ result -> postResult { v -> v.onItemProcessed(result) } })
    }

    interface PresenterListener : ITaskListener {

        fun getViewWidth(): Int

        fun onDataLoaded(data: ArrayList<AwesomeEntity>)

        fun onItemProcessed(result: Int)

    }

}