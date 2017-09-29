package com.ufkoku.demo_app.ui.common.presenter;

import com.ufkoku.demo_app.entity.AwesomeEntity;
import com.ufkoku.demo_app.model.PageEntityModel;
import com.ufkoku.mvp.presenter.rx2.BaseAsyncRxSchedulerPresenter;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;

public class StaticListPresenter<V extends StaticListPresenter.PresenterListener> extends BaseAsyncRxSchedulerPresenter<V> {

    public static Integer TASK_FETCH_DATA = 1;

    @NotNull
    @Override
    protected ThreadPoolExecutor createExecutor() {
        return new ScheduledThreadPoolExecutor(1);
    }

    public void fetchData() {
        execute(
                PageEntityModel.createPageObservable(0),
                TASK_FETCH_DATA,
                page -> waitForViewIfNeeded().onDataLoaded(page.getData()),
                null,
                null,
                false
        );
    }

    public interface PresenterListener extends ITaskListener {

        void onDataLoaded(List<AwesomeEntity> data);

    }

}