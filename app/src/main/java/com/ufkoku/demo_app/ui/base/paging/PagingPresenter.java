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
                .subscribe(new EnhancedSubscriber<String>(this) {

                    @Override
                    public void onCompletedImpl() {
                        notifyTaskFinished(TASK_LOAD_INIT_DATA);
                    }

                    @Override
                    public void onErrorImpl(@NotNull Throwable e) {
                        e.printStackTrace();

                        notifyTaskFinished(TASK_LOAD_INIT_DATA);

                        waitForViewIfNeeded().onInitDataLoadFailed(0);
                    }

                    @Override
                    public void onInterruptedErrorImpl(@NotNull Throwable e) {
                        notifyTaskFinished(TASK_LOAD_INIT_DATA);
                    }

                    @Override
                    public void onNextImpl(String value) {
                        waitForViewIfNeeded().onInitDataLoaded(value);
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
                .subscribe(new EnhancedSubscriber<PagingResponse<AwesomeEntity>>(this) {

                    @Override
                    public void onCompletedImpl() {
                        if (taskId == TASK_LOAD_FIRST_PAGE) {
                            firstPageSub = null;
                        } else {
                            nextPageSub = null;
                        }

                        notifyTaskFinished(taskId);
                    }

                    @Override
                    public void onErrorImpl(@NotNull Throwable e) {
                        e.printStackTrace();

                        notifyTaskFinished(taskId);

                        if (taskId == TASK_LOAD_FIRST_PAGE) {
                            firstPageSub = null;
                        } else {
                            nextPageSub = null;
                        }

                        if (offset == 0) {
                            waitForViewIfNeeded().onFirstPageLoadFailed(0);
                        } else {
                            waitForViewIfNeeded().onNextPageLoadFailed(0);
                        }
                    }

                    @Override
                    public void onInterruptedErrorImpl(@NotNull Throwable e) {
                        notifyTaskFinished(taskId);
                    }

                    @Override
                    public void onNextImpl(PagingResponse<AwesomeEntity> value) {
                        if (offset == 0) {
                            waitForViewIfNeeded().onFirstPageLoaded(value);
                        } else {
                            waitForViewIfNeeded().onNextPageLoaded(value);
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
                .subscribe(new EnhancedSubscriber<AwesomeEntity>(this) {

                    @Override
                    public void onCompletedImpl() {
                        notifyTaskFinished(TASK_PROCESS_PICKED_DATA);
                    }

                    @Override
                    public void onErrorImpl(@NotNull Throwable e) {
                        notifyTaskFinished(TASK_PROCESS_PICKED_DATA);
                    }

                    @Override
                    public void onInterruptedErrorImpl(@NotNull Throwable e) {
                        notifyTaskFinished(TASK_PROCESS_PICKED_DATA);
                    }

                    @Override
                    public void onNextImpl(AwesomeEntity value) {
                        waitForViewIfNeeded().onPickedItemProcessed(value);
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