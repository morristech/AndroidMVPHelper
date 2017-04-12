package com.ufkoku.demo_app.ui.base.presenter;

import com.ufkoku.demo_app.entity.AwesomeEntity;
import com.ufkoku.demo_app.entity.PagingResponse;
import com.ufkoku.demo_app.model.PageEntityModel;
import com.ufkoku.mvp.presenter.rx.BaseAsyncRxSchedulerPresenter;
import com.ufkoku.mvp_base.view.IMvpView;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import rx.Subscriber;

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

                .subscribe(new Subscriber<PagingResponse<AwesomeEntity>>() {
                    @Override
                    public void onCompleted() {
                        waitForViewIfNeeded();
                        notifyTaskFinished(TASK_FETCH_DATA);
                    }

                    @Override
                    public void onError(Throwable e) {
                        waitForViewIfNeeded();
                        notifyTaskFinished(TASK_FETCH_DATA);
                    }

                    @Override
                    public void onNext(PagingResponse<AwesomeEntity> entity) {
                        waitForViewIfNeeded();
                        V activity = getView();
                        if (activity != null) {
                            activity.onDataLoaded(entity.getData());
                        }
                    }
                });
    }

    public interface PresenterListener extends ITaskListener {

        void onDataLoaded(List<AwesomeEntity> data);

    }

}
