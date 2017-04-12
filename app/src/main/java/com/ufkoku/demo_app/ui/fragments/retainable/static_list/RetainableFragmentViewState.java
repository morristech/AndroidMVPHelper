package com.ufkoku.demo_app.ui.fragments.retainable.static_list;

import android.support.annotation.NonNull;

import com.ufkoku.demo_app.entity.AwesomeEntity;
import com.ufkoku.mvp_base.viewstate.IViewState;

import java.util.List;

public class RetainableFragmentViewState implements IViewState<IRetainableFragment> {

    private transient boolean applied = false;

    private List<AwesomeEntity> data;

    public List<AwesomeEntity> getData() {
        return data;
    }

    public void setData(List<AwesomeEntity> entity) {
        this.data = entity;
    }

    public boolean isApplied() {
        return applied;
    }

    @Override
    public void apply(@NonNull IRetainableFragment iRetainableFragment) {
        if (data != null) {
            applied = true;
            iRetainableFragment.populateData(data);
        }
    }

}
