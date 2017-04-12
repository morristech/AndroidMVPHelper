package com.ufkoku.demo_app.model;

import com.ufkoku.demo_app.entity.AwesomeEntity;

import rx.Observable;
import rx.Observer;
import rx.observables.SyncOnSubscribe;

public class ProcessEntityModel {

    public static Observable<AwesomeEntity> createObservable(final AwesomeEntity entity) {
        return Observable.create(new SyncOnSubscribe<Void, AwesomeEntity>() {

            @Override
            protected Void generateState() {
                return null;
            }

            @Override
            protected Void next(Void state, Observer<? super AwesomeEntity> observer) {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                observer.onNext(entity);
                observer.onCompleted();

                return state;
            }

        });

    }

}
