package com.example.flowmoney;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.flowmoney.data.entity.OperationEntity;
import com.example.flowmoney.data.entity.SavingEntity;
import com.example.flowmoney.ui.analytics.AnalyticsActivity;
import com.example.flowmoney.ui.main.AddOperationActivity;
import com.example.flowmoney.ui.main.MainViewModel;
import com.example.flowmoney.ui.saving.CreateSavingActivity;
import com.example.flowmoney.ui.saving.SavingInfoActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private FloatingActionButton fabAdd;
    private TextView tvBalanceAmount;
    private ImageView ivToggleBalance;
    private TextView tvMonthIncome, tvMonthExpense;
    private LinearLayout savingsContainer;
    private TextView tvSavingName, tvSavingAmount, tvSavingPercent;

    private boolean isBalanceVisible = true;
    private double currentBalance = 0.0;

    private Handler handler = new Handler();

    private MainViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvBalanceAmount = findViewById(R.id.tvBalanceAmount);
        ivToggleBalance = findViewById(R.id.ivToggleBalance);
        fabAdd = findViewById(R.id.fabAdd);

        tvMonthIncome = findViewById(R.id.tvMonthIncome);
        tvMonthExpense = findViewById(R.id.tvMonthExpense);

        savingsContainer = findViewById(R.id.savingsContainer);
        tvSavingName = findViewById(R.id.tvSavingName);
        tvSavingAmount = findViewById(R.id.tvSavingAmount);
        tvSavingPercent = findViewById(R.id.tvSavingPercent);

        LinearLayout analyticsContainer = findViewById(R.id.analyticsContainer);
        analyticsContainer.setOnClickListener(v -> startActivity(new Intent(this, AnalyticsActivity.class)));

        viewModel = new ViewModelProvider(this).get(MainViewModel.class);

        // LiveData на вклад
        viewModel.getSavingLive().observe(this, saving -> updateSavingUI(saving));

        viewModel.getBalance().observe(this, value -> {
            currentBalance = value != null ? value : 0;
            tvBalanceAmount.setText(isBalanceVisible ? formatBalance(currentBalance) : maskBalance(currentBalance));
            tvBalanceAmount.setTextColor(getColor(R.color.black));
        });

        viewModel.getMonthIncome().observe(this, value -> {
            double month = value != null ? value : 0;
            tvMonthIncome.setText("+" + formatBalance(month));
        });

        viewModel.getMonthExpense().observe(this, value -> {
            double month = value != null ? value : 0;
            tvMonthExpense.setText("-" + formatBalance(month));
        });


        ivToggleBalance.setOnClickListener(v -> {
            isBalanceVisible = !isBalanceVisible;
            tvBalanceAmount.setText(isBalanceVisible ? formatBalance(currentBalance) : maskBalance(currentBalance));
            if (viewModel.getSavingLive().getValue() != null) {
                SavingEntity s = viewModel.getSavingLive().getValue();
                tvSavingAmount.setText(isBalanceVisible ? String.format(Locale.US, "%.2f", s.amount) : "•••••");
            }
            ivToggleBalance.setImageResource(isBalanceVisible ? R.drawable.ic_eye_open : R.drawable.ic_eye_closed);
        });


        fabAdd.setOnClickListener(v -> {
            startActivity(new Intent(this, AddOperationActivity.class));
        });

        savingsContainer.setOnClickListener(v -> {
            SavingEntity saving = viewModel.getSavingLive().getValue();
            if (saving != null) {
                startActivity(new Intent(this, SavingInfoActivity.class)
                        .putExtra("savingId", saving.id));
            } else {
                startActivity(new Intent(this, CreateSavingActivity.class));
            }
        });

        startAutoAdd();
    }

    private void updateBalance(List<OperationEntity> operations) {
        double incomeSum = 0, expenseSum = 0, monthIncome = 0, monthExpense = 0;
        Calendar cal = Calendar.getInstance();
        int currentMonth = cal.get(Calendar.MONTH);
        int currentYear = cal.get(Calendar.YEAR);

        for (OperationEntity op : operations) {
            Calendar opDate = Calendar.getInstance();
            opDate.setTime(new java.util.Date(op.date));
            if ("INCOME".equals(op.type)) {
                incomeSum += op.amount;
                if (opDate.get(Calendar.MONTH) == currentMonth && opDate.get(Calendar.YEAR) == currentYear)
                    monthIncome += op.amount;
            } else if ("EXPENSE".equals(op.type)) {
                expenseSum += op.amount;
                if (opDate.get(Calendar.MONTH) == currentMonth && opDate.get(Calendar.YEAR) == currentYear)
                    monthExpense += op.amount;
            }
        }

        currentBalance = incomeSum - expenseSum;

        tvBalanceAmount.setText(isBalanceVisible ? formatBalance(currentBalance) : maskBalance(currentBalance));
        tvBalanceAmount.setTextColor(getColor(R.color.black));
        tvMonthIncome.setText("+" + formatBalance(monthIncome));
        tvMonthExpense.setText("-" + formatBalance(monthExpense));
    }

    private void updateSavingUI(SavingEntity saving) {
        if (saving != null) {
            tvSavingName.setTextColor(getColor(R.color.black));
            tvSavingAmount.setTextColor(getColor(R.color.black));
            tvSavingPercent.setTextColor(getColor(R.color.black));

            tvSavingName.setText(saving.name);
            tvSavingAmount.setText(isBalanceVisible ? String.format(Locale.US, "%.2f", saving.amount) : "•••••");
            tvSavingPercent.setText(String.format(Locale.US, "+%.2f%%", saving.percent));
        } else {
            tvSavingName.setTextColor(getColor(R.color.gray));
            tvSavingAmount.setTextColor(getColor(R.color.gray));
            tvSavingPercent.setTextColor(getColor(R.color.gray));

            tvSavingName.setText("У вас нет вклада — нажмите, чтобы открыть");
            tvSavingAmount.setText("");
            tvSavingPercent.setText("");
        }
    }

    private void startAutoAdd() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                SavingEntity saving = viewModel.getSavingLive().getValue();
                if (saving != null) {
                    long now = System.currentTimeMillis();
                    if (now - saving.lastUpdate >= 10 * 60 * 1000) {
                        viewModel.addInterest(saving);
                    }
                }
                handler.postDelayed(this, 60 * 1000);
            }
        }, 1000);
    }

    private String formatBalance(double value) {
        return String.format(Locale.US, "%.2f", value);
    }

    private String maskBalance(double value) {
        return formatBalance(value).replaceAll(".", "•");
    }
}
