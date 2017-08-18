package com.ufkoku.demo_app.ui.lifecycle_listeners;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;

import com.ufkoku.mvp_base.view.lifecycle.FragmentLifecycle;

@SuppressLint("LongLogTag")
public class FragmentLifecycleObserver {

    private static final String TAG = "FragmentLifecycleObserver";

    @FragmentLifecycle.OnLifecycleEvent(event = FragmentLifecycle.ON_ATTACH)
    public void onAttach() {
        Log.d(TAG, "onAttach, no args");
    }

    @FragmentLifecycle.OnLifecycleEvent(event = FragmentLifecycle.ON_ATTACH)
    public void onAttach(Fragment fragment) {
        Log.d(TAG, "onAttach, with fragment " + fragment);
    }

    @FragmentLifecycle.OnLifecycleEvent(event = FragmentLifecycle.ON_ATTACH)
    public void onAttach(Fragment fragment, Context context) {
        Log.d(TAG, "onAttach, with fragment " + fragment + " and context " + context);
    }

    @FragmentLifecycle.OnLifecycleEvent(event = FragmentLifecycle.ON_CREATE)
    public void onCreate() {
        Log.d(TAG, "onCreate, no args");
    }

    @FragmentLifecycle.OnLifecycleEvent(event = FragmentLifecycle.ON_CREATE)
    public void onCreate(Fragment fragment) {
        Log.d(TAG, "onCreate, with fragment " + fragment);
    }

    @FragmentLifecycle.OnLifecycleEvent(event = FragmentLifecycle.ON_CREATE)
    public void onCreate(Fragment fragment, Bundle savedInstance) {
        Log.d(TAG, "onCreate, with fragment " + fragment + " and instance " + savedInstance);
    }

    @FragmentLifecycle.OnLifecycleEvent(event = FragmentLifecycle.ON_VIEW_CREATED)
    public void onViewCreated() {
        Log.d(TAG, "onViewCreated, no args");
    }

    @FragmentLifecycle.OnLifecycleEvent(event = FragmentLifecycle.ON_VIEW_CREATED)
    public void onViewCreated(Fragment fragment) {
        Log.d(TAG, "onViewCreated, with fragment " + fragment);
    }

    @FragmentLifecycle.OnLifecycleEvent(event = FragmentLifecycle.ON_VIEW_CREATED)
    public void onViewCreated(Fragment fragment, View view) {
        Log.d(TAG, "onViewCreated, with fragment " + fragment + " and view " + view);
    }

    @FragmentLifecycle.OnLifecycleEvent(event = FragmentLifecycle.ON_VIEW_CREATED)
    public void onViewCreated(Fragment fragment, View view, Bundle savedInstance) {
        Log.d(TAG, "onViewCreated, with fragment " + fragment + ", view " + view + " and bundle " + savedInstance);
    }

    @FragmentLifecycle.OnLifecycleEvent(event = FragmentLifecycle.ON_ACTIVITY_CREATED)
    public void onActivityCreated() {
        Log.d(TAG, "onActivityCreated, no args");
    }

    @FragmentLifecycle.OnLifecycleEvent(event = FragmentLifecycle.ON_ACTIVITY_CREATED)
    public void onActivityCreated(Fragment fragment) {
        Log.d(TAG, "onActivityCreated, with fragment " + fragment);
    }

    @FragmentLifecycle.OnLifecycleEvent(event = FragmentLifecycle.ON_ACTIVITY_CREATED)
    public void onActivityCreated(Fragment fragment, Bundle savedInstance) {
        Log.d(TAG, "onActivityCreated, with fragment " + fragment + " and bundle " + savedInstance);
    }

    @FragmentLifecycle.OnLifecycleEvent(event = FragmentLifecycle.ON_START)
    public void onStart() {
        Log.d(TAG, "onStart, no args");
    }

    @FragmentLifecycle.OnLifecycleEvent(event = FragmentLifecycle.ON_START)
    public void onStart(Fragment fragment) {
        Log.d(TAG, "onStart, with fragment" + fragment);
    }

    @FragmentLifecycle.OnLifecycleEvent(event = FragmentLifecycle.ON_RESUME)
    public void onResume() {
        Log.d(TAG, "onResume, no args");
    }

    @FragmentLifecycle.OnLifecycleEvent(event = FragmentLifecycle.ON_RESUME)
    public void onResume(Fragment fragment) {
        Log.d(TAG, "onResume, with fragment" + fragment);
    }

    @FragmentLifecycle.OnLifecycleEvent(event = FragmentLifecycle.ON_PAUSE)
    public void onPause() {
        Log.d(TAG, "onPause, no args");
    }

    @FragmentLifecycle.OnLifecycleEvent(event = FragmentLifecycle.ON_PAUSE)
    public void onPause(Fragment fragment) {
        Log.d(TAG, "onPause, with fragment" + fragment);
    }

    @FragmentLifecycle.OnLifecycleEvent(event = FragmentLifecycle.ON_STOP)
    public void onStop() {
        Log.d(TAG, "onStop, no args");
    }

    @FragmentLifecycle.OnLifecycleEvent(event = FragmentLifecycle.ON_STOP)
    public void onStop(Fragment fragment) {
        Log.d(TAG, "onStop, with fragment" + fragment);
    }

    @FragmentLifecycle.OnLifecycleEvent(event = FragmentLifecycle.ON_SAVE_INSTANCE)
    public void onSaveInstance() {
        Log.d(TAG, "onSaveInstance, no args");
    }

    @FragmentLifecycle.OnLifecycleEvent(event = FragmentLifecycle.ON_SAVE_INSTANCE)
    public void onSaveInstance(Fragment fragment) {
        Log.d(TAG, "onSaveInstance, with fragment" + fragment);
    }

    @FragmentLifecycle.OnLifecycleEvent(event = FragmentLifecycle.ON_DESTROY_VIEW)
    public void onDestroyView() {
        Log.d(TAG, "onDestroyView, no args");
    }

    @FragmentLifecycle.OnLifecycleEvent(event = FragmentLifecycle.ON_DESTROY_VIEW)
    public void onDestroyView(Fragment fragment) {
        Log.d(TAG, "onDestroyView, with fragment" + fragment);
    }

    @FragmentLifecycle.OnLifecycleEvent(event = FragmentLifecycle.ON_DESTROY)
    public void onDestroy() {
        Log.d(TAG, "onDestroy, no args");
    }

    @FragmentLifecycle.OnLifecycleEvent(event = FragmentLifecycle.ON_DESTROY)
    public void onDestroy(Fragment fragment) {
        Log.d(TAG, "onDestroy, with fragment" + fragment);
    }

    @FragmentLifecycle.OnLifecycleEvent(event = FragmentLifecycle.ON_DETACH)
    public void onDetach() {
        Log.d(TAG, "onDetach, no args");
    }

    @FragmentLifecycle.OnLifecycleEvent(event = FragmentLifecycle.ON_DETACH)
    public void onDetach(Fragment fragment) {
        Log.d(TAG, "onDetach, with fragment" + fragment);
    }

}
