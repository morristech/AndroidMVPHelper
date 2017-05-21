package com.ufkoku.demo_app.ui.fragments.paging_list;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ufkoku.demo_app.R;
import com.ufkoku.demo_app.ui.base.paging.IPagingView;
import com.ufkoku.demo_app.ui.base.paging.IPagingViewWrap;
import com.ufkoku.demo_app.ui.base.paging.PagingDelegate;
import com.ufkoku.demo_app.ui.base.paging.PagingPresenter;
import com.ufkoku.demo_app.ui.base.paging.PagingViewState;
import com.ufkoku.mvp.BaseMvpFragment;

import org.jetbrains.annotations.NotNull;

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
        return new PagingViewState();
    }

    @NotNull
    @Override
    public PagingPresenter createPresenter() {
        return new PagingPresenter();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        delegate.onAttach((Activity) context);
    }

    @Override
    public void onCreate(@org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        setRetainInstance(true);
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.view_paging_list, container, false);

        delegate.setTvInitData((TextView) view.findViewById(R.id.initData));
        delegate.setSwipeRefreshLayout((SwipeRefreshLayout) view.findViewById(R.id.swipeToRefresh));
        delegate.setRecyclerView((RecyclerView) view.findViewById(R.id.recycler));
        delegate.setWaitView(view.findViewById(R.id.waitView));

        return view;
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
