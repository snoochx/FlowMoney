package com.example.flowmoney.ui.analytics;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.flowmoney.R;
import com.example.flowmoney.data.CategoryType;
import com.example.flowmoney.data.entity.OperationEntity;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AnalyticsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;

    private List<AnalyticsListItem> data;

    public AnalyticsAdapter() {}

    public void setData(List<AnalyticsListItem> newData) {
        data = newData != null ? newData : List.of();
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return data.get(position) instanceof AnalyticsHeaderItem ? TYPE_HEADER : TYPE_ITEM;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == TYPE_HEADER) {
            View view = inflater.inflate(R.layout.item_analytics_header, parent, false);
            return new HeaderViewHolder(view);
        } else {
            View view = inflater.inflate(R.layout.item_analytics, parent, false);
            return new ItemViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof HeaderViewHolder) {
            ((HeaderViewHolder) holder).tvHeader.setText(((AnalyticsHeaderItem) data.get(position)).title);
        } else if (holder instanceof ItemViewHolder) {
            ((ItemViewHolder) holder).bind(((AnalyticsOperationItem) data.get(position)).operation);
        }
    }

    @Override
    public int getItemCount() {
        return data != null ? data.size() : 0;
    }

    static class HeaderViewHolder extends RecyclerView.ViewHolder {
        TextView tvHeader;
        public HeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvHeader = itemView.findViewById(R.id.tvHeader);
        }
    }

    static class ItemViewHolder extends RecyclerView.ViewHolder {
        ImageView ivCategory;
        TextView tvCategory, tvAmount, tvTime;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            ivCategory = itemView.findViewById(R.id.ivCategory);
            tvCategory = itemView.findViewById(R.id.tvCategory);
            tvAmount = itemView.findViewById(R.id.tvAmount);
            tvTime = itemView.findViewById(R.id.tvTime);
        }

        public void bind(OperationEntity op) {
            CategoryType cat = CategoryType.fromTitle(op.category != null ? op.category : "Другое");

            tvCategory.setText(cat.title);

            Calendar cal = Calendar.getInstance();
            cal.setTime(new Date(op.date));
            tvTime.setText(String.format("%02d:%02d", cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE)));

            ivCategory.setImageResource(cat.iconRes);
            ivCategory.setColorFilter(Color.BLACK);
            ivCategory.setBackgroundResource(R.drawable.bg_category_circle);
            ivCategory.getBackground().setTint(Color.parseColor(cat.colorHex));

            if ("EXPENSE".equals(op.type)) {
                tvAmount.setText(String.format(Locale.US, "-%.2f", op.amount));
                tvAmount.setTextColor(Color.parseColor("#C62828"));
            } else if ("INCOME".equals(op.type)) {
                tvAmount.setText(String.format(Locale.US, "+%.2f", op.amount));
                tvAmount.setTextColor(Color.parseColor("#2E7D32"));
            } else {
                tvAmount.setText(String.format(Locale.US, "%.2f", op.amount));
                tvAmount.setTextColor(Color.BLACK);
            }
        }
    }
}
