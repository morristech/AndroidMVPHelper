package com.ufkoku.demo_app.ui.common.paging;

import com.ufkoku.demo_app.entity.AwesomeEntity;
import com.ufkoku.demo_app.entity.PagingResponse;
import com.ufkoku.demo_app.model.PageEntityModel;
import com.ufkoku.demo_app.model.ProcessEntityModel;
import com.ufkoku.demo_app.model.SingleEntityModel;
import com.ufkoku.mvp.list.interfaces.IPagingSearchablePresenter;
import com.ufkoku.mvp.presenter.rx2.BaseAsyncRxSchedulerPresenter;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;

import io.reactivex.Observable;
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
    protected ThreadPoolExecutor createExecutor() {
        return new ScheduledThreadPoolExecutor(1);
    }

    @Override
    public void cancel() {
        cancelAllPageRequests();
        super.cancel();
    }

    @SuppressWarnings({"ConstantConditions"})
    public void getInitData() {
        Observable<String> observable = SingleEntityModel.createEntityObservable()
                .map(awesomeEntity -> awesomeEntity.getImportantDataField() + "");

        execute(
                observable,
                TASK_LOAD_INIT_DATA,
                value -> waitForViewIfNeeded().onInitDataLoaded(value),
                throwable -> waitForViewIfNeeded().onInitDataLoadFailed(0),
                null,
                false
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

        Disposable disposable = execute(
                PageEntityModel.createPageObservable(offset),
                taskId,
                page -> {
                    if (taskId == TASK_LOAD_FIRST_PAGE) {
                        waitForViewIfNeeded().onFirstPageLoaded(page);
                    } else {
                        waitForViewIfNeeded().onNextPageLoaded(page);
                    }
                },
                throwable -> {
                    if (taskId == TASK_LOAD_FIRST_PAGE) {
                        firstPageSub = null;
                    } else {
                        nextPageSub = null;
                    }

                    if (!checkIfInterruptedException(throwable)) {
                        if (taskId == TASK_LOAD_FIRST_PAGE) {
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
                },
                true);

        if (offset == 0) {
            firstPageSub = disposable;
        } else {
            nextPageSub = disposable;
        }
    }

    @SuppressWarnings({"ConstantConditions"})
    public void processPickedItem(final AwesomeEntity entity) {
        execute(ProcessEntityModel.createObservable(entity),
                TASK_PROCESS_PICKED_DATA,
                awesomeEntity -> waitForViewIfNeeded().onPickedItemProcessed(awesomeEntity),
                null,
                null,
                false);
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
        }
    }

    @Override
    public void cancelNextPages() {
        if (nextPageSub != null && !nextPageSub.isDisposed()) {
            nextPageSub.dispose();
            nextPageSub = null;
        }
    }

    @Override
    public void cancelAllPageRequests() {
        cancelFirstPages();
        cancelNextPages();
    }

}