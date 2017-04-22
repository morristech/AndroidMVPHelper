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

package com.ufkoku.mvp.savable.delegate

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.View
import com.ufkoku.mvp_base.presenter.IAsyncPresenter
import com.ufkoku.mvp_base.presenter.IPresenter
import com.ufkoku.mvp_base.view.IMvpFragment
import com.ufkoku.mvp_base.view.IMvpView
import com.ufkoku.mvp_base.viewstate.ISavableViewState

class FragmentDelegate<out F, V : IMvpView, P : IPresenter<V>, VS : ISavableViewState<V>>(val fragment: F)
where F : Fragment, F : IMvpFragment<V, P, VS>, F : ISavableDelegateClient {

    companion object {
        private val STATE_FRAGMENT_TAG = "com.ufkoku.mvp.savable.delegate.FragmentDelegate.PresenterFragment"
        private val KEY_PRESENTER = "presenterFragmentKey"
    }

    /**
     * Retainable fragment to store presenter, if ISavableDelegator.retainPresenter() returns true
     * */
    private var presenterFragment: PresenterFragment<P>? = null

    /**
     * Used to get presenter fragment from fragment manager
     * */
    private var presenterFragmentTag: String? = null

    var presenter: P? = null
        get() = if (fragment.retainPresenter()) presenterFragment!!.presenter else field
        private set(value) {
            if (fragment.retainPresenter()) {
                presenterFragment!!.presenter = value
                field = null
            } else {
                field = value
            }
        }

    var viewState: VS? = null
        private set

    var instanceSaved = false
        private set


    //-----------------------------------------------------------------------------------------//

    fun onCreate(savedInstanceState: Bundle?) {
        viewState = fragment.createNewViewState()
        if (savedInstanceState != null) {
            viewState!!.restore(savedInstanceState)
        }

        if (fragment.retainPresenter()) {
            presenterFragmentTag = savedInstanceState?.getString(KEY_PRESENTER) ?: (STATE_FRAGMENT_TAG + hashCode())
            presenterFragment = fragment.fragmentManager.findFragmentByTag(presenterFragmentTag) as PresenterFragment<P>?
            if (presenterFragment == null) {
                presenterFragment = PresenterFragment()
                fragment.fragmentManager.beginTransaction()
                        .add(presenterFragment, presenterFragmentTag)
                        .commit()
            }
        }

        if (presenter == null) {
            presenter = fragment.createPresenter()
        }
    }

    fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        viewState!!.apply(fragment.getMvpView())
        presenter!!.onAttachView(fragment.getMvpView())

        fragment.onInitialized(presenter!!, viewState!!)
    }

    fun onSaveInstanceState(outState: Bundle?) {
        if (outState != null) {
            viewState!!.save(outState)
            if (presenterFragmentTag != null) {
                outState.putString(KEY_PRESENTER, presenterFragmentTag)
            }
            instanceSaved = true
        }
    }

    fun onDestroyView() {
        presenter?.onDetachView()
    }

    fun onDestroy() {
        if (!instanceSaved && presenterFragment != null) {
            fragment.fragmentManager.beginTransaction()
                    .remove(presenterFragment)
                    .commitAllowingStateLoss()
            presenterFragment = null
        }

        if (!fragment.retainPresenter()) {
            if (presenter is IAsyncPresenter<*>) {
                (presenter as IAsyncPresenter<*>).cancel()
            }
            presenter = null
        }

        viewState = null
    }

    //-----------------------------------------------------------------------------------------//

    class PresenterFragment<P : IPresenter<*>> : Fragment() {

        var presenter: P? = null

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            retainInstance = true
        }

        override fun onDestroy() {
            if (presenter is IAsyncPresenter<*>) {
                (presenter as IAsyncPresenter<*>).cancel()
            }
            presenter = null

            super.onDestroy()
        }

    }

}
