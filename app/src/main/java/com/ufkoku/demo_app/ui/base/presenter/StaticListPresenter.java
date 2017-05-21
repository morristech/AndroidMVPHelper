package com.ufkoku.demo_app.ui.base.presenter;

import com.ufkoku.demo_app.entity.AwesomeEntity;
import com.ufkoku.demo_app.model.PageEntityModel;
import com.ufkoku.mvp.presenter.rx2.BaseAsyncRxSchedulerPresenter;
import com.ufkoku.mvp_base.view.IMvpView;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;

public class StaticListPresenter<V extends StaticListPresenter.PresenterListener & IMvpView> extends BaseAsyncRxSchedulerPresenter<V> {

    public static Integer TASK_FETCH_DATA = 1;

    @NotNull
    @Override
    protected ExecutorService createExecutor() {
        return new ScheduledThreadPoolExecutor(1);
    }

    public void fetchData() {
        notifyTaskAdded(TASK_FETCH_DATA);

        PageEntityModel.createPageObservable(0)
                .subscribeOn(getScheduler())
                .subscribe(
                        page -> waitForViewIfNeeded().onDataLoaded(page.getData()),
                        throwable -> notifyTaskFinished(TASK_FETCH_DATA),
                        () -> notifyTaskFinished(TASK_FETCH_DATA)
                );
    }

    public interface PresenterListener extends ITaskListener {

        void onDataLoaded(List<AwesomeEntity> data);

    }

}