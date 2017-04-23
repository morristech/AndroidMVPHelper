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
import android.support.v7.app.AppCompatActivity
import com.ufkoku.mvp.utils.HolderFragment
import com.ufkoku.mvp_base.presenter.IAsyncPresenter
import com.ufkoku.mvp_base.presenter.IPresenter
import com.ufkoku.mvp_base.view.IMvpActivity
import com.ufkoku.mvp_base.view.IMvpView
import com.ufkoku.mvp_base.viewstate.IViewState

open class ActivityDelegate<out A, V : IMvpView, P : IPresenter<V>, VS : IViewState<V>>(val activity: A)
where A : AppCompatActivity, A : IMvpActivity<V, P, VS> {

    companion object {
        private val KEY_PRESENTER = "presenterFragmentKey"
        private val KEY_VIEW_STATE = "viewStateFragmentKey"
    }

    protected var presenterId: Int? = null

    var presenter: P? = null
        protected set

    protected var viewStateId: Int? = null

    var viewState: VS? = null
        protected set

    //-----------------------------------------------------------------------------------------//

    @Suppress("UNCHECKED_CAST")
    fun onCreate(savedInstanceState: Bundle?) {
        val holder = HolderFragment.getInstance(activity)

        if (savedInstanceState != null && savedInstanceState.containsKey(KEY_PRESENTER)) {
            presenterId = savedInstanceState.getInt(KEY_PRESENTER)
            presenter = holder.getPresenter(presenterId!!) as P?
        }
        if (presenter == null) {
            presenter = activity.createPresenter()
            if (presenterId == null) {
                presenterId = holder.addPresenter(presenter!!)
            } else {
                holder.setPresenter(presenterId!!, presenter!!)
            }
        }

        if (savedInstanceState != null && savedInstanceState.containsKey(KEY_VIEW_STATE)) {
            viewStateId = savedInstanceState.getInt(KEY_VIEW_STATE)
            viewState = holder.getViewState(viewStateId!!) as VS?
        }
        if (viewState == null) {
            viewState = activity.createNewViewState()
            if (viewStateId == null) {
                viewStateId = holder.addViewState(viewState!!)
            } else {
                holder.setViewState(presenterId!!, viewState!!)
            }
        }

        activity.createView()

        viewState!!.apply(activity.getMvpView())
        presenter!!.onAttachView(activity.getMvpView())

        activity.onInitialized(presenter!!, viewState!!)
    }

    fun onSaveInstanceState(out: Bundle?) {
        if (out != null) {
            out.putInt(KEY_PRESENTER, presenterId!!)
            out.putInt(KEY_VIEW_STATE, viewStateId!!)
        }
    }

    fun onDestroy() {
        presenter!!.onDetachView()

        if (activity.isFinishing) {
            val holder = HolderFragment.getInstance(activity)
            holder.removePresenter(presenterId!!)
            holder.removeViewState(viewStateId!!)

            if (presenter is IAsyncPresenter<*>) {
                (presenter!! as IAsyncPresenter<*>).cancel()
            }
        }

        presenter = null
        viewState = null
    }

}