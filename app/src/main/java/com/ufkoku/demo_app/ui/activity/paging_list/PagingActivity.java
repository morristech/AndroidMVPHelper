package com.ufkoku.demo_app.ui.activity.paging_list;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.ufkoku.demo_app.R;
import com.ufkoku.demo_app.ui.common.paging.IPagingView;
import com.ufkoku.demo_app.ui.common.paging.IPagingViewWrap;
import com.ufkoku.demo_app.ui.common.paging.PagingDelegate;
import com.ufkoku.demo_app.ui.common.paging.PagingPresenter;
import com.ufkoku.demo_app.ui.common.paging.PagingViewState;
import com.ufkoku.mvp.BaseMvpActivity;
import com.ufkoku.mvp.utils.view_injection.annotation.Layout;

import org.jetbrains.annotations.NotNull;

@Layout(R.layout.view_paging_list)
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

    @NotNull
    @Override
    public PagingViewState createNewViewState() {
        PagingViewState state = new PagingViewState();
        delegate.setViewState(state);
        return state;
    }

    @NotNull
    @Override
    public PagingPresenter createPresenter() {
        PagingPresenter presenter = new PagingPresenter();
        delegate.setPresenter(presenter);
        return presenter;
    }

    @Override
    public void createView() {
        super.createView();
        delegate.setTvInitData(findViewById(R.id.initData));
        delegate.setSwipeRefreshLayout(findViewById(R.id.swipeToRefresh));
        delegate.setRecyclerView(findViewById(R.id.recycler));
        delegate.setWaitView(findViewById(R.id.waitView));
    }

    @NotNull
    @Override
    public IPagingView getMvpView() {
        return delegateWrap;
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
