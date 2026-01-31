package com.example.flowmoney.ui.account;

import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.flowmoney.R;
import com.example.flowmoney.data.database.AppDatabase;
import com.example.flowmoney.data.entity.AccountEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

public class TransferActivity extends AppCompatActivity {

    private TextView tvFromAccount, tvToAccount;
    private EditText etTransferAmount;
    private Button btnSendTransfer;
    private ImageView btnSwapAccounts;

    private LinearLayout fromContainer, toContainer;
    private AppDatabase db;

    private List<AccountEntity> accountsList = new ArrayList<>();
    private AccountEntity fromAccount, toAccount;
    private AccountEntity mainBalanceAccount; // виртуальный основной баланс

    private double mainBalance = 0.0; // текущий баланс из MainActivity через intent

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transfer);

        mainBalance = getIntent().getDoubleExtra("mainBalance", 0.0);

        db = AppDatabase.getInstance(this);

        tvFromAccount = findViewById(R.id.tvFromAccount);
        tvToAccount = findViewById(R.id.tvToAccount);
        etTransferAmount = findViewById(R.id.etTransferAmount);
        btnSendTransfer = findViewById(R.id.btnSendTransfer);
        btnSwapAccounts = findViewById(R.id.btnSwapAccounts);

        fromContainer = findViewById(R.id.fromAccountContainer);
        toContainer = findViewById(R.id.toAccountContainer);

        db.accountDao().getAllLive().observe(this, accounts -> {
            accountsList.clear();
            if (accounts != null) accountsList.addAll(accounts);
            AccountEntity mainAccount = null;
            for (AccountEntity acc : accountsList) {
                if (acc.isMain) {
                    mainAccount = acc;
                    break;
                }
            }

            if (fromAccount == null && mainAccount != null) {
                fromAccount = mainAccount;
                tvFromAccount.setText(fromAccount.name);
            }
            if (toAccount == null) {
                // Берём первый счёт не основной
                for (AccountEntity acc : accountsList) {
                    if (!acc.isMain) {
                        toAccount = acc;
                        tvToAccount.setText(toAccount.name);
                        break;
                    }
                }
            }
        });

        // Swap кнопка с анимацией
        btnSwapAccounts.setOnClickListener(v -> {
            ObjectAnimator rotate = ObjectAnimator.ofFloat(btnSwapAccounts, "rotation", 0f, 180f);
            rotate.setDuration(200);
            rotate.start();
            swapAccounts();
        });

        // Клик по счетам для выбора
        fromContainer.setOnClickListener(v -> showAccountPicker(true));
        toContainer.setOnClickListener(v -> showAccountPicker(false));

        // Кнопка перевода
        btnSendTransfer.setOnClickListener(v -> sendTransfer());
    }

    private void swapAccounts() {
        AccountEntity temp = fromAccount;
        fromAccount = toAccount;
        toAccount = temp;

        tvFromAccount.setText(fromAccount.name);
        tvToAccount.setText(toAccount.name);
    }

    private void showAccountPicker(boolean isFrom) {
        if (accountsList.isEmpty()) return;

        String[] names = new String[accountsList.size()];
        for (int i = 0; i < accountsList.size(); i++) names[i] = accountsList.get(i).name;

        new AlertDialog.Builder(this)
                .setTitle("Выберите счет")
                .setItems(names, (dialog, which) -> {
                    if (isFrom) {
                        fromAccount = accountsList.get(which);
                        tvFromAccount.setText(fromAccount.name);
                    } else {
                        toAccount = accountsList.get(which);
                        tvToAccount.setText(toAccount.name);
                    }
                })
                .show();
    }

    private void sendTransfer() {
        String amountStr = etTransferAmount.getText().toString().trim();
        if (amountStr.isEmpty()) {
            Toast.makeText(this, "Введите сумму", Toast.LENGTH_SHORT).show();
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(amountStr);
        } catch (Exception e) {
            Toast.makeText(this, "Некорректная сумма", Toast.LENGTH_SHORT).show();
            return;
        }

        if (fromAccount == null || toAccount == null) {
            Toast.makeText(this, "Выберите оба счета", Toast.LENGTH_SHORT).show();
            return;
        }

        if (fromAccount.id == toAccount.id) {
            Toast.makeText(this, "Счета должны быть разными", Toast.LENGTH_SHORT).show();
            return;
        }

        if (fromAccount.id == 0 && amount > mainBalance) {
            Toast.makeText(this, "Недостаточно средств на основном балансе", Toast.LENGTH_SHORT).show();
            return;
        }

        if (fromAccount.id != 0 && amount > fromAccount.balance) {
            Toast.makeText(this, "Недостаточно средств на счете", Toast.LENGTH_SHORT).show();
            return;
        }

        Executors.newSingleThreadExecutor().execute(() -> {
            // перевод
            if (fromAccount.id == 0) mainBalance -= amount;
            else fromAccount.balance -= amount;

            if (toAccount.id == 0) mainBalance += amount;
            else toAccount.balance += amount;

            if (fromAccount.id != 0) db.accountDao().update(fromAccount);
            if (toAccount.id != 0) db.accountDao().update(toAccount);

            runOnUiThread(() -> {
                Toast.makeText(this, "Перевод выполнен", Toast.LENGTH_SHORT).show();
                finish();
            });
        });
    }
}
