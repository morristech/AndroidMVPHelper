package com.ufkoku.demo_app.model

import com.ufkoku.demo_app.entity.AwesomeEntity

import io.reactivex.Observable

object ProcessEntityModel {

    fun createObservable(entity: AwesomeEntity): Observable<AwesomeEntity> {
        return Observable.just(entity)
                .doOnNext { Thread.sleep(2000) }
    }

}
