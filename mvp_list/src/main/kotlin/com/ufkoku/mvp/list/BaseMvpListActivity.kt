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

import android.os.Bundle
import com.ufkoku.mvp.BaseMvpActivity
import com.ufkoku.mvp.list.delegate.BasePagingSearchableDelegate
import com.ufkoku.mvp.list.interfaces.IPagingResponse
import com.ufkoku.mvp.list.interfaces.IPagingSearchablePresenter
import com.ufkoku.mvp.list.interfaces.IPagingSearchableView
import com.ufkoku.mvp.list.viewstate.BasePagingSearchableViewState
import com.ufkoku.mvp_base.presenter.IPresenter


abstract class BaseMvpListActivity<I, in PR, D, V, P, VS> : BaseMvpActivity<V, P, VS>()
where
PR : IPagingResponse<I>,
D : BasePagingSearchableDelegate<I, PR, V, P, VS>,
V : IPagingSearchableView<I, PR>,
P : IPresenter<V>, P : IPagingSearchablePresenter,
VS : BasePagingSearchableViewState<I, V> {

    @Suppress("LeakingThis")
    protected val pagingDelegate = createDelegate()

    override fun onCreate(savedInstanceState: Bundle?) {
        pagingDelegate.onAttach(this)

        //setting retained
        pagingDelegate.presenter = presenter
        pagingDelegate.viewState = viewState

        super.onCreate(savedInstanceState)
    }

    override fun onInitialized(presenter: P, viewState: VS) {
        pagingDelegate.onInitialized(presenter, viewState)
    }

    override fun onDestroy() {
        pagingDelegate.onDestroyView()
        pagingDelegate.onDestroy()
        pagingDelegate.onDetach()
        super.onDestroy()
    }

    final override fun retainPresenter(): Boolean {
        return true
    }

    final override fun createPresenter(): P {
        val presenter = createListPresenter()
        pagingDelegate.presenter = presenter
        return presenter
    }

    final override fun createViewState(): VS {
        val viewState = createListViewState()
        pagingDelegate.viewState = viewState
        return viewState
    }

    abstract fun createListPresenter(): P

    abstract fun createListViewState(): VS

    abstract fun createDelegate(): D

}

