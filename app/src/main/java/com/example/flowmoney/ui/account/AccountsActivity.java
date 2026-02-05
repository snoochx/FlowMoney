package com.example.flowmoney.ui.account;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.flowmoney.R;
import com.example.flowmoney.data.database.AppDatabase;
import com.example.flowmoney.data.entity.AccountEntity;

import java.util.List;

public class AccountsActivity extends AppCompatActivity {

    private LinearLayout llAccountsList;
    private LayoutInflater inflater;
    private AppDatabase db;
    private View fabCreateAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accounts);

        llAccountsList = findViewById(R.id.llAccountsList);
        inflater = LayoutInflater.from(this);
        db = AppDatabase.getInstance(this);

        fabCreateAccount = findViewById(R.id.fabCreateAccount);
        fabCreateAccount.setOnClickListener(v -> {
            Intent intent = new Intent(AccountsActivity.this, CreateAccountActivity.class);
            startActivity(intent);
        });

        db.accountDao().getAllLive().observe(this, this::renderAccountsList);
    }

    private void renderAccountsList(List<AccountEntity> accounts) {
        llAccountsList.removeAllViews();
        if (accounts == null || accounts.isEmpty()) return;

        for (AccountEntity account : accounts) {
            if (account == null) continue;

            View itemView = inflater.inflate(R.layout.item_account, llAccountsList, false);

            TextView tvName = itemView.findViewById(R.id.tvAccountName);
            TextView tvBalance = itemView.findViewById(R.id.tvAccountBalance);

            tvName.setText(account.name != null ? "\uD83D\uDCB8 " + account.name : "");
            tvBalance.setText(String.format(java.util.Locale.US, "%.2f", account.balance));

            if (account.id > 0) {
                int idToUse = account.id;
                itemView.setOnClickListener(v -> {
                    Intent intent = new Intent(AccountsActivity.this, AccountInfoActivity.class);
                    intent.putExtra("accountId", idToUse);
                    startActivity(intent);
                });
            }

            llAccountsList.addView(itemView);
        }
    }
}
