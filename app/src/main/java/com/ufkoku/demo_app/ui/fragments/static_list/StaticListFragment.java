package com.ufkoku.demo_app.ui.fragments.static_list;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import com.ufkoku.demo_app.R;
import com.ufkoku.demo_app.entity.AwesomeEntity;
import com.ufkoku.demo_app.ui.common.view_state.StaticListViewState;
import com.ufkoku.demo_app.ui.lifecycle_listeners.FragmentLifecycleObserver;
import com.ufkoku.demo_app.ui.common.presenter.StaticListPresenter;
import com.ufkoku.demo_app.ui.view.DataView;
import com.ufkoku.demo_app.ui.fragments.base.IFragmentManager;
import com.ufkoku.mvp.BaseMvpFragment;
import com.ufkoku.mvp.utils.view_injection.annotation.InjectView;
import com.ufkoku.mvp.utils.view_injection.annotation.Layout;

import org.jetbrains.annotations.NotNull;

import java.util.List;

@Layout(R.layout.view_data)
public class StaticListFragment extends BaseMvpFragment<IStaticListFragment, StaticListPresenter<IStaticListFragment>, StaticListViewState> implements IStaticListFragment {

    protected static final String ARG_RETAIN = "com.ufkoku.demo_app.ui.fragments.savable.StaticListFragment.retain";

    @InjectView(R.id.view_data)
    private DataView view;

    private IStaticListFragmentWrap wrap = new IStaticListFragmentWrap(this);

    private FragmentLifecycleObserver observer = new FragmentLifecycleObserver();

    {
        subscribe(observer);
    }

    //----------------------------------------------------------------------------------------//

    @Override
    public boolean nullViews() {
        return true;
    }

    @Override
    @SuppressWarnings({"ConstantConditions"})
    public boolean retainPresenter() {
        //don't do it in real life, return constant
        Bundle args = getArguments();
        return args != null && args.getBoolean(ARG_RETAIN);
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    public boolean retainViewState() {
        //don't do it in real life, return constant
        Bundle args = getArguments();
        return args != null && args.getBoolean(ARG_RETAIN);
    }

    @NotNull
    @Override
    public IStaticListFragment getMvpView() {
        return wrap;
    }

    @NotNull
    @Override
    public StaticListViewState createViewState() {
        return new StaticListViewState();
    }

    @NotNull
    @Override
    public StaticListPresenter<IStaticListFragment> createPresenter() {
        return new StaticListPresenter<>();
    }

    @Override
    public void onViewCreated(@NotNull View view, @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        this.view.setListener(new DataView.ViewListener() {
            @Override
            public void onRetainableClicked() {
                Context context = getContext();
                if (context instanceof IFragmentManager) {
                    ((IFragmentManager) context).setFragment(new Builder(true).build());
                }
            }

            @Override
            public void onSavableClicked() {
                Context context = getContext();
                if (context instanceof IFragmentManager) {
                    ((IFragmentManager) context).setFragment(new Builder(false).build());
                }
            }
        });

        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onInitialized(StaticListPresenter<IStaticListFragment> presenter, StaticListViewState viewState) {
        if (!viewState.isApplied()) {
            if (!presenter.isTaskRunning(StaticListPresenter.TASK_FETCH_DATA)) {
                presenter.fetchData();
            }
        }

        updateProgressVisibility();
    }

    //----------------------------------------------------------------------------------------//

    @Override
    public void onDataLoaded(List<AwesomeEntity> entities) {
        StaticListViewState state = getViewState();
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
        StaticListPresenter<IStaticListFragment> presenter = getPresenter();
        if (presenter != null) {
            setWaitViewVisible(presenter.hasRunningTasks());
        }
    }

    //---------------------------------------------------------------------------------//

    public static class Builder {

        private boolean retainElements = false;

        public Builder() {
        }

        public Builder(boolean retainElements) {
            this.retainElements = retainElements;
        }

        public boolean isRetainElements() {
            return retainElements;
        }

        public void setRetainElements(boolean retainElements) {
            this.retainElements = retainElements;
        }

        public StaticListFragment build() {
            StaticListFragment fragment = new StaticListFragment();

            Bundle args = new Bundle();
            args.putBoolean(ARG_RETAIN, retainElements);
            fragment.setArguments(args);

            return fragment;
        }

    }

}