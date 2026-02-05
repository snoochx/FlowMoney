package com.example.flowmoney;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.flowmoney.ui.account.AccountsActivity;
import com.example.flowmoney.ui.analytics.AnalyticsActivity;
import com.example.flowmoney.ui.main.AddOperationActivity;
import com.example.flowmoney.ui.main.MainViewModel;
import com.example.flowmoney.data.database.AppDatabase;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private View fabAdd;
    private TextView tvBalanceAmount;
    private ImageView ivToggleBalance;
    private TextView tvMonthIncome, tvMonthExpense;
    private boolean isBalanceVisible = true;
    private double currentBalance = 0.0;

    private AppDatabase db;
    private MainViewModel viewModel;
    private ImageView btnAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvBalanceAmount = findViewById(R.id.tvBalanceAmount);
        ivToggleBalance = findViewById(R.id.ivToggleBalance);
        fabAdd = findViewById(R.id.fabAdd);
        btnAccount = findViewById(R.id.btnAccount);
        tvMonthIncome = findViewById(R.id.tvMonthIncome);
        tvMonthExpense = findViewById(R.id.tvMonthExpense);

        db = AppDatabase.getInstance(this);

        btnAccount.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AccountsActivity.class);
            startActivity(intent);
        });

        findViewById(R.id.analyticsContainer).setOnClickListener(v ->
                startActivity(new Intent(this, AnalyticsActivity.class))
        );

        ivToggleBalance.setOnClickListener(v -> {
            isBalanceVisible = !isBalanceVisible;
            tvBalanceAmount.setText(
                    isBalanceVisible ? formatBalance(currentBalance) : maskBalance(currentBalance)
            );
            ivToggleBalance.setImageResource(
                    isBalanceVisible ? R.drawable.ic_eye_open : R.drawable.ic_eye_closed
            );
        });


        View accountsContainer = findViewById(R.id.accountsContainer);
        accountsContainer.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AccountsActivity.class);
            startActivity(intent);
        });

        fabAdd.setOnClickListener(v ->
                startActivity(new Intent(this, AddOperationActivity.class))
        );

        viewModel = new ViewModelProvider(this).get(MainViewModel.class);
        viewModel.getMainAccount().observe(this, account -> {
            if (account == null) return;
            currentBalance = account.balance;
            tvBalanceAmount.setText(
                    isBalanceVisible ? formatBalance(currentBalance) : maskBalance(currentBalance)
            );
        });

        viewModel.getMonthIncome().observe(this, value -> {
            double month = value != null ? value : 0.0;
            tvMonthIncome.setText("+" + formatBalance(month));
        });

        viewModel.getMonthExpense().observe(this, value -> {
            double month = value != null ? value : 0.0;
            tvMonthExpense.setText("-" + formatBalance(month));
        });
    }

    private String formatBalance(double value) {
        return String.format(Locale.US, "%.2f", value);
    }

    private String maskBalance(double value) {
        return formatBalance(value).replaceAll(".", "•");
    }
}
