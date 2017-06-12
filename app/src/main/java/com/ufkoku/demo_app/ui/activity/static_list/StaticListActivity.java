package com.ufkoku.demo_app.ui.activity.static_list;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

import com.ufkoku.demo_app.R;
import com.ufkoku.demo_app.entity.AwesomeEntity;
import com.ufkoku.demo_app.ui.base.listeners.ActivityLifecycleObserver;
import com.ufkoku.demo_app.ui.base.presenter.StaticListPresenter;
import com.ufkoku.demo_app.ui.base.view.DataView;
import com.ufkoku.mvp.BaseMvpActivity;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class StaticListActivity extends BaseMvpActivity<IStaticListActivity, StaticListPresenter<IStaticListActivity>, StaticListActivityViewState> implements IStaticListActivity {

    protected static final String ARG_RETAIN = "com.ufkoku.demo_app.ui.activity.static_list.StaticListActivity.ARG_RETAIN";

    private DataView view;

    private IStaticListActivityWrap wrap = new IStaticListActivityWrap(this);

    private ActivityLifecycleObserver observer = new ActivityLifecycleObserver();

    {
        subscribe(observer);
    }

    //------------------------------------------------------------------------------------//

    @Override
    public boolean nullViews() {
        return true;
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    public boolean retainPresenter() {
        //don't do it in real life, return constant value
        Intent intent = getIntent();
        return intent != null && intent.getBooleanExtra(ARG_RETAIN, false);
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    public boolean retainViewState() {
        //don't do it in real life, return constant value
        Intent intent = getIntent();
        return intent != null && intent.getBooleanExtra(ARG_RETAIN, false);
    }

    @Override
    public void createView() {
        view = (DataView) getLayoutInflater().inflate(R.layout.view_data, null);
        view.setListener(new DataView.ViewListener() {
            @Override
            public void onRetainableClicked() {
                startActivity(new Builder(true).build(StaticListActivity.this));
            }

            @Override
            public void onSavableClicked() {
                startActivity(new Builder(false).build(StaticListActivity.this));
            }
        });
        setContentView(view);
    }

    @NotNull
    @Override
    public IStaticListActivity getMvpView() {
        return wrap;
    }

    @NotNull
    @Override
    public StaticListActivityViewState createNewViewState() {
        return new StaticListActivityViewState();
    }

    @NotNull
    @Override
    public StaticListPresenter<IStaticListActivity> createPresenter() {
        return new StaticListPresenter<>();
    }

    @Override
    public void onInitialized(@NonNull StaticListPresenter<IStaticListActivity> presenter, @NonNull StaticListActivityViewState viewState) {
        if (!viewState.isApplied()) {
            if (!presenter.isTaskRunning(StaticListPresenter.TASK_FETCH_DATA)) {
                presenter.fetchData();
            }
        }

        updateProgressVisibility();
    }

    //------------------------------------------------------------------------------------//

    @Override
    public void onDataLoaded(List<AwesomeEntity> entity) {
        StaticListActivityViewState state = getViewState();
        if (state != null) {
            state.setData(entity);
        }

        populateData(entity);
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
        StaticListPresenter<IStaticListActivity> presenter = getPresenter();
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

        public Intent build(Context context) {
            Intent intent = new Intent(context, StaticListActivity.class);

            intent.putExtra(ARG_RETAIN, retainElements);

            return intent;
        }

    }

}