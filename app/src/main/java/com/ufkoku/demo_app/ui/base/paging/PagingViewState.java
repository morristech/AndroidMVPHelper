package com.ufkoku.demo_app.ui.base.paging;

import android.os.Bundle;

import com.ufkoku.demo_app.entity.AwesomeEntity;
import com.ufkoku.mvp.list.BasePagingSearchableViewState;
import com.ufkoku.mvp.viewstate.autosavable.AutoSavable;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@AutoSavable
public class PagingViewState extends BasePagingSearchableViewState<AwesomeEntity, IPagingView> {

    private String initData = null;

    private List<AwesomeEntity> items;

    public String getInitData() {
        return initData;
    }

    public void setInitData(String initData) {
        this.initData = initData;
    }

    @Nullable
    @Override
    public List<AwesomeEntity> getItems() {
        return items;
    }

    @Override
    public void setItems(@Nullable List<AwesomeEntity> list) {
        items = list;
    }

    @Override
    public void save(@NotNull Bundle out) {
        PagingViewStateSaver.save(this, out);
    }

    @Override
    public void restore(@NotNull Bundle inState) {
        PagingViewStateSaver.restore(this, inState);
    }

    @Override
    public void apply(@NotNull IPagingView view) {
        if (initData != null) {
            view.setInitData(initData);
        }
        super.apply(view);
    }

}
