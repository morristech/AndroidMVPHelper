package com.ufkoku.demo_app.ui.activity.paging_list;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;

import com.ufkoku.demo_app.R;
import com.ufkoku.demo_app.entity.AwesomeEntity;
import com.ufkoku.demo_app.entity.PagingResponse;
import com.ufkoku.demo_app.ui.common.paging.IPagingView;
import com.ufkoku.demo_app.ui.common.paging.IPagingViewWrap;
import com.ufkoku.demo_app.ui.common.paging.PagingDelegate;
import com.ufkoku.demo_app.ui.common.paging.PagingPresenter;
import com.ufkoku.demo_app.ui.common.paging.PagingViewState;
import com.ufkoku.mvp.list.BaseMvpListActivity;
import com.ufkoku.mvp.utils.view_injection.annotation.Layout;

import org.jetbrains.annotations.NotNull;

@Layout(R.layout.view_paging_list)
public class PagingActivity extends BaseMvpListActivity<AwesomeEntity, PagingResponse<AwesomeEntity>, PagingDelegate, IPagingView, PagingPresenter, PagingViewState> {

    private IPagingViewWrap delegateWrap = new IPagingViewWrap(getPagingDelegate());

    @Override
    public void createView() {
        super.createView();
        PagingDelegate pagingDelegate = getPagingDelegate();
        pagingDelegate.setTvInitData(findViewById(R.id.initData));
        pagingDelegate.setSwipeRefreshLayout(findViewById(R.id.swipeToRefresh));
        pagingDelegate.setRecyclerView(findViewById(R.id.recycler));
        pagingDelegate.setWaitView(findViewById(R.id.waitView));
    }

    @NotNull
    @Override
    public IPagingView getMvpView() {
        return delegateWrap;
    }

    @NotNull
    @Override
    public PagingDelegate createDelegate() {
        return new PagingDelegate();
    }

    @NotNull
    @Override
    public PagingPresenter createListPresenter() {
        return new PagingPresenter();
    }

    @NotNull
    @Override
    public PagingViewState createListViewState() {
        return new PagingViewState();
    }

}
