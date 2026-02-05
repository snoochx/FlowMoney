package com.example.flowmoney.ui.account;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.flowmoney.R;
import com.example.flowmoney.data.entity.AccountEntity;
import java.util.List;

public class AccountsPickerAdapter extends RecyclerView.Adapter<AccountsPickerAdapter.ViewHolder> {

    public interface OnAccountClickListener {
        void onAccountClick(AccountEntity account);
    }

    private final List<AccountEntity> accounts;
    private final OnAccountClickListener listener;

    public AccountsPickerAdapter(List<AccountEntity> accounts, OnAccountClickListener listener) {
        this.accounts = accounts;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_account_picker, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AccountEntity account = accounts.get(position);
        holder.tvName.setText(account.name);
        holder.itemView.setOnClickListener(v -> listener.onAccountClick(account));
    }

    @Override
    public int getItemCount() {
        return accounts.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName;
        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvAccountName);
        }
    }
}
