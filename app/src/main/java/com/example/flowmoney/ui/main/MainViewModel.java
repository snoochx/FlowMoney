package com.example.flowmoney.ui.main;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.flowmoney.DatabaseProvider;
import com.example.flowmoney.data.database.AppDatabase;
import com.example.flowmoney.data.entity.AccountEntity;
import com.example.flowmoney.data.entity.OperationEntity;

import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.Executors;

public class MainViewModel extends AndroidViewModel {

    private final AppDatabase db;

    private final LiveData<AccountEntity> mainAccount;
    private final LiveData<Double> monthIncome;
    private final LiveData<Double> monthExpense;

    public MainViewModel(@NonNull Application application) {
        super(application);
        db = DatabaseProvider.getDatabase(application);
        mainAccount = db.accountDao().getMainAccount();

        Calendar cal = Calendar.getInstance();
        String month = String.format(Locale.US, "%02d", cal.get(Calendar.MONTH) + 1);
        String year = String.valueOf(cal.get(Calendar.YEAR));

        monthIncome = db.operationDao().getMonthIncomeLive(month, year);
        monthExpense = db.operationDao().getMonthExpenseLive(month, year);
    }

    public LiveData<AccountEntity> getMainAccount() { return mainAccount; }
    public LiveData<Double> getMonthIncome() { return monthIncome; }
    public LiveData<Double> getMonthExpense() { return monthExpense; }

    public void addOperation(OperationEntity op) {
        Executors.newSingleThreadExecutor().execute(() -> {
            db.accountDao().addOperationAndUpdateMain(op);
        });
    }
}
