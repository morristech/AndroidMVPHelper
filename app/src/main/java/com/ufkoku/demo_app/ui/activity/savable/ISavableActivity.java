package com.ufkoku.demo_app.ui.activity.savable;

import com.ufkoku.demo_app.ui.base.presenter.StaticListPresenter;
import com.ufkoku.demo_app.ui.base.view.IDataView;
import com.ufkoku.mvp.view.wrap.Wrap;
import com.ufkoku.mvp_base.view.IMvpView;

@Wrap
public interface ISavableActivity extends IMvpView, IDataView, StaticListPresenter.PresenterListener {

}
