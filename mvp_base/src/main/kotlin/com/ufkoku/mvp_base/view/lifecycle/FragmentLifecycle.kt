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


package com.ufkoku.mvp_base.view.lifecycle

import android.support.annotation.IntDef
import java.lang.annotation.Inherited

object FragmentLifecycle {

    const val ON_ATTACH = 0
    const val ON_CREATE = 1
    const val ON_VIEW_CREATED = 2
    const val ON_ACTIVITY_CREATED = 3
    const val ON_START = 4
    const val ON_RESUME = 5
    const val ON_PAUSE = 6
    const val ON_STOP = 7
    const val ON_SAVE_INSTANCE = 8
    const val ON_DESTROY_VIEW = 9
    const val ON_DESTROY = 10
    const val ON_DETACH = 11

    @Target(AnnotationTarget.VALUE_PARAMETER)
    @Retention(AnnotationRetention.SOURCE)
    @IntDef(ON_ATTACH.toLong(), ON_CREATE.toLong(), ON_VIEW_CREATED.toLong(), ON_ACTIVITY_CREATED.toLong(), ON_START.toLong(), ON_RESUME.toLong(), ON_PAUSE.toLong(), ON_STOP.toLong(), ON_SAVE_INSTANCE.toLong(), ON_DESTROY_VIEW.toLong(), ON_DESTROY.toLong(), ON_DETACH.toLong())
    annotation class FragmentEvent

    @Target(AnnotationTarget.FUNCTION)
    @Retention(AnnotationRetention.RUNTIME)
    @Inherited
    annotation class OnLifecycleEvent(@FragmentEvent val event: Int)

}