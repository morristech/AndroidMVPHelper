package com.ufkoku.demo_app.ui.activity.savable;

import android.content.Intent;

import com.ufkoku.demo_app.R;
import com.ufkoku.demo_app.entity.AwesomeEntity;
import com.ufkoku.demo_app.ui.activity.retainable.static_list.RetainableActivity;
import com.ufkoku.demo_app.ui.base.presenter.StaticListPresenter;
import com.ufkoku.demo_app.ui.base.view.DataView;
import com.ufkoku.mvp.savable.BaseSavableActivity;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class SavableActivity extends BaseSavableActivity<ISavableActivity, StaticListPresenter<ISavableActivity>, SavableActivityViewState> implements ISavableActivity {

    private DataView view;

    private ISavableActivityWrap wrap = new ISavableActivityWrap(this);

    //------------------------------------------------------------------------------------//


    @Override
    public boolean retainPresenter() {
        return true;
    }

    @Override
    public void createView() {
        view = (DataView) getLayoutInflater().inflate(R.layout.view_data, null);
        view.setListener(new DataView.ViewListener() {
            @Override
            public void onRetainableClicked() {
                Intent intent = new Intent(SavableActivity.this, RetainableActivity.class);
                startActivity(intent);
            }

            @Override
            public void onSavableClicked() {
                Intent intent = new Intent(SavableActivity.this, SavableActivity.class);
                startActivity(intent);
            }
        });
        setContentView(view);
    }

    @NotNull
    @Override
    public ISavableActivity getMvpView() {
        return wrap;
    }

    @NotNull
    @Override
    public SavableActivityViewState createNewViewState() {
        return new SavableActivityViewState();
    }

    @NotNull
    @Override
    public StaticListPresenter<ISavableActivity> createPresenter() {
        return new StaticListPresenter<>();
    }

    @Override
    public void onInitialized(StaticListPresenter<ISavableActivity> presenter, SavableActivityViewState viewState) {
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
        SavableActivityViewState state = getViewState();
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
        StaticListPresenter<ISavableActivity> presenter = getPresenter();
        if (presenter != null) {
            setWaitViewVisible(presenter.hasRunningTasks());
        }
    }

}