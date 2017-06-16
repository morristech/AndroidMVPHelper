package com.ufkoku.mvp.utils.view_injection.annotation

import android.support.annotation.IdRes

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FIELD)
annotation class InjectView(@IdRes val value: Int)