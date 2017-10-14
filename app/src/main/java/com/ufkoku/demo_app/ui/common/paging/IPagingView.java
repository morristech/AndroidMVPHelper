package com.ufkoku.demo_app.ui.common.paging;

import com.ufkoku.demo_app.entity.AwesomeEntity;
import com.ufkoku.demo_app.entity.PagingResponse;
import com.ufkoku.mvp.list.interfaces.IPagingSearchableView;
import com.ufkoku.mvp.view.wrap.Wrap;

@Wrap
public interface IPagingView extends IPagingSearchableView<AwesomeEntity, PagingResponse<AwesomeEntity>>, PagingPresenter.IPresenterView, PagingViewState.IViewStateView, PagingAdapter.PagingAdapterListener {

}