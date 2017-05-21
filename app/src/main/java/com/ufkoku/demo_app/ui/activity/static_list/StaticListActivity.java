package com.ufkoku.demo_app.ui.activity.static_list;

import android.content.Context;
import android.content.Intent;

import com.ufkoku.demo_app.R;
import com.ufkoku.demo_app.entity.AwesomeEntity;
import com.ufkoku.demo_app.ui.base.presenter.StaticListPresenter;
import com.ufkoku.demo_app.ui.base.view.DataView;
import com.ufkoku.mvp.BaseMvpActivity;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class StaticListActivity extends BaseMvpActivity<IStaticListActivity, StaticListPresenter<IStaticListActivity>, StaticListActivityViewState> implements IStaticListActivity {

    protected static final String ARG_RETAIN = "com.ufkoku.demo_app.ui.activity.static_list.StaticListActivity.ARG_RETAIN";

    private DataView view;

    private IStaticListActivityWrap wrap = new IStaticListActivityWrap(this);

    //------------------------------------------------------------------------------------//

    @Override
    @SuppressWarnings("ConstantConditions")
    public boolean retainPresenter() {
        return getViewState().isRetain();
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    public boolean retainViewState() {
        return getViewState().isRetain();
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
        StaticListActivityViewState viewState = new StaticListActivityViewState();

        Intent intent = getIntent();
        if (intent != null) {
            viewState.setRetain(intent.getBooleanExtra(ARG_RETAIN, false));
        }

        return viewState;
    }

    @NotNull
    @Override
    public StaticListPresenter<IStaticListActivity> createPresenter() {
        return new StaticListPresenter<>();
    }

    @Override
    public void onInitialized(StaticListPresenter<IStaticListActivity> presenter, StaticListActivityViewState viewState) {
        if (!viewState.isApplied()) {
            if (!presenter.isTaskRunning(StaticListPresenter.TASK_FETCH_DATA)) {
                presenter.fetchData();
            }
        }

        updateProgressVisibility();
    }

    //------------------------------------------------------------------------------------//

    @Override
    protected void onDestroy() {
        super.onDestroy();
        view = null;
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