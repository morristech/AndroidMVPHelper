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

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import com.ufkoku.mvp_base.presenter.IAsyncPresenter
import com.ufkoku.mvp_base.presenter.IPresenter
import com.ufkoku.mvp_base.view.IMvpActivity
import com.ufkoku.mvp_base.view.IMvpView
import com.ufkoku.mvp_base.viewstate.IViewState

class ActivityDelegate<out A, V : IMvpView, P : IPresenter<V>, VS : IViewState<V>>(val activity: A)
where A : AppCompatActivity, A : IMvpActivity<V, P, VS> {

    companion object {
        private val STATE_FRAGMENT_TAG = "com.ufkoku.mvp.retainable.delegate.StateFragment"
    }

    private var stateFragment: StateFragment<P, VS>? = null

    var presenter: P?
        get() = stateFragment!!.presenter
        private set(value) {
            stateFragment!!.presenter = value
        }

    var viewState: VS?
        get() = stateFragment!!.viewState
        private set(value) {
            stateFragment!!.viewState = value
        }

    //-----------------------------------------------------------------------------------------//

    fun onCreate(savedInstanceState: Bundle?) {
        val fragmentManager = activity.supportFragmentManager

        stateFragment = fragmentManager.findFragmentByTag(STATE_FRAGMENT_TAG) as StateFragment<P, VS>?
        if (stateFragment == null) {
            stateFragment = StateFragment()
            fragmentManager.beginTransaction()
                    .add(stateFragment, STATE_FRAGMENT_TAG)
                    .commit()
        }

        if (viewState == null) {
            viewState = activity.createNewViewState()
        }

        if (presenter == null) {
            presenter = activity.createPresenter()
        }

        activity.createView()

        viewState!!.apply(activity.getMvpView())
        presenter!!.onAttachView(activity.getMvpView())

        activity.onInitialized(presenter!!, viewState!!)
    }

    fun onDestroy() {
        presenter!!.onDetachView()
        if (activity.isFinishing && presenter is IAsyncPresenter<*>) {
            (presenter!! as IAsyncPresenter<*>).cancel()
        }
    }

    //-----------------------------------------------------------------------------------------//

    class StateFragment<P : IPresenter<*>, VS : IViewState<*>> : Fragment() {

        var presenter: P? = null

        var viewState: VS? = null

        override fun onCreate(savedInstanceState: Bundle?) {
            retainInstance = true
            super.onCreate(savedInstanceState)
        }

    }

}