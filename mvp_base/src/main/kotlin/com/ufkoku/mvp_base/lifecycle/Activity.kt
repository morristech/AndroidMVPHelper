package com.ufkoku.mvp_base.lifecycle

import android.support.annotation.IntDef

object Activity {

    const val ON_CREATED = 0
    const val ON_STARTED = 1
    const val ON_RESUMED = 2
    const val ON_INSTANCE_SAVED = 3
    const val ON_PAUSED = 4
    const val ON_STOPPED = 5
    const val ON_DESTROYED = 6

    @Target(AnnotationTarget.VALUE_PARAMETER)
    @Retention(AnnotationRetention.SOURCE)
    @IntDef(ON_CREATED.toLong(), ON_STARTED.toLong(), ON_RESUMED.toLong(), ON_INSTANCE_SAVED.toLong(), ON_PAUSED.toLong(), ON_STOPPED.toLong(), ON_DESTROYED.toLong())
    annotation class ActivityMethod

}