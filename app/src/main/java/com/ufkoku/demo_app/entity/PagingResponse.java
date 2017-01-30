package com.ufkoku.demo_app.entity;

import com.ufkoku.mvp.list.interfaces.IPagingResponse;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Created by Zwei on 30.01.2017.
 */

public class PagingResponse<I> implements IPagingResponse<I> {

    private final List<I> data;
    private final boolean canLoadMore;

    public PagingResponse(@NotNull List<I> data, boolean canLoadMore) {
        this.data = data;
        this.canLoadMore = canLoadMore;
    }

    @NotNull
    @Override
    public List<I> getData() {
        return data;
    }

    @Override
    public boolean getCanLoadMore() {
        return canLoadMore;
    }

}
