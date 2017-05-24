package com.ufkoku.mvp_base.lifecycle

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class OnActivityMethod(@Activity.ActivityMethod val method: Int)