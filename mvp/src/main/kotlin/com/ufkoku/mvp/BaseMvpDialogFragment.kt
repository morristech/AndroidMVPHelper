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

package com.ufkoku.mvp

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.View
import com.ufkoku.mvp.base.IMvpFragment
import com.ufkoku.mvp.delegate.controller.FragmentDelegate
import com.ufkoku.mvp.delegate.observable.FragmentLifecycleObservable
import com.ufkoku.mvp.utils.NullerUtil
import com.ufkoku.mvp.utils.NullerUtil.nullAllFields
import com.ufkoku.mvp_base.presenter.IPresenter
import com.ufkoku.mvp_base.view.IMvpView
import com.ufkoku.mvp_base.viewstate.IViewState

@SuppressLint("LongLogTag")
abstract class BaseMvpDialogFragment<V : IMvpView, P : IPresenter<V>, VS : IViewState<V>> : DialogFragment(), IMvpFragment<V, P, VS> {

    companion object {
        private val TAG = "BaseMvpDialogFragment"
    }

    private val delegate: FragmentDelegate<BaseMvpDialogFragment<V, P, VS>, V, P, VS> = FragmentDelegate(this)

    private val lifecycleDelegate: FragmentLifecycleObservable = FragmentLifecycleObservable()

    protected val presenter: P?
        get() {
            return delegate.presenter
        }

    protected val viewState: VS?
        get() {
            return delegate.viewState
        }

    //---------------------------------------------------------------------------------------//

    override fun onAttach(context: Context) {
        super.onAttach(context)
        lifecycleDelegate.onAttach(this, context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        delegate.onCreate(savedInstanceState)
        lifecycleDelegate.onCreate(this, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        delegate.onViewCreated(view, savedInstanceState)
        lifecycleDelegate.onViewCreated(this, view, savedInstanceState)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        lifecycleDelegate.onActivityCreated(this, savedInstanceState)
    }

    override fun onStart() {
        super.onStart()
        lifecycleDelegate.onStart(this)
    }

    override fun onResume() {
        super.onResume()
        lifecycleDelegate.onResume(this)
    }

    override fun onPause() {
        lifecycleDelegate.onPause(this)
        super.onPause()
    }

    override fun onStop() {
        lifecycleDelegate.onStop(this)
        super.onStop()
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        delegate.onSaveInstanceState(outState)
        lifecycleDelegate.onSaveInstance(this, outState)
        super.onSaveInstanceState(outState)
    }

    override fun onDestroyView() {
        lifecycleDelegate.onDestroyView(this)
        delegate.onDestroyView()
        super.onDestroyView()
        if (nullViews()) {
            this.nullAllFields(View::class.java, BaseMvpDialogFragment::class.java)
        }
    }

    override fun onDestroy() {
        lifecycleDelegate.onDestroy(this)
        delegate.onDestroy()
        super.onDestroy()
    }

    override fun onDetach() {
        lifecycleDelegate.onDetach(this)
        super.onDetach()
    }

    //---------------------------------------------------------------------------------------//

    override fun subscribe(observer: Any) {
        lifecycleDelegate.subscribe(observer)
    }

    override fun unsubscribe(observer: Any) {
        lifecycleDelegate.unsubscribe(observer)
    }

}