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

package com.ufkoku.mvp_base.view

import com.ufkoku.mvp_base.presenter.IPresenter
import com.ufkoku.mvp_base.viewstate.IViewState

interface IMvpActivity<V : IMvpView, P : IPresenter<V>, VS : IViewState<V>> {

    /**
     * Call setContentView hear and init all UI variables
     * */
    fun createView()

    fun getMvpView(): V

    /**
     * Creating viewState
     * */
    fun createNewViewState(): VS

    /**
     * Creating presenter
     * */
    fun createPresenter(): P

    /**
     * This is methods is called when ui, view state and presenter are initialized, and viewState.apply() method called
     * */
    fun onInitialized(presenter: P, viewState: VS)

}