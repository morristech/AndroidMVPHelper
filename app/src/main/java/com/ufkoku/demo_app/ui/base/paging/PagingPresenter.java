package com.ufkoku.demo_app.ui.base.paging;

import com.ufkoku.demo_app.entity.AwesomeEntity;
import com.ufkoku.demo_app.model.PageEntityModel;
import com.ufkoku.demo_app.model.ProcessEntityModel;
import com.ufkoku.demo_app.model.SingleEntityModel;
import com.ufkoku.mvp.list.interfaces.IPagingSearchablePresenter;
import com.ufkoku.mvp.presenter.rx2.BaseAsyncRxSchedulerPresenter;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import io.reactivex.disposables.Disposable;

public class PagingPresenter extends BaseAsyncRxSchedulerPresenter<IPagingView> implements IPagingSearchablePresenter {

    public static final int TASK_LOAD_INIT_DATA = 0;
    public static final int TASK_LOAD_FIRST_PAGE = 1;
    public static final int TASK_LOAD_NEXT_PAGE = 2;
    public static final int TASK_PROCESS_PICKED_DATA = 3;

    private Disposable firstPageSub = null;
    private Disposable nextPageSub = null;

    @NotNull
    @Override
    protected ExecutorService createExecutor() {
        return new ScheduledThreadPoolExecutor(1);
    }

    @Override
    public void cancel() {
        cancelAllPageRequests();
        super.cancel();
    }

    @SuppressWarnings({"ConstantConditions"})
    public void getInitData() {
        notifyTaskAdded(TASK_LOAD_INIT_DATA);

        SingleEntityModel.createEntityObservable()
                .map(awesomeEntity -> awesomeEntity.getImportantDataField() + "")
                .subscribeOn(getScheduler())
                .subscribe(
                        value -> waitForViewIfNeeded().onInitDataLoaded(value),
                        throwable -> {
                            notifyTaskFinished(TASK_LOAD_INIT_DATA);
                            if (!checkIfInterruptedException(throwable)) {
                                waitForViewIfNeeded().onInitDataLoadFailed(0);
                            }
                        },
                        () -> notifyTaskFinished(TASK_LOAD_INIT_DATA)
                );
    }

    @SuppressWarnings({"ConstantConditions"})
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

        Disposable disposable = PageEntityModel.createPageObservable(offset)
                .subscribeOn(getScheduler())
                .subscribe(
                        page -> {
                            if (offset == 0) {
                                waitForViewIfNeeded().onFirstPageLoaded(page);
                            } else {
                                waitForViewIfNeeded().onNextPageLoaded(page);
                            }
                        },
                        throwable -> {
                            throwable.printStackTrace();

                            notifyTaskFinished(taskId);

                            if (taskId == TASK_LOAD_FIRST_PAGE) {
                                firstPageSub = null;
                            } else {
                                nextPageSub = null;
                            }

                            if (!checkIfInterruptedException(throwable)) {
                                if (offset == 0) {
                                    waitForViewIfNeeded().onFirstPageLoadFailed(0);
                                } else {
                                    waitForViewIfNeeded().onNextPageLoadFailed(0);
                                }
                            }
                        },
                        () -> {
                            if (taskId == TASK_LOAD_FIRST_PAGE) {
                                firstPageSub = null;
                            } else {
                                nextPageSub = null;
                            }
                            notifyTaskFinished(taskId);
                        }
                );

        if (offset == 0) {
            firstPageSub = disposable;
        } else {
            nextPageSub = disposable;
        }
    }

    @SuppressWarnings({"ConstantConditions"})
    public void processPickedItem(final AwesomeEntity entity) {
        notifyTaskAdded(TASK_PROCESS_PICKED_DATA);
        ProcessEntityModel.createObservable(entity)
                .subscribeOn(getScheduler())
                .subscribe(
                        awesomeEntity -> waitForViewIfNeeded().onPickedItemProcessed(awesomeEntity),
                        throwable -> notifyTaskFinished(TASK_PROCESS_PICKED_DATA),
                        () -> notifyTaskFinished(TASK_PROCESS_PICKED_DATA)
                );
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
        if (firstPageSub != null && !firstPageSub.isDisposed()) {
            firstPageSub.dispose();
            firstPageSub = null;
            notifyTaskFinished(TASK_LOAD_FIRST_PAGE);
        }
    }

    @Override
    public void cancelNextPages() {
        if (nextPageSub != null && !nextPageSub.isDisposed()) {
            nextPageSub.dispose();
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