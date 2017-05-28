/*
 * Copyright 2017 Ufkoku (https://github.com/Ufkoku/AndroidMVPHelper)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package com.ufkoku.mvp.delegate.observable

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.View
import com.ufkoku.mvp_base.view.lifecycle.FragmentLifecycle
import java.lang.reflect.Method
import java.util.*

open class FragmentLifecycleObservable : BaseLifecycleObservable() {

    protected companion object {
        val TAG = "FragmentLifecycleObservable"
        val ANNOTATION_CLASS = FragmentLifecycle.OnLifecycleEvent::class.java
    }

    protected val onAttachListeners: WeakHashMap<Any, Collection<Method>> = WeakHashMap()
    protected val onCreateListeners: WeakHashMap<Any, Collection<Method>> = WeakHashMap()
    protected val onViewCreatedListeners: WeakHashMap<Any, Collection<Method>> = WeakHashMap()
    protected val onActivityCreatedListeners: WeakHashMap<Any, Collection<Method>> = WeakHashMap()
    protected val onStartListeners: WeakHashMap<Any, Collection<Method>> = WeakHashMap()
    protected val onResumeListeners: WeakHashMap<Any, Collection<Method>> = WeakHashMap()
    protected val onPauseListeners: WeakHashMap<Any, Collection<Method>> = WeakHashMap()
    protected val onStopListeners: WeakHashMap<Any, Collection<Method>> = WeakHashMap()
    protected val onSaveInstanceListeners: WeakHashMap<Any, Collection<Method>> = WeakHashMap()
    protected val onDestroyViewListeners: WeakHashMap<Any, Collection<Method>> = WeakHashMap()
    protected val onDestroyListeners: WeakHashMap<Any, Collection<Method>> = WeakHashMap()
    protected val onDetachListeners: WeakHashMap<Any, Collection<Method>> = WeakHashMap()

    //---------------------------------------------------------------------------------------//

    override fun subscribe(observer: Any) {
        if (registeredSet.contains(observer)) {
            return
        }

        val methods = getAcceptableMethods(observer.javaClass, ANNOTATION_CLASS)

        if (methods.isNotEmpty()) {
            registeredSet.add(observer)

            fillMethodsForEvent(FragmentLifecycle.ON_ATTACH, onAttachListeners, observer, methods)
            fillMethodsForEvent(FragmentLifecycle.ON_CREATE, onCreateListeners, observer, methods)
            fillMethodsForEvent(FragmentLifecycle.ON_VIEW_CREATED, onViewCreatedListeners, observer, methods)
            fillMethodsForEvent(FragmentLifecycle.ON_ACTIVITY_CREATED, onActivityCreatedListeners, observer, methods)
            fillMethodsForEvent(FragmentLifecycle.ON_START, onStartListeners, observer, methods)
            fillMethodsForEvent(FragmentLifecycle.ON_RESUME, onResumeListeners, observer, methods)
            fillMethodsForEvent(FragmentLifecycle.ON_PAUSE, onPauseListeners, observer, methods)
            fillMethodsForEvent(FragmentLifecycle.ON_STOP, onStopListeners, observer, methods)
            fillMethodsForEvent(FragmentLifecycle.ON_SAVE_INSTANCE, onSaveInstanceListeners, observer, methods)
            fillMethodsForEvent(FragmentLifecycle.ON_DESTROY_VIEW, onDestroyViewListeners, observer, methods)
            fillMethodsForEvent(FragmentLifecycle.ON_DESTROY, onDestroyListeners, observer, methods)
            fillMethodsForEvent(FragmentLifecycle.ON_DETACH, onDetachListeners, observer, methods)
        }
    }

    override fun unsubscribe(observer: Any) {
        if (!registeredSet.contains(observer)) {
            return
        }

        registeredSet.remove(observer)

        onAttachListeners.remove(observer)
        onCreateListeners.remove(observer)
        onViewCreatedListeners.remove(observer)
        onActivityCreatedListeners.remove(observer)
        onStartListeners.remove(observer)
        onResumeListeners.remove(observer)
        onPauseListeners.remove(observer)
        onStopListeners.remove(observer)
        onSaveInstanceListeners.remove(observer)
        onDestroyViewListeners.remove(observer)
        onDestroyListeners.remove(observer)
        onDetachListeners.remove(observer)
    }

    //---------------------------------------------------------------------------------------//

    open fun onAttach(fragment: Fragment, context: Context) {
        invokeMethodsFromMap(onAttachListeners, fragment, context)
    }

    open fun onCreate(fragment: Fragment, savedInstance: Bundle?) {
        invokeMethodsFromMap(onCreateListeners, fragment, savedInstance)
    }

    open fun onViewCreated(fragment: Fragment, view: View, savedInstance: Bundle?) {
        invokeMethodsFromMap(onViewCreatedListeners, fragment, view, savedInstance)
    }

    open fun onActivityCreated(fragment: Fragment, savedInstance: Bundle?) {
        invokeMethodsFromMap(onActivityCreatedListeners, fragment, savedInstance)
    }

    open fun onStart(fragment: Fragment) {
        invokeMethodsFromMap(onStartListeners, fragment)
    }

    open fun onResume(fragment: Fragment) {
        invokeMethodsFromMap(onResumeListeners, fragment)
    }

    open fun onPause(fragment: Fragment) {
        invokeMethodsFromMap(onPauseListeners, fragment)
    }

    open fun onStop(fragment: Fragment) {
        invokeMethodsFromMap(onStopListeners, fragment)
    }

    open fun onSaveInstance(fragment: Fragment, savedInstance: Bundle?) {
        invokeMethodsFromMap(onSaveInstanceListeners, fragment, savedInstance)
    }

    open fun onDestroyView(fragment: Fragment) {
        invokeMethodsFromMap(onDestroyViewListeners, fragment)
    }

    open fun onDestroy(fragment: Fragment) {
        invokeMethodsFromMap(onDestroyListeners, fragment)
    }

    open fun onDetach(fragment: Fragment) {
        invokeMethodsFromMap(onDetachListeners, fragment)
    }

    //---------------------------------------------------------------------------------------//

    protected open fun fillMethodsForEvent(
            @FragmentLifecycle.FragmentEvent event: Int,
            target: MutableMap<Any, Collection<Method>>,
            subscriber: Any,
            methods: Collection<Method>) {

        val filtered = methods.filter { it.getAnnotation(ANNOTATION_CLASS).event == event }
        if (filtered.isNotEmpty()) {
            target[subscriber] = filtered
        }

    }

}
