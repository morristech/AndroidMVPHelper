package com.ufkoku.demo_app.ui.activity.retainable.paging;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.ufkoku.demo_app.R;
import com.ufkoku.demo_app.ui.awesome_entity_paging.IPagingView;
import com.ufkoku.demo_app.ui.awesome_entity_paging.PagingDelegate;
import com.ufkoku.demo_app.ui.awesome_entity_paging.PagingPresenter;
import com.ufkoku.demo_app.ui.awesome_entity_paging.PagingViewState;
import com.ufkoku.mvp.retainable.BaseRetainableActivity;

import org.jetbrains.annotations.NotNull;

public class PagingActivity extends BaseRetainableActivity<IPagingView, PagingPresenter, PagingViewState> {

    private PagingDelegate delegate = new PagingDelegate();

    @Override
    public void createView() {
        setContentView(R.layout.paging);

        delegate.setTvInitData((TextView) findViewById(R.id.initData));
        delegate.setSwipeRefreshLayout((SwipeRefreshLayout) findViewById(R.id.swipeToRefresh));
        delegate.setRecyclerView((RecyclerView) findViewById(R.id.recycler));
        delegate.setWaitView(findViewById(R.id.waitView));
    }

    @NotNull
    @Override
    public IPagingView getMvpView() {
        return delegate;
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
