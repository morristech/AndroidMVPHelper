package com.ufkoku.demo_app.ui.activity.retainable.static_list;

import android.content.Intent;

import com.ufkoku.demo_app.R;
import com.ufkoku.demo_app.entity.AwesomeEntity;
import com.ufkoku.demo_app.ui.activity.savable.SavableActivity;
import com.ufkoku.demo_app.ui.base.presenter.StaticListPresenter;
import com.ufkoku.demo_app.ui.base.view.DataView;
import com.ufkoku.mvp.retainable.BaseRetainableActivity;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class RetainableActivity extends BaseRetainableActivity<IRetainableActivity, StaticListPresenter<IRetainableActivity>, RetainableViewState> implements IRetainableActivity {

    private DataView view;

    private IRetainableActivityWrap wrap = new IRetainableActivityWrap(this);

    //---------------------------------------------------------------------------------//

    @Override
    public void createView() {
        view = (DataView) getLayoutInflater().inflate(R.layout.view_data, null);
        view.setListener(new DataView.ViewListener() {
            @Override
            public void onRetainableClicked() {
                Intent intent = new Intent(RetainableActivity.this, RetainableActivity.class);
                startActivity(intent);
            }

            @Override
            public void onSavableClicked() {
                Intent intent = new Intent(RetainableActivity.this, SavableActivity.class);
                startActivity(intent);
            }
        });
        setContentView(view);
    }

    @NotNull
    @Override
    public IRetainableActivity getMvpView() {
        return wrap;
    }

    @NotNull
    @Override
    public RetainableViewState createNewViewState() {
        return new RetainableViewState();
    }

    @NotNull
    @Override
    public StaticListPresenter<IRetainableActivity> createPresenter() {
        return new StaticListPresenter<>();
    }

    @Override
    public void onInitialized(StaticListPresenter<IRetainableActivity> oresenter, RetainableViewState viewState) {
        if (!viewState.isApplied()) {
            if (!oresenter.isTaskRunning(StaticListPresenter.TASK_FETCH_DATA)) {
                oresenter.fetchData();
            }
        }

        updateProgressVisibility();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        view = null;
    }

    //---------------------------------------------------------------------------------//

    @Override
    public void onDataLoaded(List<AwesomeEntity> entity) {
        RetainableViewState state = getViewState();
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
        StaticListPresenter<IRetainableActivity> presenter = getPresenter();
        if (presenter != null) {
            setWaitViewVisible(presenter.hasRunningTasks());
        }
    }

}
