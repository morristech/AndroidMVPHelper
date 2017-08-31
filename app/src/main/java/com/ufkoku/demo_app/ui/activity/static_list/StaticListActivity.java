package com.ufkoku.demo_app.ui.activity.static_list;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

import com.ufkoku.demo_app.R;
import com.ufkoku.demo_app.entity.AwesomeEntity;
import com.ufkoku.demo_app.ui.common.view_state.StaticListViewState;
import com.ufkoku.demo_app.ui.lifecycle_listeners.ActivityLifecycleObserver;
import com.ufkoku.demo_app.ui.common.presenter.StaticListPresenter;
import com.ufkoku.demo_app.ui.view.DataView;
import com.ufkoku.mvp.BaseMvpActivity;
import com.ufkoku.mvp.utils.view_injection.annotation.InjectView;
import com.ufkoku.mvp.utils.view_injection.annotation.Layout;

import org.jetbrains.annotations.NotNull;

import java.util.List;

@Layout(value = R.layout.view_data)
public class StaticListActivity extends BaseMvpActivity<IStaticListActivity, StaticListPresenter<IStaticListActivity>, StaticListViewState> implements IStaticListActivity {

    protected static final String ARG_RETAIN = "com.ufkoku.demo_app.ui.activity.static_list.StaticListActivity.ARG_RETAIN";

    @InjectView(R.id.view_data)
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
        super.createView();
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
    }

    @NotNull
    @Override
    public IStaticListActivity getMvpView() {
        return wrap;
    }

    @NotNull
    @Override
    public StaticListViewState createViewState() {
        return new StaticListViewState();
    }

    @NotNull
    @Override
    public StaticListPresenter<IStaticListActivity> createPresenter() {
        return new StaticListPresenter<>();
    }

    @Override
    public void onInitialized(@NonNull StaticListPresenter<IStaticListActivity> presenter, @NonNull StaticListViewState viewState) {
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
        StaticListViewState state = getViewState();
        if (state != null) {
            state.setEntities(entity);
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