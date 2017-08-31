package com.ufkoku.demo_app.ui.activity.static_list;

import com.ufkoku.demo_app.ui.common.presenter.StaticListPresenter;
import com.ufkoku.demo_app.ui.common.view_state.StaticListViewState;
import com.ufkoku.demo_app.ui.view.IDataView;
import com.ufkoku.mvp.view.wrap.Wrap;

@Wrap
public interface IStaticListActivity extends IDataView, StaticListViewState.StaticListViewStateView, StaticListPresenter.PresenterListener {

}
