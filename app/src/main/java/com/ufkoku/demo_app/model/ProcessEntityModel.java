package com.ufkoku.demo_app.model;

import com.ufkoku.demo_app.entity.AwesomeEntity;

import io.reactivex.Observable;

public class ProcessEntityModel {

    public static Observable<AwesomeEntity> createObservable(final AwesomeEntity entity) {
        return Observable.create(emitter -> {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                if (!emitter.isDisposed()) {
                    emitter.onError(e);
                }
                return;
            }
            emitter.onNext(entity);
            emitter.onComplete();
        });
    }

}
