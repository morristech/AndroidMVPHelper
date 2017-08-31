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

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.View
import com.ufkoku.mvp.BaseMvpDialogFragment
import com.ufkoku.mvp.list.delegate.BasePagingSearchableDelegate
import com.ufkoku.mvp.list.interfaces.IPagingResponse
import com.ufkoku.mvp.list.interfaces.IPagingSearchablePresenter
import com.ufkoku.mvp.list.interfaces.IPagingSearchableView
import com.ufkoku.mvp.list.viewstate.BasePagingSearchableViewState
import com.ufkoku.mvp_base.presenter.IPresenter


abstract class BaseMvpListDialogFragment<I, in PR, D, V, P, VS> : BaseMvpDialogFragment<V, P, VS>()
where
PR : IPagingResponse<I>,
D : BasePagingSearchableDelegate<I, PR, V, P, VS>,
V : IPagingSearchableView<I, PR>,
P : IPresenter<V>, P : IPagingSearchablePresenter,
VS : BasePagingSearchableViewState<I, V> {

    @Suppress("LeakingThis")
    protected val pagingDelegate = createDelegate()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        pagingDelegate.onAttach(context as Activity)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        pagingDelegate.presenter = presenter
        pagingDelegate.viewState = viewState
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupViewsToDelegate(view, pagingDelegate)
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onInitialized(presenter: P, viewState: VS) {
        pagingDelegate.onInitialized(presenter, viewState)
    }

    override fun onDestroyView() {
        pagingDelegate.onDestroyView()
        super.onDestroyView()
    }

    override fun onDestroy() {
        pagingDelegate.onDestroy()
        super.onDestroy()
    }

    override fun onDetach() {
        pagingDelegate.onDetach()
        super.onDetach()
    }

    final override fun retainPresenter(): Boolean {
        return true
    }

    final override fun createPresenter(): P {
        val presenter = createListPresenter()
        pagingDelegate.presenter = presenter
        return presenter
    }

    final override fun createNewViewState(): VS {
        val viewState = createListViewState()
        pagingDelegate.viewState = viewState
        return viewState
    }

    abstract fun createListPresenter(): P

    abstract fun createListViewState(): VS

    abstract fun setupViewsToDelegate(view: View, delegate: D)

    abstract fun createDelegate(): D

}
