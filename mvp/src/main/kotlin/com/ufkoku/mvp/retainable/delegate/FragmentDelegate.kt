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

package com.ufkoku.mvp.retainable.delegate

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.View
import com.ufkoku.mvp_base.presenter.IAsyncPresenter
import com.ufkoku.mvp_base.presenter.IPresenter
import com.ufkoku.mvp_base.view.IMvpFragment
import com.ufkoku.mvp_base.view.IMvpView
import com.ufkoku.mvp_base.viewstate.IViewState

open class FragmentDelegate<out F, V : IMvpView, P : IPresenter<V>, VS : IViewState<V>>(val fragment: F) where F : Fragment, F : IMvpFragment<V, P, VS> {

    var presenter: P? = null
        protected set

    var viewState: VS? = null
        protected set

    fun onCreate(savedInstanceState: Bundle?) {
        fragment.retainInstance = true
        viewState = fragment.createNewViewState()
        presenter = fragment.createPresenter()
    }

    fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        viewState!!.apply(fragment.getMvpView())
        presenter!!.onAttachView(fragment.getMvpView())

        fragment.onInitialized(presenter!!, viewState!!)
    }

    fun onDestroyView() {
        presenter!!.onDetachView()
    }

    fun onDestroy() {
        if (presenter is IAsyncPresenter<*>) {
            (presenter as IAsyncPresenter<*>).cancel()
        }

        presenter = null
        viewState = null
    }

}
