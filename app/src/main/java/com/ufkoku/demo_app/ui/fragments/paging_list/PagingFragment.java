package com.ufkoku.demo_app.ui.fragments.paging_list;

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
import com.ufkoku.mvp.list.BaseMvpListFragment;
import com.ufkoku.mvp.utils.view_injection.annotation.Layout;

import org.jetbrains.annotations.NotNull;

@Layout(R.layout.view_paging_list)
public class PagingFragment
        extends BaseMvpListFragment<AwesomeEntity, PagingResponse<AwesomeEntity>, PagingDelegate, IPagingView, PagingPresenter, PagingViewState> {

    private IPagingViewWrap delegateWrap = new IPagingViewWrap(getPagingDelegate());

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

    @Override
    public void setupViewsToDelegate(@NotNull View view, @NonNull PagingDelegate delegate) {
        delegate.setTvInitData(view.findViewById(R.id.initData));
        delegate.setSwipeRefreshLayout(view.findViewById(R.id.swipeToRefresh));
        delegate.setRecyclerView(view.findViewById(R.id.recycler));
        delegate.setWaitView(view.findViewById(R.id.waitView));
    }

}
