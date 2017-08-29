package com.ufkoku.demo_app.ui.fragments.paging_list;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.ufkoku.demo_app.R;
import com.ufkoku.demo_app.ui.common.paging.IPagingView;
import com.ufkoku.demo_app.ui.common.paging.IPagingViewWrap;
import com.ufkoku.demo_app.ui.common.paging.PagingDelegate;
import com.ufkoku.demo_app.ui.common.paging.PagingPresenter;
import com.ufkoku.demo_app.ui.common.paging.PagingViewState;
import com.ufkoku.mvp.BaseMvpFragment;
import com.ufkoku.mvp.utils.view_injection.annotation.Layout;

import org.jetbrains.annotations.NotNull;

@Layout(R.layout.view_paging_list)
public class PagingFragment

        extends BaseMvpFragment<IPagingView, PagingPresenter, PagingViewState> {

    private PagingDelegate delegate = createDelegate();

    private IPagingViewWrap delegateWrap = new IPagingViewWrap(delegate);

    protected PagingDelegate createDelegate() {
        return new PagingDelegate();
    }

    @NotNull
    @Override
    public IPagingView getMvpView() {
        return delegateWrap;
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
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        delegate.onAttach((Activity) context);
    }

    @Override
    public void onCreate(@org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        setRetainInstance(true);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NotNull View view, @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        delegate.setTvInitData(view.findViewById(R.id.initData));
        delegate.setSwipeRefreshLayout(view.findViewById(R.id.swipeToRefresh));
        delegate.setRecyclerView(view.findViewById(R.id.recycler));
        delegate.setWaitView(view.findViewById(R.id.waitView));

        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onInitialized(@NonNull PagingPresenter presenter, @NonNull PagingViewState viewState) {
        delegate.onInitialized(presenter, viewState);
    }

    @Override
    public void onDestroyView() {
        delegate.onDestroyView();
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        delegate.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        delegate.onDetach();
        super.onDetach();
    }

}
