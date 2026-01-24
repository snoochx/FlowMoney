package com.example.flowmoney;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.flowmoney.data.entity.SavingHistoryEntity;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class SavingHistoryAdapter extends RecyclerView.Adapter<SavingHistoryAdapter.HistoryViewHolder> {

    private List<SavingHistoryEntity> historyList;

    public SavingHistoryAdapter(List<SavingHistoryEntity> historyList) {
        this.historyList = historyList;
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_saving_history, parent, false);
        return new HistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        SavingHistoryEntity item = historyList.get(position);
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault());
        holder.tvDate.setText(sdf.format(item.timestamp));
        holder.tvAmount.setText(String.format(Locale.US, "+%.2f", item.amount));
    }

    @Override
    public int getItemCount() {
        return historyList.size();
    }

    static class HistoryViewHolder extends RecyclerView.ViewHolder {
        TextView tvDate, tvAmount;

        public HistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDate = itemView.findViewById(R.id.tvHistoryDate);
            tvAmount = itemView.findViewById(R.id.tvHistoryAmount);
        }
    }
}
