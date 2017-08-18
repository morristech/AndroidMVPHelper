package com.ufkoku.demo_app.ui.view;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import com.ufkoku.demo_app.R;
import com.ufkoku.demo_app.entity.AwesomeEntity;
import com.ufkoku.demo_app.ui.view.adapter.DataAdapter;

import java.util.List;

public class DataView extends FrameLayout implements IDataView {

    private ViewListener listener;

    private RecyclerView recyclerView;

    private View vWaitView;

    public DataView(Context context) {
        super(context);
    }

    public DataView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DataView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        recyclerView = (RecyclerView) findViewById(R.id.recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        vWaitView = findViewById(R.id.waitView);

        findViewById(R.id.retainable).setOnClickListener(view -> {
            if (listener != null) {
                listener.onRetainableClicked();
            }
        });
        findViewById(R.id.savable).setOnClickListener(view -> {
            if (listener != null) {
                listener.onSavableClicked();
            }
        });
    }

    public void setListener(ViewListener listener) {
        this.listener = listener;
    }

    @Override
    public void populateData(final List<AwesomeEntity> entities) {
        recyclerView.post(new Runnable() {
            @Override
            public void run() {
                recyclerView.setAdapter(new DataAdapter(getContext(), entities));
            }
        });
    }

    @Override
    public void setWaitViewVisible(boolean visible) {
        vWaitView.setVisibility(visible ? VISIBLE : GONE);
    }

    public interface ViewListener {

        void onRetainableClicked();

        void onSavableClicked();

    }

}
