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

import android.util.Log
import com.ufkoku.mvp_base.view.lifecycle.ILifecycleObservable
import java.lang.reflect.Method
import java.lang.reflect.Modifier
import java.util.*

abstract class BaseLifecycleObservable : ILifecycleObservable {

    protected companion object {
        val TAG = "ActivityObservable"
    }

    protected val registeredSet: MutableSet<Any?> = Collections.newSetFromMap(WeakHashMap())

    //-----------------------------------------------------------------------------------------//

    protected fun getAcceptableMethods(clazz: Class<*>, annotation: Class<out Annotation>): Collection<Method> {
        val methods = clazz.getAllDeclaredAcceptableMethods(annotation).filterUnique()
        methods.forEach { it.isAccessible = true }
        return methods
    }

    protected fun Class<*>.getAllDeclaredAcceptableMethods(annotation: Class<out Annotation>): Collection<Method> {
        var methods = this.declaredMethods
                .filter { !Modifier.isStatic(it.modifiers) && it.isAnnotationPresent(annotation) }

        if (methods !is MutableList) {
            methods = methods.toMutableList()
        }

        if (this.superclass != null) {
            methods.addAll(this.superclass.getAllDeclaredAcceptableMethods(annotation))
        }

        return methods
    }

    protected fun Iterable<Method>.filterUnique(): Collection<Method> {
        val uniqueMethods = ArrayList<Method>()

        for (method in this) {
            if (uniqueMethods.none { areEqualMethods(method, it) }) {
                uniqueMethods.add(method)
            }
        }

        return uniqueMethods
    }

    protected open fun areEqualMethods(method1: Method, method2: Method): Boolean {
        if (method1.name != (method2.name)) {
            return false
        }

        if (method1.returnType != method2.returnType) {
            return false
        }

        val params1 = method1.parameterTypes
        val params2 = method2.parameterTypes
        if (params1.size == params2.size) {
            return params1.indices.none { params1[it] != params2[it] }
        }

        return false
    }

    protected open fun printErrorMessage(method: Method, ex: Exception?) {
        if (ex == null) {
            Log.e(TAG, "Incorrect contract of method $method")
        } else {
            Log.e(TAG, "Incorrect contract of method $method", ex)
        }
    }

    //-----------------------------------------------------------------------------------------//

    protected open fun invokeMethodsFromMap(subscribers: Map<Any, Collection<Method>>, vararg args: Any?) {
        for ((key, value) in subscribers) {
            for (method in value) {
                invokeMethod(key, method, *args)
            }
        }
    }

    protected open fun invokeMethod(subscriber: Any, method: Method, vararg args: Any?) {
        val params = method.parameterTypes
        val paramsCount = params.size

        if (args.size < paramsCount) {
            printErrorMessage(method, null)
            return
        }

        try {
            if (paramsCount == 0) {
                method.invoke(subscriber)
            } else {
                val requestedArgs: Array<*> = Arrays.copyOf(args, paramsCount)
                method.invoke(subscriber, *requestedArgs)
            }
        } catch (ex: IllegalArgumentException) {
            printErrorMessage(method, ex)
        }
    }

}