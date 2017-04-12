package com.ufkoku.demo_app.model;

import com.ufkoku.demo_app.entity.AwesomeEntity;
import com.ufkoku.demo_app.entity.PagingResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import rx.Observable;
import rx.Observer;
import rx.observables.SyncOnSubscribe;

public class PageEntityModel {

    public static final int LIMIT = 20;

    public static Observable<PagingResponse<AwesomeEntity>> createPageObservable(final int offset) {
        return Observable.create(new SyncOnSubscribe<Random, PagingResponse<AwesomeEntity>>() {

            @Override
            protected Random generateState() {
                return new Random();
            }

            @Override
            protected Random next(Random state, Observer<? super PagingResponse<AwesomeEntity>> observer) {
                List<AwesomeEntity> data = new ArrayList<>();
                for (int i = offset; i < offset + LIMIT; i++) {
                    data.add(new AwesomeEntity(i));
                }

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    observer.onError(e);
                }

                observer.onNext(new PagingResponse<>(data, offset + LIMIT <= LIMIT * 2));
                observer.onCompleted();

                return state;
            }

        });

    }

}
