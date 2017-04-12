package com.ufkoku.demo_app.ui.activity.retainable.static_list;

import com.ufkoku.demo_app.entity.AwesomeEntity;
import com.ufkoku.mvp_base.viewstate.IViewState;

import java.util.List;

public class RetainableViewState implements IViewState<IRetainableActivity> {

    private transient boolean applied = false;

    private List<AwesomeEntity> data;

    public List<AwesomeEntity> getData() {
        return data;
    }

    public void setData(List<AwesomeEntity> data) {
        this.data = data;
    }

    public boolean isApplied() {
        return applied;
    }

    @Override
    public void apply(IRetainableActivity retainableActivity) {
        if (data != null){
            applied = true;
            retainableActivity.populateData(data);
        }
    }

}
