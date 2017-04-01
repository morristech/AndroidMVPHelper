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

package com.ufkoku.mvp.presenter

import com.ufkoku.mvp_base.presenter.IAsyncPresenter
import com.ufkoku.mvp_base.view.IMvpView
import java.util.concurrent.ExecutorService

abstract class BaseAsyncExecutorPresenter<T : IMvpView> : BaseAsyncPresenter<T>(), IAsyncPresenter<T> {

    protected var executor: ExecutorService? = null

    protected abstract fun createExecutor(): ExecutorService

    override fun onAttachView(view: T) {
        super.onAttachView(view)

        if (executor == null) {
            executor = createExecutor()
        }
    }

    override fun onDetachView() {
        super.onDetachView()
    }

    override fun cancel() {
        if (executor != null) {
            executor!!.shutdownNow()
            executor = null
        }
    }

}
