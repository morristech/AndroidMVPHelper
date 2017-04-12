package com.ufkoku.demo_app.ui.activity.savable;

import android.os.Bundle;

import com.ufkoku.demo_app.entity.AwesomeEntity;
import com.ufkoku.mvp.viewstate.autosavable.AutoSavable;
import com.ufkoku.mvp_base.viewstate.ISavableViewState;

import java.util.List;

@AutoSavable
public class SavableActivityViewState implements ISavableViewState<ISavableActivity> {

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
    public void save(Bundle bundle) {
        SavableActivityViewStateSaver.save(this, bundle);
    }

    @Override
    public void restore(Bundle bundle) {
        SavableActivityViewStateSaver.restore(this, bundle);
    }

    @Override
    public void apply(ISavableActivity savableActivity) {
        if (data != null) {
            applied = true;
            savableActivity.populateData(data);
            savableActivity.setWaitViewVisible(false);
        }
    }

}
