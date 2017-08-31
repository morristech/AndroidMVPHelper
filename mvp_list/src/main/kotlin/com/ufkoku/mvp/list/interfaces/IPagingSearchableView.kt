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

package com.ufkoku.mvp.list.interfaces

import com.ufkoku.mvp_base.presenter.IAsyncPresenter

interface IPagingSearchableView<I, in PR : IPagingResponse<I>> : IAsyncPresenter.ITaskListener {

    fun setQuery(query: String)

    fun onFirstPageLoaded(response: PR)

    fun setItems(items: MutableList<I>, canLoadMore: Boolean, isSearch: Boolean)

    fun onNextPageLoaded(response: PR)

    fun onNextPageLoadFailed(code: Int)

    fun onFirstPageLoadFailed(code: Int)

}
