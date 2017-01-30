package com.ufkoku.demo_app.ui.awesome_entity_paging;

import android.support.annotation.NonNull;

import com.ufkoku.demo_app.entity.AwesomeEntity;
import com.ufkoku.demo_app.entity.PagingResponse;
import com.ufkoku.mvp.list.interfaces.IPagingSearchablePresenter;
import com.ufkoku.mvp.presenter.rx.BaseAsyncRxPresenter;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

public class PagingPresenter extends BaseAsyncRxPresenter<IPagingView> implements IPagingSearchablePresenter {

    public static final int TASK_LOAD_INIT_DATA = 0;
    public static final int TASK_LOAD_FIRST_PAGE = 1;
    public static final int TASK_LOAD_NEXT_PAGE = 2;
    public static final int TASK_PROCESS_PICKED_DATA = 3;

    protected static final int LIMIT = 20;

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

        Observable.create(new UiWaitingOnSubscribe<String>(this) {
            @Override
            public void call(UiWaitingOnSubscriber<String> subscriber) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    subscriber.onError(e);
                }
                subscriber.onNext("InitDataString");
                subscriber.onCompleted();
            }
        })

                .subscribeOn(getScheduler())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<String>() {
                    @Override
                    public void onCompleted() {
                        notifyTaskFinished(TASK_LOAD_INIT_DATA);
                    }

                    @Override
                    public void onError(Throwable e) {
                        notifyTaskFinished(TASK_LOAD_INIT_DATA);
                        e.printStackTrace();
                        IPagingView fragment = getView();
                        if (fragment != null) {
                            fragment.onInitDataLoadFailed(0);
                        }
                    }

                    @Override
                    public void onNext(String s) {
                        IPagingView fragment = getView();
                        if (fragment != null) {
                            fragment.onInitDataLoaded(s);
                        }
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

        Subscription subscription = Observable.create(new UiWaitingOnSubscribe<PagingResponse<AwesomeEntity>>(this) {
            @Override
            public void call(@NonNull UiWaitingOnSubscriber<PagingResponse<AwesomeEntity>> subscriber) {
                List<AwesomeEntity> data = new ArrayList<>();
                for (int i = offset; i < offset + LIMIT; i++) {
                    data.add(new AwesomeEntity(i));
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    subscriber.onError(e);
                }
                subscriber.onNext(new PagingResponse<>(data, offset + LIMIT <= LIMIT * 2));
                subscriber.onCompleted();
            }
        })

                .subscribeOn(getScheduler())
                .observeOn(AndroidSchedulers.mainThread())

                .subscribe(new Subscriber<PagingResponse<AwesomeEntity>>() {
                    @Override
                    public void onCompleted() {
                        if (taskId == TASK_LOAD_FIRST_PAGE){
                            firstPageSub = null;
                        } else {
                            nextPageSub = null;
                        }

                        notifyTaskFinished(taskId);
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (taskId == TASK_LOAD_FIRST_PAGE){
                            firstPageSub = null;
                        } else {
                            nextPageSub = null;
                        }

                        notifyTaskFinished(taskId);

                        e.printStackTrace();
                        IPagingView fragment = getView();
                        if (fragment != null) {
                            if (offset == 0) {
                                fragment.onFirstPageLoadFailed(0);
                            } else {
                                fragment.onNextPageLoadFailed(0);
                            }
                        }
                    }

                    @Override
                    public void onNext(PagingResponse<AwesomeEntity> response) {
                        IPagingView fragment = getView();
                        if (fragment != null) {
                            if (offset == 0) {
                                fragment.onFirstPageLoaded(response);
                            } else {
                                fragment.onNextPageLoaded(response);
                            }
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

        Observable.create(new UiWaitingOnSubscribe<AwesomeEntity>(this) {
            @Override
            public void call(@NotNull UiWaitingOnSubscriber<AwesomeEntity> subscriber) {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                subscriber.onNext(entity);
                subscriber.onCompleted();
            }
        })

                .subscribeOn(getScheduler())
                .observeOn(AndroidSchedulers.mainThread())

                .subscribe(new Subscriber<AwesomeEntity>() {
                    @Override
                    public void onCompleted() {
                        notifyTaskFinished(TASK_PROCESS_PICKED_DATA);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(AwesomeEntity entity) {
                        IPagingView view = getView();
                        if (view != null) {
                            view.onPickedItemProcessed(entity);
                        }
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
