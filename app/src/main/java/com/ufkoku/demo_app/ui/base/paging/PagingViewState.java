package com.ufkoku.demo_app.ui.base.paging;

import com.ufkoku.demo_app.entity.AwesomeEntity;
import com.ufkoku.mvp.list.BasePagingSearchableViewState;

import org.jetbrains.annotations.NotNull;

public class PagingViewState extends BasePagingSearchableViewState<AwesomeEntity, IPagingView> {

    private String initData = null;

    public String getInitData() {
        return initData;
    }

    public void setInitData(String initData) {
        this.initData = initData;
    }

    @Override
    public void apply(@NotNull IPagingView view) {
        if (initData != null) {
            view.setInitData(initData);
        }
        super.apply(view);
    }
}
