package com.ufkoku.demo_app.ui.base.paging;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ufkoku.demo_app.R;
import com.ufkoku.demo_app.entity.AwesomeEntity;
import com.ufkoku.mvp.list.BasePagingAdapter;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class PagingAdapter extends BasePagingAdapter<AwesomeEntity, PagingAdapter.PagingAdapterListener> {

    public PagingAdapter(@NotNull LayoutInflater inflater, @NotNull List<AwesomeEntity> items) {
        super(inflater, items);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case BasePagingAdapter.TYPE_ITEM: {
                return new ItemViewHolder(getInflater().inflate(R.layout.list_item, parent, false));
            }
            case BasePagingAdapter.TYPE_LOAD_MANUALLY: {
                return new LoadFailedViewHolder(getInflater().inflate(R.layout.list_item_load_failed, parent, false));
            }
            case BasePagingAdapter.TYPE_LOADER: {
                return new RecyclerView.ViewHolder(getInflater().inflate(R.layout.list_item_loader, parent, false)) {
                };
            }
            default: {
                throw new IllegalArgumentException("Unsupported view type " + viewType);
            }
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        int type = getItemViewType(position);
        if (type == TYPE_ITEM) {
            ((ItemViewHolder) holder).bind(getItems().get(position));
        }
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {

        private AwesomeEntity binded;

        private TextView tvTitle;

        public ItemViewHolder(View itemView) {
            super(itemView);

            tvTitle = (TextView) itemView;

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (getListener() != null && binded != null){
                        getListener().onItemClicked(binded);
                    }
                }
            });
        }

        public void bind(AwesomeEntity binded) {
            this.binded = binded;
            tvTitle.setText(binded.getImportantDataField() + "");
        }

    }

    public class LoadFailedViewHolder extends RecyclerView.ViewHolder {

        public LoadFailedViewHolder(View itemView) {
            super(itemView);
            itemView.findViewById(R.id.retryLoad).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (getListener() != null) {
                        getListener().loadNextPageClicked();
                    }
                }
            });
        }

    }

    public interface PagingAdapterListener extends BasePagingAdapter.AdapterListener {

        void onItemClicked(AwesomeEntity entity);

    }

}
