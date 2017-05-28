package com.ufkoku.demo_app.model;

import com.ufkoku.demo_app.entity.AwesomeEntity;
import com.ufkoku.demo_app.entity.PagingResponse;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;

public class PageEntityModel {

    public static final int LIMIT = 20;

    public static Observable<PagingResponse<AwesomeEntity>> createPageObservable(final int offset) {
        return Observable.create(emitter -> {
            List<AwesomeEntity> data = new ArrayList<>();
            for (int i = offset; i < offset + LIMIT; i++) {
                data.add(new AwesomeEntity(i));
            }

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                if (!emitter.isDisposed()) {
                    emitter.onError(e);
                }
                return;
            }

            emitter.onNext(new PagingResponse<>(data, offset + LIMIT <= LIMIT * 2));
            emitter.onComplete();
        });
    }

}
