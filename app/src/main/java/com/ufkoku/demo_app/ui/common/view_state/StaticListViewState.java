package com.ufkoku.demo_app.ui.common.view_state;

import android.os.Bundle;
import android.support.annotation.NonNull;

import com.ufkoku.demo_app.entity.AwesomeEntity;
import com.ufkoku.mvp.viewstate.autosavable.AutoSavable;
import com.ufkoku.mvp.viewstate.autosavable.Ignore;
import com.ufkoku.mvp_base.view.IMvpView;
import com.ufkoku.mvp_base.viewstate.IViewState;

import java.util.ArrayList;
import java.util.List;

@AutoSavable
public class StaticListViewState implements IViewState<StaticListViewState.StaticListViewStateView> {

    @Ignore
    private boolean applied = false;

    private ArrayList<AwesomeEntity> entities;

    public boolean isApplied() {
        return applied;
    }

    public List<AwesomeEntity> getEntities() {
        return entities;
    }

    public void setEntities(List<AwesomeEntity> entity) {
        this.entities = (ArrayList<AwesomeEntity>) entity;
    }

    @Override
    public void save(@NonNull Bundle bundle) {
        StaticListViewStateSaver.save(this, bundle);
    }

    @Override
    public void restore(@NonNull Bundle bundle) {
        StaticListViewStateSaver.restore(this, bundle);
    }

    @Override
    public void apply(@NonNull StaticListViewStateView iSavableFragment) {
        if (entities != null) {
            applied = true;
            iSavableFragment.populateData(entities);
        }
    }

    public interface StaticListViewStateView extends IMvpView {

        void populateData(List<AwesomeEntity> entities);

    }

}
