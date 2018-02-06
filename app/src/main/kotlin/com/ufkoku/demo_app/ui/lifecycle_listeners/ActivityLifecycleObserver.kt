package com.ufkoku.demo_app.ui.lifecycle_listeners

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.util.Log

import com.ufkoku.mvp_base.view.lifecycle.ActivityLifecycle

@SuppressLint("LongLogTag")
class ActivityLifecycleObserver {

    companion object {
        private val TAG = "ActivityLifecycleObserver"
    }

    @ActivityLifecycle.OnLifecycleEvent(event = ActivityLifecycle.ON_CREATE)
    fun onCreate() {
        Log.d(TAG, "onCreate, no args")
    }

    @ActivityLifecycle.OnLifecycleEvent(event = ActivityLifecycle.ON_CREATE)
    fun onCreate(activity: Activity) {
        Log.d(TAG, "onCreate, with activity " + activity)
    }

    @ActivityLifecycle.OnLifecycleEvent(event = ActivityLifecycle.ON_CREATE)
    fun onCreate(activity: Activity, savedInstance: Bundle?) {
        Log.d(TAG, "onCreate, with activity $activity and instance $savedInstance")
    }

    @ActivityLifecycle.OnLifecycleEvent(event = ActivityLifecycle.ON_START)
    fun onStart() {
        Log.d(TAG, "onStart, no args")
    }

    @ActivityLifecycle.OnLifecycleEvent(event = ActivityLifecycle.ON_START)
    fun onStart(activity: Activity) {
        Log.d(TAG, "onStart, with activity" + activity)
    }

    @ActivityLifecycle.OnLifecycleEvent(event = ActivityLifecycle.ON_RESUME)
    fun onResume() {
        Log.d(TAG, "onResume, no args")
    }

    @ActivityLifecycle.OnLifecycleEvent(event = ActivityLifecycle.ON_RESUME)
    fun onResume(activity: Activity) {
        Log.d(TAG, "onResume, with activity" + activity)
    }

    @ActivityLifecycle.OnLifecycleEvent(event = ActivityLifecycle.ON_PAUSE)
    fun onPause() {
        Log.d(TAG, "onPause, no args")
    }

    @ActivityLifecycle.OnLifecycleEvent(event = ActivityLifecycle.ON_PAUSE)
    fun onPause(activity: Activity) {
        Log.d(TAG, "onPause, with activity" + activity)
    }

    @ActivityLifecycle.OnLifecycleEvent(event = ActivityLifecycle.ON_STOP)
    fun onStop() {
        Log.d(TAG, "onStop, no args")
    }

    @ActivityLifecycle.OnLifecycleEvent(event = ActivityLifecycle.ON_STOP)
    fun onStop(activity: Activity) {
        Log.d(TAG, "onStop, with activity" + activity)
    }

    @ActivityLifecycle.OnLifecycleEvent(event = ActivityLifecycle.ON_SAVE_INSTANCE)
    fun onSaveInstance() {
        Log.d(TAG, "onSaveInstance, no args")
    }

    @ActivityLifecycle.OnLifecycleEvent(event = ActivityLifecycle.ON_SAVE_INSTANCE)
    fun onSaveInstance(activity: Activity) {
        Log.d(TAG, "onSaveInstance, with activity" + activity)
    }

    @ActivityLifecycle.OnLifecycleEvent(event = ActivityLifecycle.ON_SAVE_INSTANCE)
    fun onSaveInstance(activity: Activity, savedInstance: Bundle?) {
        Log.d(TAG, "onSaveInstance, with activity$activity and bundle $savedInstance")
    }

    @ActivityLifecycle.OnLifecycleEvent(event = ActivityLifecycle.ON_DESTROY)
    fun onDestroy() {
        Log.d(TAG, "onDestroy, no args")
    }

    @ActivityLifecycle.OnLifecycleEvent(event = ActivityLifecycle.ON_DESTROY)
    fun onDestroy(activity: Activity) {
        Log.d(TAG, "onDestroy, with activity" + activity)
    }

}
