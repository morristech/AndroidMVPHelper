package com.ufkoku.demo_app.ui.base.paging;

import com.ufkoku.demo_app.entity.AwesomeEntity;
import com.ufkoku.demo_app.entity.PagingResponse;
import com.ufkoku.demo_app.model.PageEntityModel;
import com.ufkoku.demo_app.model.ProcessEntityModel;
import com.ufkoku.demo_app.model.SingleEntityModel;
import com.ufkoku.mvp.list.interfaces.IPagingSearchablePresenter;
import com.ufkoku.mvp.presenter.rx.BaseAsyncRxSchedulerPresenter;
import com.ufkoku.mvp.presenter.rx.EnhancedSubscriber;
import com.ufkoku.mvp_base.view.IMvpView;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import rx.Subscriber;
import rx.Subscription;

public class PagingPresenter extends BaseAsyncRxSchedulerPresenter<IPagingView> implements IPagingSearchablePresenter {

    public static final int TASK_LOAD_INIT_DATA = 0;
    public static final int TASK_LOAD_FIRST_PAGE = 1;
    public static final int TASK_LOAD_NEXT_PAGE = 2;
    public static final int TASK_PROCESS_PICKED_DATA = 3;

    private Subscription firstPageSub = null;
    private Subscription nextPageSub = null;

    @NotNull
    @Override
    protected ExecutorService createExecutor() {
        return new ScheduledThreadPoolExecutor(1);
    }

    @Override
    public void cancel() {
        super.cancel();
        cancelAllPageRequests();
    }

    public void getInitData() {
        notifyTaskAdded(TASK_LOAD_INIT_DATA);

        SingleEntityModel.createEntityObservable()
                .map(awesomeEntity -> awesomeEntity.getImportantDataField() + "")
                .subscribeOn(getScheduler())
                .subscribe(new EnhancedSubscriber<String, IPagingView>(this) {

                    @Override
                    public void onCompleted(@NotNull IPagingView view) {
                        notifyTaskFinished(TASK_LOAD_INIT_DATA);
                    }

                    @Override
                    public void onError(@NotNull Throwable e, @NotNull IPagingView view) {
                        e.printStackTrace();

                        notifyTaskFinished(TASK_LOAD_INIT_DATA);

                        view.onInitDataLoadFailed(0);
                    }

                    @Override
                    public void onInterruptedError(@NotNull Throwable e) {
                        notifyTaskFinished(TASK_LOAD_INIT_DATA);
                    }

                    @Override
                    public void onNext(String value, @NotNull IPagingView view) {
                        view.onInitDataLoaded(value);
                    }

                });
    }

    public void getContent(final int offset) {
        final int taskId;

        if (offset == 0) {
            cancelFirstPages();
            taskId = TASK_LOAD_FIRST_PAGE;
        } else {
            cancelNextPages();
            taskId = TASK_LOAD_NEXT_PAGE;
        }

        notifyTaskAdded(taskId);

        Subscription subscription = PageEntityModel.createPageObservable(offset)
                .subscribeOn(getScheduler())
                .subscribe(new EnhancedSubscriber<PagingResponse<AwesomeEntity>, IPagingView>(this) {

                    @Override
                    public void onCompleted(@NotNull IPagingView view) {
                        if (taskId == TASK_LOAD_FIRST_PAGE) {
                            firstPageSub = null;
                        } else {
                            nextPageSub = null;
                        }

                        notifyTaskFinished(taskId);
                    }

                    @Override
                    public void onError(@NotNull Throwable e, @NotNull IPagingView view) {
                        e.printStackTrace();

                        notifyTaskFinished(taskId);

                        if (taskId == TASK_LOAD_FIRST_PAGE) {
                            firstPageSub = null;
                        } else {
                            nextPageSub = null;
                        }

                        if (offset == 0) {
                            view.onFirstPageLoadFailed(0);
                        } else {
                            view.onNextPageLoadFailed(0);
                        }
                    }

                    @Override
                    public void onInterruptedError(@NotNull Throwable e) {
                        notifyTaskFinished(taskId);
                    }

                    @Override
                    public void onNext(PagingResponse<AwesomeEntity> value, @NotNull IPagingView view) {
                        if (offset == 0) {
                            view.onFirstPageLoaded(value);
                        } else {
                            view.onNextPageLoaded(value);
                        }
                    }

                });

        if (offset == 0) {
            firstPageSub = subscription;
        } else {
            nextPageSub = subscription;
        }
    }

    public void processPickedItem(final AwesomeEntity entity) {
        notifyTaskAdded(TASK_PROCESS_PICKED_DATA);
        ProcessEntityModel.createObservable(entity)
                .subscribeOn(getScheduler())
                .subscribe(new EnhancedSubscriber<AwesomeEntity, IPagingView>(this) {

                    @Override
                    public void onCompleted(@NotNull IPagingView view) {
                        notifyTaskFinished(TASK_PROCESS_PICKED_DATA);
                    }

                    @Override
                    public void onError(@NotNull Throwable e, @NotNull IPagingView view) {
                        notifyTaskFinished(TASK_PROCESS_PICKED_DATA);
                    }

                    @Override
                    public void onInterruptedError(@NotNull Throwable e) {
                        notifyTaskFinished(TASK_PROCESS_PICKED_DATA);
                    }

                    @Override
                    public void onNext(AwesomeEntity value, @NotNull IPagingView view) {
                        view.onPickedItemProcessed(value);
                    }

                });
    }

    @Override
    public boolean isFirstPageLoading() {
        return isTaskRunning(TASK_LOAD_FIRST_PAGE);
    }

    @Override
    public boolean isNextPageLoading() {
        return isTaskRunning(TASK_LOAD_NEXT_PAGE);
    }

    @Override
    public void cancelFirstPages() {
        if (firstPageSub != null && !firstPageSub.isUnsubscribed()) {
            firstPageSub.unsubscribe();
            firstPageSub = null;
            notifyTaskFinished(TASK_LOAD_FIRST_PAGE);
        }
    }

    @Override
    public void cancelNextPages() {
        if (nextPageSub != null && !nextPageSub.isUnsubscribed()) {
            nextPageSub.unsubscribe();
            nextPageSub = null;
            notifyTaskFinished(TASK_LOAD_NEXT_PAGE);
        }
    }

    @Override
    public void cancelAllPageRequests() {
        cancelFirstPages();
        cancelNextPages();
    }

}