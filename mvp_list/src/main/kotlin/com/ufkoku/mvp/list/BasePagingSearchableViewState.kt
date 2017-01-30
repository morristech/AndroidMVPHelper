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

package com.ufkoku.mvp.list

import com.ufkoku.mvp.list.interfaces.IPagingSearchableView
import com.ufkoku.mvp.list.util.StringUtils
import com.ufkoku.mvp_base.viewstate.IViewState

open class BasePagingSearchableViewState<I, in V : IPagingSearchableView<I, *>> : IViewState<V> {

    companion object {
        @JvmField val NO_ERROR_CODE = -1
    }

    var query = ""

    var items: MutableList<I>? = null

    var canLoadMore = false

    var isNextPageFailed = false

    var errorCode = NO_ERROR_CODE

    override fun apply(view: V) {
        view.setQuery(query)
        if (this.items != null) {
            view.setItems(this.items!!, canLoadMore, StringUtils.isNotNullOrEmpty(query))
            if (errorCode != NO_ERROR_CODE && isNextPageFailed) {
                view.onNextPageLoadFailed(errorCode)
            }
        } else if (errorCode != NO_ERROR_CODE) {
            view.onFirstPageLoadFailed(errorCode)
        }
    }

}
