package com.ufkoku.demo_app.ui.activity.paging_list;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.ufkoku.demo_app.R;
import com.ufkoku.demo_app.ui.base.paging.IPagingView;
import com.ufkoku.demo_app.ui.base.paging.IPagingViewWrap;
import com.ufkoku.demo_app.ui.base.paging.PagingDelegate;
import com.ufkoku.demo_app.ui.base.paging.PagingPresenter;
import com.ufkoku.demo_app.ui.base.paging.PagingViewState;
import com.ufkoku.mvp.BaseMvpActivity;

import org.jetbrains.annotations.NotNull;

public class PagingActivity extends BaseMvpActivity<IPagingView, PagingPresenter, PagingViewState> {

    private PagingDelegate delegate = new PagingDelegate();

    private IPagingViewWrap delegateWrap = new IPagingViewWrap(delegate);

    @Override
    public boolean retainPresenter() {
        return true;
    }

    @Override
    public boolean retainViewState() {
        return true;
    }

    @Override
    public void createView() {
        setContentView(R.layout.view_paging_list);

        delegate.setTvInitData((TextView) findViewById(R.id.initData));
        delegate.setSwipeRefreshLayout((SwipeRefreshLayout) findViewById(R.id.swipeToRefresh));
        delegate.setRecyclerView((RecyclerView) findViewById(R.id.recycler));
        delegate.setWaitView(findViewById(R.id.waitView));
    }

    @NotNull
    @Override
    public IPagingView getMvpView() {
        return delegateWrap;
    }

    @NotNull
    @Override
    public PagingViewState createNewViewState() {
        return new PagingViewState();
    }

    @NotNull
    @Override
    public PagingPresenter createPresenter() {
        return new PagingPresenter();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        delegate.onAttach(this);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onInitialized(@NotNull PagingPresenter presenter, @NotNull PagingViewState viewState) {
        delegate.onInitialized(presenter, viewState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        delegate.onDestroyView();
        delegate.onDestroy();
        delegate.onDetach();
    }

}
