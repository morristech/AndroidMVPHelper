package com.ufkoku.demo_app.ui.base.listeners;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.ufkoku.mvp_base.view.lifecycle.ActivityLifecycle;

@SuppressLint("LongLogTag")
public class ActivityLifecycleObserver {

    private static final String TAG = "ActivityLifecycleObserver";

    @ActivityLifecycle.OnLifecycleEvent(event = ActivityLifecycle.ON_CREATE)
    public void onCreate() {
        Log.d(TAG, "onCreate, no args");
    }

    @ActivityLifecycle.OnLifecycleEvent(event = ActivityLifecycle.ON_CREATE)
    public void onCreate(Activity activity) {
        Log.d(TAG, "onCreate, with activity " + activity);
    }

    @ActivityLifecycle.OnLifecycleEvent(event = ActivityLifecycle.ON_CREATE)
    public void onCreate(Activity activity, Bundle savedInstance) {
        Log.d(TAG, "onCreate, with activity " + activity + " and instance " + savedInstance);
    }

    @ActivityLifecycle.OnLifecycleEvent(event = ActivityLifecycle.ON_START)
    public void onStart() {
        Log.d(TAG, "onStart, no args");
    }

    @ActivityLifecycle.OnLifecycleEvent(event = ActivityLifecycle.ON_START)
    public void onStart(Activity activity) {
        Log.d(TAG, "onStart, with activity" + activity);
    }

    @ActivityLifecycle.OnLifecycleEvent(event = ActivityLifecycle.ON_RESUME)
    public void onResume() {
        Log.d(TAG, "onResume, no args");
    }

    @ActivityLifecycle.OnLifecycleEvent(event = ActivityLifecycle.ON_RESUME)
    public void onResume(Activity activity) {
        Log.d(TAG, "onResume, with activity" + activity);
    }

    @ActivityLifecycle.OnLifecycleEvent(event = ActivityLifecycle.ON_PAUSE)
    public void onPause() {
        Log.d(TAG, "onPause, no args");
    }

    @ActivityLifecycle.OnLifecycleEvent(event = ActivityLifecycle.ON_PAUSE)
    public void onPause(Activity activity) {
        Log.d(TAG, "onPause, with activity" + activity);
    }

    @ActivityLifecycle.OnLifecycleEvent(event = ActivityLifecycle.ON_STOP)
    public void onStop() {
        Log.d(TAG, "onStop, no args");
    }

    @ActivityLifecycle.OnLifecycleEvent(event = ActivityLifecycle.ON_STOP)
    public void onStop(Activity activity) {
        Log.d(TAG, "onStop, with activity" + activity);
    }

    @ActivityLifecycle.OnLifecycleEvent(event = ActivityLifecycle.ON_SAVE_INSTANCE)
    public void onSaveInstance() {
        Log.d(TAG, "onSaveInstance, no args");
    }

    @ActivityLifecycle.OnLifecycleEvent(event = ActivityLifecycle.ON_SAVE_INSTANCE)
    public void onSaveInstance(Activity activity) {
        Log.d(TAG, "onSaveInstance, with activity" + activity);
    }

    @ActivityLifecycle.OnLifecycleEvent(event = ActivityLifecycle.ON_SAVE_INSTANCE)
    public void onSaveInstance(Activity activity, Bundle savedInstance) {
        Log.d(TAG, "onSaveInstance, with activity" + activity + " and bundle " + savedInstance);
    }

    @ActivityLifecycle.OnLifecycleEvent(event = ActivityLifecycle.ON_DESTROY)
    public void onDestroy() {
        Log.d(TAG, "onDestroy, no args");
    }

    @ActivityLifecycle.OnLifecycleEvent(event = ActivityLifecycle.ON_DESTROY)
    public void onDestroy(Activity activity) {
        Log.d(TAG, "onDestroy, with activity" + activity);
    }

}
