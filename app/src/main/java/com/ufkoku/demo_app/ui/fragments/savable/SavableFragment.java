package com.ufkoku.demo_app.ui.fragments.savable;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ufkoku.demo_app.R;
import com.ufkoku.demo_app.entity.AwesomeEntity;
import com.ufkoku.demo_app.ui.base.presenter.StaticListPresenter;
import com.ufkoku.demo_app.ui.base.view.DataView;
import com.ufkoku.demo_app.ui.fragments.base.IFragmentManager;
import com.ufkoku.demo_app.ui.fragments.retainable.static_list.RetainableFragment;
import com.ufkoku.mvp.savable.BaseSavableFragment;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class SavableFragment extends BaseSavableFragment<ISavableFragment, StaticListPresenter<ISavableFragment>, SavableFragmentViewState> implements ISavableFragment {

    private DataView view;

    //----------------------------------------------------------------------------------------//

    @NotNull
    @Override
    public ISavableFragment getMvpView() {
        return this;
    }


    @NotNull
    @Override
    public SavableFragmentViewState createNewViewState() {
        return new SavableFragmentViewState();
    }

    @NotNull
    @Override
    public StaticListPresenter<ISavableFragment> createPresenter() {
        return new StaticListPresenter<>();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = (DataView) inflater.inflate(R.layout.view_data, container, false);
        view.setListener(new DataView.ViewListener() {
            @Override
            public void onRetainableClicked() {
                Context context = getContext();
                if (context instanceof IFragmentManager) {
                    ((IFragmentManager) context).setFragment(new RetainableFragment());
                }
            }

            @Override
            public void onSavableClicked() {
                Context context = getContext();
                if (context instanceof IFragmentManager) {
                    ((IFragmentManager) context).setFragment(new SavableFragment());
                }
            }
        });
        return view;
    }

    @Override
    public void onInitialized(StaticListPresenter<ISavableFragment> presenter, SavableFragmentViewState viewState) {
        if (!viewState.isApplied()) {
            presenter.fetchData();
        }

        updateProgressVisibility();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        view = null;
    }

    //----------------------------------------------------------------------------------------//

    @Override
    public void onDataLoaded(List<AwesomeEntity> entities) {
        SavableFragmentViewState state = getViewState();
        if (state != null) {
            state.setEntities(entities);
        }
        populateData(entities);
    }

    @Override
    public void populateData(List<AwesomeEntity> entity) {
        if (view != null) {
            view.populateData(entity);
        }
    }

    @Override
    public void setWaitViewVisible(boolean visible) {
        if (view != null) {
            view.setWaitViewVisible(visible);
        }
    }

    @Override
    public void onTaskStatusChanged(int taskId, int status) {
        updateProgressVisibility();
    }

    //---------------------------------------------------------------------------------//

    public void updateProgressVisibility() {
        StaticListPresenter<ISavableFragment> presenter = getPresenter();
        if (presenter != null) {
            setWaitViewVisible(presenter.hasRunningTasks());
        }
    }

}
