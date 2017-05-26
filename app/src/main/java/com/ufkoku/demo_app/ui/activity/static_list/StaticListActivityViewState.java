package com.ufkoku.demo_app.ui.activity.static_list;

import android.os.Bundle;
import android.support.annotation.NonNull;

import com.ufkoku.demo_app.entity.AwesomeEntity;
import com.ufkoku.mvp.viewstate.autosavable.AutoSavable;
import com.ufkoku.mvp.viewstate.autosavable.Ignore;
import com.ufkoku.mvp_base.viewstate.IViewState;

import java.util.List;

@AutoSavable
public class StaticListActivityViewState implements IViewState<IStaticListActivity> {

    @Ignore
    private boolean applied = false;

    private List<AwesomeEntity> data;

    public boolean isApplied() {
        return applied;
    }

    public List<AwesomeEntity> getData() {
        return data;
    }

    public void setData(List<AwesomeEntity> data) {
        this.data = data;
    }

    @Override
    public void save(@NonNull Bundle bundle) {
        StaticListActivityViewStateSaver.save(this, bundle);
    }

    @Override
    public void restore(@NonNull Bundle bundle) {
        StaticListActivityViewStateSaver.restore(this, bundle);
    }

    @Override
    public void apply(@NonNull IStaticListActivity savableActivity) {
        if (data != null) {
            applied = true;
            savableActivity.populateData(data);
            savableActivity.setWaitViewVisible(false);
        }
    }

}
