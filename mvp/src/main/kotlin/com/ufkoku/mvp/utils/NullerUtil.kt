/*
 * Copyright 2016 Ufkoku (https://github.com/Ufkoku/AndroidMVPHelper)
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

package com.ufkoku.mvp.utils

import java.lang.reflect.Field
import java.util.*

object NullerUtil {

    private val cache: WeakHashMap<Class<*>, WeakHashMap<Class<*>, MutableList<Field>>> = WeakHashMap()

    fun Any.nullAllFields(typeOfFields: Class<*>) {
        this.nullAllFields(this.javaClass.getAllAcceptableFields(typeOfFields))
    }

    fun Any.nullAllFields(methods: Collection<Field>) {
        methods.forEach { it.set(this, null) }
    }

    fun Class<*>.getAllAcceptableFields(typeOfFields: Class<*>): Collection<Field> {
        var cacheForClass = cache[this.javaClass]
        if (cacheForClass == null) {
            cacheForClass = WeakHashMap()
            cache[this.javaClass] = cacheForClass
        }

        var fields = cacheForClass[typeOfFields]
        if (fields == null) {
            fields = this.declaredFields
                    .filter { !typeOfFields.isPrimitive && typeOfFields.isAssignableFrom(it.type) }
                    .toMutableList()
            cacheForClass[typeOfFields] = fields
        }

        if (this.superclass != null) {
            fields.addAll(this.superclass.getAllAcceptableFields(typeOfFields))
        }

        fields.forEach { it.isAccessible = true }

        return fields
    }

}