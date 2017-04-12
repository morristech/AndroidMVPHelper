package com.ufkoku.demo_app.ui.fragments.retainable.static_list;

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
import com.ufkoku.demo_app.ui.fragments.savable.SavableFragment;
import com.ufkoku.mvp.retainable.BaseRetainableFragment;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class RetainableFragment extends BaseRetainableFragment<IRetainableFragment, StaticListPresenter<IRetainableFragment>, RetainableFragmentViewState> implements IRetainableFragment {

    private DataView view;

    private IRetainableFragmentWrap wrap = new IRetainableFragmentWrap(this);

    //-----------------------------------------------------------------------------------//

    @NotNull
    @Override
    public IRetainableFragment getMvpView() {
        return wrap;
    }

    @NotNull
    @Override
    public RetainableFragmentViewState createNewViewState() {
        return new RetainableFragmentViewState();
    }

    @NotNull
    @Override
    public StaticListPresenter<IRetainableFragment> createPresenter() {
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
                if (context instanceof IFragmentManager){
                    ((IFragmentManager) context).setFragment(new RetainableFragment());
                }
            }

            @Override
            public void onSavableClicked() {
                Context context = getContext();
                if (context instanceof IFragmentManager){
                    ((IFragmentManager) context).setFragment(new SavableFragment());
                }
            }
        });
        return view;
    }

    @Override
    public void onInitialized(StaticListPresenter<IRetainableFragment> presenter, RetainableFragmentViewState viewState) {
        if (!viewState.isApplied()) {
            if (!presenter.isTaskRunning(StaticListPresenter.TASK_FETCH_DATA)) {
                presenter.fetchData();
            }
        }

        updateProgressVisibility();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        view = null;
    }

    //-----------------------------------------------------------------------------------//

    @Override
    public void onDataLoaded(List<AwesomeEntity> entity) {
        RetainableFragmentViewState state = getViewState();
        if (state != null){
            state.setData(entity);
        }
        populateData(entity);
    }

    @Override
    public void populateData(List<AwesomeEntity> entity) {
        if (view != null){
            view.setWaitViewVisible(false);
            view.populateData(entity);
        }
    }

    @Override
    public void setWaitViewVisible(boolean visible) {
        if (view != null){
            view.setWaitViewVisible(visible);
        }
    }

    @Override
    public void onTaskStatusChanged(int taskId, int status) {
        updateProgressVisibility();
    }

    //---------------------------------------------------------------------------------//

    public void updateProgressVisibility() {
        StaticListPresenter<IRetainableFragment> presenter = getPresenter();
        if (presenter != null) {
            setWaitViewVisible(presenter.hasRunningTasks());
        }
    }

}
