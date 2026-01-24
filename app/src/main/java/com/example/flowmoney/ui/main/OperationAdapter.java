package com.example.flowmoney.ui.main;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.flowmoney.R;
import com.example.flowmoney.data.entity.OperationEntity;

import java.util.ArrayList;
import java.util.List;

public class OperationAdapter extends RecyclerView.Adapter<OperationAdapter.OperationViewHolder> {

    private final List<OperationEntity> operations = new ArrayList<>();

    public void setOperations(List<OperationEntity> newOperations) {
        operations.clear();
        operations.addAll(newOperations);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public OperationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_operation, parent, false);
        return new OperationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OperationViewHolder holder, int position) {
        OperationEntity op = operations.get(position);

        holder.tvCategory.setText(op.category);
        holder.tvComment.setText(op.comment == null ? "" : op.comment);

        String sign = op.type.equals("INCOME") ? "+" : "-";
        holder.tvAmount.setText(sign + op.amount);

        int color = op.type.equals("INCOME")
                ? holder.itemView.getResources().getColor(android.R.color.holo_green_dark)
                : holder.itemView.getResources().getColor(android.R.color.holo_red_dark);

        holder.tvAmount.setTextColor(color);
    }

    @Override
    public int getItemCount() {
        return operations.size();
    }

    static class OperationViewHolder extends RecyclerView.ViewHolder {

        TextView tvCategory, tvComment, tvAmount;

        public OperationViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCategory = itemView.findViewById(R.id.tvCategory);
            tvComment = itemView.findViewById(R.id.tvComment);
            tvAmount = itemView.findViewById(R.id.tvAmount);
        }
    }
}
