package com.ufkoku.demo_app.ui.common.paging;

import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.ufkoku.demo_app.entity.AwesomeEntity;
import com.ufkoku.demo_app.entity.PagingResponse;
import com.ufkoku.mvp.list.BasePagingSearchableDelegate;
import com.ufkoku.mvp.list.BasePagingSearchableViewState;
import com.ufkoku.mvp.list.util.StringUtils;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class PagingDelegate extends BasePagingSearchableDelegate<AwesomeEntity, PagingResponse<AwesomeEntity>, IPagingView, PagingPresenter, PagingViewState> implements IPagingView, PagingAdapter.PagingAdapterListener {

    protected TextView tvInitData;

    public void setTvInitData(TextView tvInitData) {
        this.tvInitData = tvInitData;
        if (getViewState() != null) {
            setInitData(getViewState().getInitData());
        }
    }

    public TextView getTvInitData() {
        return tvInitData;
    }

    protected void loadInitData() {
        PagingPresenter presenter = getPresenter();
        if (presenter != null) {
            presenter.getInitData();
        }
    }

    @Override
    protected void loadNextPage() {
        PagingPresenter presenter = getPresenter();
        PagingViewState state = getViewState();
        if (presenter != null && state != null) {
            presenter.getContent(state.getItems() != null ? state.getItems().size() : 0);
        }
    }

    @Override
    protected void loadFirstPage() {
        PagingPresenter presenter = getPresenter();
        if (presenter != null) {
            presenter.getContent(0);
        }
    }

    @NotNull
    @Override
    public PagingAdapter createPagingAdapter(@NotNull LayoutInflater inflater, @NotNull List<AwesomeEntity> items) {
        if (getRecyclerView() != null){
            getRecyclerView().setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        }
        PagingAdapter adapter = new PagingAdapter(inflater, items);
        adapter.setListener(this);
        return adapter;
    }

    @NotNull
    @Override
    public String getEmptyMessage(boolean isSearch) {
        return "Empty data for " + (isSearch ? "search" : "default") + " request";
    }

    @NotNull
    @Override
    public String getErrorMessage(boolean isSearch, int code) {
        return "Error with code " + code;
    }

    @Override
    public void onInitialized(@NonNull PagingPresenter presenter, @NonNull PagingViewState state) {
        if (state.getInitData() == null && state.getErrorCode() == PagingViewState.NO_ERROR_CODE) {
            this.setPresenter(presenter);
            this.setViewState(state);
            if (!presenter.isTaskRunning(PagingPresenter.TASK_LOAD_INIT_DATA)) {
                presenter.getInitData();
            }
            updateProgressVisibility();
        } else {
            super.onInitialized(presenter, state);
        }
    }

    @Override
    public void onInitDataLoaded(String data) {
        PagingViewState state = getViewState();
        if (state != null) {
            state.setErrorCode(BasePagingSearchableViewState.NO_ERROR_CODE);
            state.setInitData(data);
        }

        setInitData(data);

        loadFirstPage();
    }

    @Override
    public void onInitDataLoadFailed(int code) {
        PagingViewState state = getViewState();
        if (state != null) {
            state.setErrorCode(code);

            View vError = getVError();
            TextView tvErrorMessage = getTvErrorMessage();
            if (vError != null && tvErrorMessage != null) {
                tvErrorMessage.setText(getErrorMessage(StringUtils.INSTANCE.isNotNullOrEmpty(state.getQuery()), code));
                vError.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void setInitData(String data) {
        if (tvInitData != null) {
            tvInitData.setText(data);
        }
    }

    @Override
    public void onPickedItemProcessed(AwesomeEntity entity) {
        Toast.makeText(getActivity(), "Field processed " + entity.getImportantDataField(), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onErrorRetryButtonClicked() {
        PagingViewState state = getViewState();
        if (state != null) {
            if (state.getInitData() == null) {
                loadInitData();
            } else {
                super.onErrorRetryButtonClicked();
            }
        }
    }

    @Override
    public void onItemClicked(AwesomeEntity entity) {
        PagingPresenter presenter = getPresenter();
        if (presenter != null) {
            presenter.processPickedItem(entity);
        }
    }

    @Override
    public void updateProgressVisibility() {
        super.updateProgressVisibility();

        PagingPresenter presenter = getPresenter();
        if (presenter != null) {
            if (presenter.isAnyOfTasksRunning(PagingPresenter.TASK_LOAD_INIT_DATA, PagingPresenter.TASK_PROCESS_PICKED_DATA)) {
                View waitView = getWaitView();
                if (waitView != null) {
                    waitView.setVisibility(View.VISIBLE);
                }
            }
        }
    }

}
