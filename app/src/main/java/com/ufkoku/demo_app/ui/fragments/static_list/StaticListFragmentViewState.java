package com.ufkoku.demo_app.ui.fragments.static_list;

import android.os.Bundle;
import android.support.annotation.NonNull;

import com.ufkoku.demo_app.entity.AwesomeEntity;
import com.ufkoku.mvp.viewstate.autosavable.AutoSavable;
import com.ufkoku.mvp.viewstate.autosavable.Ignore;
import com.ufkoku.mvp_base.viewstate.IViewState;

import java.util.List;

@AutoSavable
public class StaticListFragmentViewState implements IViewState<IStaticListFragment> {

    @Ignore
    private boolean applied = false;

    private List<AwesomeEntity> entities;

    public boolean isApplied() {
        return applied;
    }

    public List<AwesomeEntity> getEntities() {
        return entities;
    }

    public void setEntities(List<AwesomeEntity> entity) {
        this.entities = entity;
    }

    @Override
    public void save(@NonNull Bundle bundle) {
        StaticListFragmentViewStateSaver.save(this, bundle);
    }

    @Override
    public void restore(@NonNull Bundle bundle) {
        StaticListFragmentViewStateSaver.restore(this, bundle);
    }

    @Override
    public void apply(@NonNull IStaticListFragment iSavableFragment) {
        if (entities != null) {
            applied = true;
            iSavableFragment.populateData(entities);
        }
    }

}
