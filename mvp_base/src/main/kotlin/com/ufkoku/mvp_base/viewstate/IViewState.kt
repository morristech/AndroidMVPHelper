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

package com.ufkoku.mvp_base.viewstate

import android.os.Bundle

interface IViewState<in T> {

    /**
     * Restore your data from bundle
     * */
    fun save(out: Bundle)

    /**
     * Save your data to bundle
     * */
    fun restore(inState: Bundle)

    /**
     * This method is called to apply saved state to view
     * */
    fun apply(view: T)

}
