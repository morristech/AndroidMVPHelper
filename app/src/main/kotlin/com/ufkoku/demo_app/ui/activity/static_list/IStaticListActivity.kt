package com.ufkoku.demo_app.ui.activity.static_list

import com.ufkoku.demo_app.ui.common.presenter.StaticListPresenter
import com.ufkoku.demo_app.ui.common.view_state.StaticListViewState
import com.ufkoku.mvp.view.wrap.Wrap

@Wrap
interface IStaticListActivity : StaticListViewState.StaticListViewStateView, StaticListPresenter.PresenterListener
