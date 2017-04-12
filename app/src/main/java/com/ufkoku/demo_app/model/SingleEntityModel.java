package com.ufkoku.demo_app.model;

import com.ufkoku.demo_app.entity.AwesomeEntity;

import java.util.Random;

import rx.Observable;
import rx.Observer;
import rx.observables.SyncOnSubscribe;

public class SingleEntityModel {

    public static Observable<AwesomeEntity> createEntityObservable() {
        return Observable.create(new SyncOnSubscribe<Random, AwesomeEntity>() {

            @Override
            protected Random generateState() {
                return new Random();
            }

            @Override
            protected Random next(Random state, Observer<? super AwesomeEntity> observer) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                observer.onNext(new AwesomeEntity(state.nextInt()));
                observer.onCompleted();

                return state;
            }

        });

    }

}
