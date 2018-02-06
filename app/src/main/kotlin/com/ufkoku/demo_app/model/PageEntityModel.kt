package com.ufkoku.demo_app.model

import com.ufkoku.demo_app.entity.AwesomeEntity
import io.reactivex.Observable
import io.reactivex.functions.BiConsumer
import java.util.*

object PageEntityModel {

    private val LIMIT = 20

    fun createPageObservable(offset: Int): Observable<ArrayList<AwesomeEntity>> {
        return Observable.range(offset, LIMIT)
                .map { AwesomeEntity(it) }
                .collectInto(ArrayList(), BiConsumer<ArrayList<AwesomeEntity>, AwesomeEntity> { obj, e -> obj.add(e) })
                .toObservable()
                .doOnNext { Thread.sleep(2000) }
    }

}
