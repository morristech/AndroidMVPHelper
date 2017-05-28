package com.ufkoku.demo_app.model;

import com.ufkoku.demo_app.entity.AwesomeEntity;

import java.util.Random;

import io.reactivex.Observable;

public class SingleEntityModel {

    public static Observable<AwesomeEntity> createEntityObservable() {
        return Observable.create(emitter -> {
            Random random = new Random();

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                if (!emitter.isDisposed()) {
                    emitter.onError(e);
                }
                return;
            }

            emitter.onNext(new AwesomeEntity(random.nextInt()));
            emitter.onComplete();
        });
    }

}
