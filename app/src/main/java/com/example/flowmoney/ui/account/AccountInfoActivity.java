package com.example.flowmoney.ui.account;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.flowmoney.R;
import com.example.flowmoney.data.database.AppDatabase;
import com.example.flowmoney.data.entity.AccountEntity;

public class AccountInfoActivity extends AppCompatActivity {

    private int accountId;
    private AccountEntity account;
    private TextView tvAccountName, tvAccountBalance;
    private LinearLayout btnTransfer, btnCloseAccount;
    private AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_info);

        accountId = getIntent().getIntExtra("accountId", -1);
        Log.d("AccountInfoActivity", "onCreate: accountId=" + accountId + " intent=" + getIntent());
        if (accountId == -1) {
            Toast.makeText(this, "Ошибка: счет не найден", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        db = AppDatabase.getInstance(this);
        Log.d("AccountInfoActivity", "DB instance: " + (db != null));

        tvAccountName = findViewById(R.id.tvAccountName);
        tvAccountBalance = findViewById(R.id.tvAccountBalance);
        btnTransfer = findViewById(R.id.btnTransfer);
        btnCloseAccount = findViewById(R.id.btnCloseAccount);

        loadAccountInfo();

        btnTransfer.setOnClickListener(v -> {
            if (account == null) return;
            Intent intent = new Intent(this, TransferActivity.class);
            intent.putExtra("accountId", accountId);
            startActivity(intent);
        });

        btnCloseAccount.setOnClickListener(v -> {
            if (account == null) return;

            new Thread(() -> {
                if (account.balance > 0) {
                    AccountEntity main = db.accountDao().getMainAccountSync();
                    if (main != null) {
                        main.balance += account.balance;
                        db.accountDao().update(main);
                    }
                }
                db.accountDao().delete(account);
                runOnUiThread(this::finish);
            }).start();
        });
    }

    private void loadAccountInfo() {
        db.accountDao().getByIdLive(accountId).observe(this, account -> {
            if (account == null) {
                finish();
                return;
            }

            this.account = account;

            tvAccountName.setText(account.name);
            tvAccountBalance.setText(String.format(java.util.Locale.US, "%.2f", account.balance));
        });
    }
}
