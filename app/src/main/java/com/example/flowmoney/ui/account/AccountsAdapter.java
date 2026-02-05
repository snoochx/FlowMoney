package com.example.flowmoney.ui.account;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.flowmoney.R;
import com.example.flowmoney.data.entity.AccountEntity;

import java.util.List;

public class AccountsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<AccountEntity> accounts;
    private final Activity activity;

    private static final int TYPE_ACCOUNT = 0;
    private static final int TYPE_EMPTY = 1;

    public AccountsAdapter(Activity activity, List<AccountEntity> accounts) {
        this.activity = activity;
        this.accounts = accounts;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_account, parent, false);
        if (viewType == TYPE_ACCOUNT) {
            return new AccountViewHolder(view);
        } else {
            view.setClickable(false);
            view.setFocusable(false);
            return new EmptyViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == TYPE_ACCOUNT) {
            AccountEntity acc = accounts.get(position);
            ((AccountViewHolder) holder).bind(acc);
        } else {
            ((EmptyViewHolder) holder).bind();
        }
    }

    @Override
    public int getItemCount() {
        if (accounts == null || accounts.isEmpty()) return 1;
        return accounts.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (accounts == null || accounts.isEmpty()) return TYPE_EMPTY;
        return TYPE_ACCOUNT;
    }

    class AccountViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvBalance;

        public AccountViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvAccountName);
            tvBalance = itemView.findViewById(R.id.tvAccountBalance);
        }

        void bind(AccountEntity account) {
            if (account == null) return;

            tvName.setText(account.name != null ? account.name : "");
            tvBalance.setText(String.format("Баланс: %.2f", account.balance));

            itemView.setOnClickListener(v -> {
                Intent intent = new Intent(activity, AccountInfoActivity.class);
                intent.putExtra("accountId", account.id);
                activity.startActivity(intent);
            });
        }
    }


    class EmptyViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvBalance;

        public EmptyViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvAccountName);
            tvBalance = itemView.findViewById(R.id.tvAccountBalance);
        }

        void bind() {
            tvName.setText("У вас нет активных счетов");
            tvName.setTextColor(activity.getResources().getColor(R.color.gray));
            if (tvBalance != null) tvBalance.setVisibility(View.GONE);
            itemView.setOnClickListener(null);
        }
    }
}
