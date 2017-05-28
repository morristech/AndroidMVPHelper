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

import android.app.Activity
import android.os.Bundle
import com.ufkoku.mvp_base.view.lifecycle.ActivityLifecycle
import java.lang.reflect.Method
import java.util.*

open class ActivityLifecycleObservable : BaseLifecycleObservable() {

    protected companion object {
        val TAG = "ActivityLifecycleObservable"
        val ANNOTATION_CLASS = ActivityLifecycle.OnLifecycleEvent::class.java
    }

    protected val onCreateListeners: WeakHashMap<Any, Collection<Method>> = WeakHashMap()
    protected val onStartListeners: WeakHashMap<Any, Collection<Method>> = WeakHashMap()
    protected val onResumeListeners: WeakHashMap<Any, Collection<Method>> = WeakHashMap()
    protected val onPauseListeners: WeakHashMap<Any, Collection<Method>> = WeakHashMap()
    protected val onStopListeners: WeakHashMap<Any, Collection<Method>> = WeakHashMap()
    protected val onSaveInstanceListeners: WeakHashMap<Any, Collection<Method>> = WeakHashMap()
    protected val onDestroyListeners: WeakHashMap<Any, Collection<Method>> = WeakHashMap()

    //---------------------------------------------------------------------------------------//

    override fun subscribe(observer: Any) {
        if (registeredSet.contains(observer)){
            return
        }

        val methods = getAcceptableMethods(observer.javaClass, ANNOTATION_CLASS)

        if (methods.isNotEmpty()) {
            registeredSet.add(observer)

            fillMethodsForEvent(ActivityLifecycle.ON_CREATE, onCreateListeners, observer, methods)
            fillMethodsForEvent(ActivityLifecycle.ON_START, onStartListeners, observer, methods)
            fillMethodsForEvent(ActivityLifecycle.ON_RESUME, onResumeListeners, observer, methods)
            fillMethodsForEvent(ActivityLifecycle.ON_PAUSE, onPauseListeners, observer, methods)
            fillMethodsForEvent(ActivityLifecycle.ON_STOP, onStopListeners, observer, methods)
            fillMethodsForEvent(ActivityLifecycle.ON_SAVE_INSTANCE, onSaveInstanceListeners, observer, methods)
            fillMethodsForEvent(ActivityLifecycle.ON_DESTROY, onDestroyListeners, observer, methods)
        }
    }

    override fun unsubscribe(observer: Any) {
        if (!registeredSet.contains(observer)){
            return
        }

        registeredSet.remove(observer)

        onCreateListeners.remove(observer)
        onStartListeners.remove(observer)
        onResumeListeners.remove(observer)
        onPauseListeners.remove(observer)
        onStopListeners.remove(observer)
        onSaveInstanceListeners.remove(observer)
        onDestroyListeners.remove(observer)
    }

    //---------------------------------------------------------------------------------------//

    open fun onCreate(activity: Activity, savedInstance: Bundle?) {
        invokeMethodsFromMap(onCreateListeners, activity, savedInstance)
    }

    open fun onStart(activity: Activity) {
        invokeMethodsFromMap(onStartListeners, activity)
    }

    open fun onResume(activity: Activity) {
        invokeMethodsFromMap(onResumeListeners, activity)
    }

    open fun onPause(activity: Activity) {
        invokeMethodsFromMap(onPauseListeners, activity)
    }

    open fun onStop(activity: Activity) {
        invokeMethodsFromMap(onStopListeners, activity)
    }

    open fun onSaveInstance(activity: Activity, savedInstance: Bundle?) {
        invokeMethodsFromMap(onSaveInstanceListeners, activity, savedInstance)
    }

    open fun onDestroy(activity: Activity) {
        invokeMethodsFromMap(onDestroyListeners, activity)
    }

    //---------------------------------------------------------------------------------------//

    protected open fun fillMethodsForEvent(
            @ActivityLifecycle.ActivityEvent event: Int,
            target: MutableMap<Any, Collection<Method>>,
            subscriber: Any,
            methods: Collection<Method>) {

        val filtered = methods.filter { it.getAnnotation(ANNOTATION_CLASS).event == event }
        if (filtered.isNotEmpty()) {
            target[subscriber] = filtered
        }

    }

}
