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

package com.ufkoku.mvp.presenter.rx

import com.ufkoku.mvp.presenter.BaseAsyncExecutorPresenter
import com.ufkoku.mvp_base.presenter.IAsyncPresenter
import rx.Scheduler
import rx.schedulers.Schedulers

abstract class BaseAsyncRxSchedulerPresenter<T : IAsyncPresenter.ITaskListener> : BaseAsyncExecutorPresenter<T>() {

    var scheduler: Scheduler? = null

    override fun onAttachView(view: T) {
        super.onAttachView(view)
        if (scheduler == null) {
            scheduler = Schedulers.from(executor)
        }
    }

    override fun cancel() {
        if (scheduler != null) {
            scheduler = null
        }
        super.cancel()
    }

}
