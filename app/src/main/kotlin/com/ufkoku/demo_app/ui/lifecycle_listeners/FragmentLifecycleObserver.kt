package com.ufkoku.demo_app.ui.lifecycle_listeners

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.View

import com.ufkoku.mvp_base.view.lifecycle.FragmentLifecycle

@SuppressLint("LongLogTag")
class FragmentLifecycleObserver {

    companion object {
        private val TAG = "FragmentLifecycleObserver"
    }

    @FragmentLifecycle.OnLifecycleEvent(event = FragmentLifecycle.ON_ATTACH)
    fun onAttach() {
        Log.d(TAG, "onAttach, no args")
    }

    @FragmentLifecycle.OnLifecycleEvent(event = FragmentLifecycle.ON_ATTACH)
    fun onAttach(fragment: Fragment) {
        Log.d(TAG, "onAttach, with fragment " + fragment)
    }

    @FragmentLifecycle.OnLifecycleEvent(event = FragmentLifecycle.ON_ATTACH)
    fun onAttach(fragment: Fragment, context: Context) {
        Log.d(TAG, "onAttach, with fragment $fragment and context $context")
    }

    @FragmentLifecycle.OnLifecycleEvent(event = FragmentLifecycle.ON_CREATE)
    fun onCreate() {
        Log.d(TAG, "onCreate, no args")
    }

    @FragmentLifecycle.OnLifecycleEvent(event = FragmentLifecycle.ON_CREATE)
    fun onCreate(fragment: Fragment) {
        Log.d(TAG, "onCreate, with fragment " + fragment)
    }

    @FragmentLifecycle.OnLifecycleEvent(event = FragmentLifecycle.ON_CREATE)
    fun onCreate(fragment: Fragment, savedInstance: Bundle?) {
        Log.d(TAG, "onCreate, with fragment $fragment and instance $savedInstance")
    }

    @FragmentLifecycle.OnLifecycleEvent(event = FragmentLifecycle.ON_VIEW_CREATED)
    fun onViewCreated() {
        Log.d(TAG, "onViewCreated, no args")
    }

    @FragmentLifecycle.OnLifecycleEvent(event = FragmentLifecycle.ON_VIEW_CREATED)
    fun onViewCreated(fragment: Fragment) {
        Log.d(TAG, "onViewCreated, with fragment " + fragment)
    }

    @FragmentLifecycle.OnLifecycleEvent(event = FragmentLifecycle.ON_VIEW_CREATED)
    fun onViewCreated(fragment: Fragment, view: View) {
        Log.d(TAG, "onViewCreated, with fragment $fragment and view $view")
    }

    @FragmentLifecycle.OnLifecycleEvent(event = FragmentLifecycle.ON_VIEW_CREATED)
    fun onViewCreated(fragment: Fragment, view: View, savedInstance: Bundle?) {
        Log.d(TAG, "onViewCreated, with fragment $fragment, view $view and bundle $savedInstance")
    }

    @FragmentLifecycle.OnLifecycleEvent(event = FragmentLifecycle.ON_ACTIVITY_CREATED)
    fun onActivityCreated() {
        Log.d(TAG, "onActivityCreated, no args")
    }

    @FragmentLifecycle.OnLifecycleEvent(event = FragmentLifecycle.ON_ACTIVITY_CREATED)
    fun onActivityCreated(fragment: Fragment) {
        Log.d(TAG, "onActivityCreated, with fragment " + fragment)
    }

    @FragmentLifecycle.OnLifecycleEvent(event = FragmentLifecycle.ON_ACTIVITY_CREATED)
    fun onActivityCreated(fragment: Fragment, savedInstance: Bundle?) {
        Log.d(TAG, "onActivityCreated, with fragment $fragment and bundle $savedInstance")
    }

    @FragmentLifecycle.OnLifecycleEvent(event = FragmentLifecycle.ON_START)
    fun onStart() {
        Log.d(TAG, "onStart, no args")
    }

    @FragmentLifecycle.OnLifecycleEvent(event = FragmentLifecycle.ON_START)
    fun onStart(fragment: Fragment) {
        Log.d(TAG, "onStart, with fragment" + fragment)
    }

    @FragmentLifecycle.OnLifecycleEvent(event = FragmentLifecycle.ON_RESUME)
    fun onResume() {
        Log.d(TAG, "onResume, no args")
    }

    @FragmentLifecycle.OnLifecycleEvent(event = FragmentLifecycle.ON_RESUME)
    fun onResume(fragment: Fragment) {
        Log.d(TAG, "onResume, with fragment" + fragment)
    }

    @FragmentLifecycle.OnLifecycleEvent(event = FragmentLifecycle.ON_PAUSE)
    fun onPause() {
        Log.d(TAG, "onPause, no args")
    }

    @FragmentLifecycle.OnLifecycleEvent(event = FragmentLifecycle.ON_PAUSE)
    fun onPause(fragment: Fragment) {
        Log.d(TAG, "onPause, with fragment" + fragment)
    }

    @FragmentLifecycle.OnLifecycleEvent(event = FragmentLifecycle.ON_STOP)
    fun onStop() {
        Log.d(TAG, "onStop, no args")
    }

    @FragmentLifecycle.OnLifecycleEvent(event = FragmentLifecycle.ON_STOP)
    fun onStop(fragment: Fragment) {
        Log.d(TAG, "onStop, with fragment" + fragment)
    }

    @FragmentLifecycle.OnLifecycleEvent(event = FragmentLifecycle.ON_SAVE_INSTANCE)
    fun onSaveInstance() {
        Log.d(TAG, "onSaveInstance, no args")
    }

    @FragmentLifecycle.OnLifecycleEvent(event = FragmentLifecycle.ON_SAVE_INSTANCE)
    fun onSaveInstance(fragment: Fragment) {
        Log.d(TAG, "onSaveInstance, with fragment" + fragment)
    }

    @FragmentLifecycle.OnLifecycleEvent(event = FragmentLifecycle.ON_DESTROY_VIEW)
    fun onDestroyView() {
        Log.d(TAG, "onDestroyView, no args")
    }

    @FragmentLifecycle.OnLifecycleEvent(event = FragmentLifecycle.ON_DESTROY_VIEW)
    fun onDestroyView(fragment: Fragment) {
        Log.d(TAG, "onDestroyView, with fragment" + fragment)
    }

    @FragmentLifecycle.OnLifecycleEvent(event = FragmentLifecycle.ON_DESTROY)
    fun onDestroy() {
        Log.d(TAG, "onDestroy, no args")
    }

    @FragmentLifecycle.OnLifecycleEvent(event = FragmentLifecycle.ON_DESTROY)
    fun onDestroy(fragment: Fragment) {
        Log.d(TAG, "onDestroy, with fragment" + fragment)
    }

    @FragmentLifecycle.OnLifecycleEvent(event = FragmentLifecycle.ON_DETACH)
    fun onDetach() {
        Log.d(TAG, "onDetach, no args")
    }

    @FragmentLifecycle.OnLifecycleEvent(event = FragmentLifecycle.ON_DETACH)
    fun onDetach(fragment: Fragment) {
        Log.d(TAG, "onDetach, with fragment" + fragment)
    }

}
