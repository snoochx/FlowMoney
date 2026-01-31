package com.example.flowmoney;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.flowmoney.ui.account.AccountInfoActivity;
import com.example.flowmoney.ui.account.AccountsAdapter;
import com.example.flowmoney.ui.account.CreateAccountActivity;
import com.example.flowmoney.ui.analytics.AnalyticsActivity;
import com.example.flowmoney.ui.main.AddOperationActivity;
import com.example.flowmoney.ui.main.MainViewModel;
import com.example.flowmoney.data.database.AppDatabase;
import com.example.flowmoney.data.entity.AccountEntity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private FloatingActionButton fabAdd;
    private TextView tvBalanceAmount;
    private ImageView ivToggleBalance;
    private TextView tvMonthIncome, tvMonthExpense;
    private boolean isBalanceVisible = true;
    private double currentBalance = 0.0;

    private RecyclerView rvAccounts;
    private AccountsAdapter accountsAdapter;
    private List<AccountEntity> accountsList = new ArrayList<>();
    private AppDatabase db;
    private MainViewModel viewModel;
    private ActivityResultLauncher<Intent> createAccountLauncher;

    private boolean isAccountsExpanded = true;
    private TextView tvAccountsTitle;
    private ImageView btnAddAccount;
    private TextView tvNoAccounts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvBalanceAmount = findViewById(R.id.tvBalanceAmount);
        ivToggleBalance = findViewById(R.id.ivToggleBalance);
        fabAdd = findViewById(R.id.fabAdd);

        tvAccountsTitle = findViewById(R.id.tvAccountsTitle);
        btnAddAccount = findViewById(R.id.btnAddAccount);
        tvNoAccounts = findViewById(R.id.tvNoAccounts);
        rvAccounts = findViewById(R.id.rvAccounts);

        tvMonthIncome = findViewById(R.id.tvMonthIncome);
        tvMonthExpense = findViewById(R.id.tvMonthExpense);

        LinearLayout analyticsContainer = findViewById(R.id.analyticsContainer);
        analyticsContainer.setOnClickListener(v ->
                startActivity(new Intent(this, AnalyticsActivity.class))
        );

        tvAccountsTitle.setOnClickListener(v -> {
            isAccountsExpanded = !isAccountsExpanded;
            updateAccountsVisibility();
        });

        btnAddAccount.setOnClickListener(v -> {
            createAccountLauncher.launch(new Intent(MainActivity.this, CreateAccountActivity.class));
        });


        db = AppDatabase.getInstance(this);
        createAccountLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        // LiveData автоматически обновит список
                    }
                }
        );

        accountsAdapter = new AccountsAdapter(this, accountsList, () -> {
            createAccountLauncher.launch(new Intent(MainActivity.this, CreateAccountActivity.class));
        });
        rvAccounts.setAdapter(accountsAdapter);
        rvAccounts.setLayoutManager(new LinearLayoutManager(this));
        rvAccounts.setHasFixedSize(true);
        rvAccounts.setNestedScrollingEnabled(false);

        isAccountsExpanded = true;
        updateAccountsVisibility();

        db.accountDao().getAllLive().observe(this, accounts -> {
            accountsList.clear();
            if (accounts != null) {
                accountsList.addAll(accounts);
            }
            updateAccountsVisibility();
            accountsAdapter.notifyDataSetChanged();
        });

        viewModel = new ViewModelProvider(this).get(MainViewModel.class);
        viewModel.getMainAccount().observe(this, account -> {
            if (account == null) return;
            currentBalance = account.balance;
            tvBalanceAmount.setText(
                    isBalanceVisible ? formatBalance(currentBalance) : maskBalance(currentBalance)
            );
            tvBalanceAmount.setTextColor(getColor(R.color.black));
        });

        viewModel.getMonthIncome().observe(this, value -> {
            double month = value != null ? value : 0.0;
            tvMonthIncome.setText("+" + formatBalance(month));
        });

        viewModel.getMonthExpense().observe(this, value -> {
            double month = value != null ? value : 0.0;
            tvMonthExpense.setText("-" + formatBalance(month));
        });

        // Кнопка показать/скрыть баланс
        ivToggleBalance.setOnClickListener(v -> {
            isBalanceVisible = !isBalanceVisible;
            tvBalanceAmount.setText(
                    isBalanceVisible ? formatBalance(currentBalance) : maskBalance(currentBalance)
            );
            ivToggleBalance.setImageResource(
                    isBalanceVisible ? R.drawable.ic_eye_open : R.drawable.ic_eye_closed
            );
        });

        fabAdd.setOnClickListener(v ->
                startActivity(new Intent(this, AddOperationActivity.class))
        );
    }

    private void updateAccountsVisibility() {
        if (isAccountsExpanded) {
            btnAddAccount.setVisibility(View.VISIBLE);
            rvAccounts.setVisibility(accountsList.isEmpty() ? View.GONE : View.VISIBLE);
            tvNoAccounts.setVisibility(accountsList.isEmpty() ? View.VISIBLE : View.GONE);
        } else {
            rvAccounts.setVisibility(View.GONE);
            btnAddAccount.setVisibility(View.GONE);
            tvNoAccounts.setVisibility(View.GONE);
        }
    }

    private String formatBalance(double value) {
        return String.format(Locale.US, "%.2f", value);
    }

    private String maskBalance(double value) {
        return formatBalance(value).replaceAll(".", "•");
    }
}
