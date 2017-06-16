package com.ufkoku.mvp.utils.view_injection.annotation

import android.support.annotation.LayoutRes
import java.lang.annotation.Inherited

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
@Inherited
annotation class Layout(@LayoutRes val value: Int)