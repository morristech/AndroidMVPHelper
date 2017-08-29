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

package com.ufkoku.mvp.delegate.controller

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.ufkoku.mvp.base.IMvpActivity
import com.ufkoku.mvp.utils.HolderFragment
import com.ufkoku.mvp_base.presenter.IAsyncPresenter
import com.ufkoku.mvp_base.presenter.IPresenter
import com.ufkoku.mvp_base.view.IMvpView
import com.ufkoku.mvp_base.viewstate.IViewState

open class ActivityDelegate<out A, V : IMvpView, P : IPresenter<V>, VS : IViewState<V>>(val activity: A)
where A : AppCompatActivity, A : IMvpActivity<V, P, VS> {

    companion object {
        private val KEY_PRESENTER = "com.ufkoku.mvp.delegate.controller.ActivityDelegate.keyPresenter"
        private val KEY_VIEW_STATE = "com.ufkoku.mvp.delegate.controller.ActivityDelegate.keyViewState"
    }

    /**
     * Used to get presenter from fragment holder
     * */
    protected var presenterId: Int? = null

    /**
     * Used to get view state from fragment holder
     * */
    protected var viewStateId: Int? = null

    var presenter: P? = null
        protected set

    var viewState: VS? = null
        protected set

    //-----------------------------------------------------------------------------------------//

    @Suppress("UNCHECKED_CAST")
    fun onCreate(savedInstanceState: Bundle?) {
        val holder: HolderFragment?
        if (activity.retainPresenter() || activity.retainViewState()) {
            holder = HolderFragment.getInstance(activity)
        } else {
            holder = null
        }

        var viewState: VS? = null
        var viewStateId: Int? = null
        if (savedInstanceState != null && savedInstanceState.containsKey(KEY_VIEW_STATE)) {
            viewStateId = savedInstanceState.getInt(KEY_VIEW_STATE)
            viewState = holder!!.getViewState(viewStateId) as VS?
        }
        if (viewState == null) {
            viewState = activity.createNewViewState()
            if (savedInstanceState != null) {
                viewState.restore(savedInstanceState)
            }
            if (activity.retainViewState()) {
                if (viewStateId == null) {
                    viewStateId = holder!!.addViewState(viewState)
                } else {
                    holder!!.setViewState(viewStateId, viewState)
                }
            }
        }
        this.viewState = viewState
        this.viewStateId = viewStateId

        var presenter: P? = null
        var presenterId: Int? = null
        if (savedInstanceState != null && savedInstanceState.containsKey(KEY_PRESENTER)) {
            presenterId = savedInstanceState.getInt(KEY_PRESENTER)
            presenter = holder!!.getPresenter(presenterId) as P?
        }
        if (presenter == null) {
            presenter = activity.createPresenter()
            if (activity.retainPresenter()) {
                if (presenterId == null) {
                    presenterId = holder!!.addPresenter(presenter)
                } else {
                    holder!!.setPresenter(presenterId, presenter)
                }
            }
        }
        this.presenter = presenter
        this.presenterId = presenterId

        activity.createView()

        viewState.apply(activity.getMvpView())
        presenter.onAttachView(activity.getMvpView())

        activity.onInitialized(presenter, viewState)
    }

    fun onSaveInstanceState(outState: Bundle?) {
        if (outState != null) {
            viewState!!.save(outState)
            if (activity.retainPresenter()) {
                outState.putInt(KEY_PRESENTER, presenterId!!)
            }
            if (activity.retainViewState()) {
                outState.putInt(KEY_VIEW_STATE, viewStateId!!)
            }
        }
    }

    fun onDestroy() {
        presenter!!.onDetachView()

        if (activity.isFinishing && (activity.retainPresenter() || activity.retainViewState())) {
            val holder: HolderFragment? = HolderFragment.getInstanceIfExist(activity)
            if (holder != null) {
                if (activity.retainPresenter()) {
                    holder.removePresenter(presenterId!!)
                }
                if (activity.retainViewState()) {
                    holder.removeViewState(viewStateId!!)
                }
            }
        }

        if (activity.isFinishing || !activity.retainPresenter()) {
            if (presenter is IAsyncPresenter<*>) {
                (presenter as IAsyncPresenter<*>).cancel()
            }
        }

        presenter = null
        viewState = null
    }

}
