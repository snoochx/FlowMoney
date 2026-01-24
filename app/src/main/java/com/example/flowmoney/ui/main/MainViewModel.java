package com.example.flowmoney.ui.main;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.flowmoney.DatabaseProvider;
import com.example.flowmoney.data.database.AppDatabase;
import com.example.flowmoney.data.entity.OperationEntity;
import com.example.flowmoney.data.entity.SavingEntity;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainViewModel extends AndroidViewModel {

    private final AppDatabase db;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final LiveData<Double> balance;
    private final LiveData<Double> monthIncome;
    private final LiveData<Double> monthExpense;
    private final LiveData<SavingEntity> savingLive;

    private LiveData<List<OperationEntity>> operationsLive = new MutableLiveData<>();

    public MainViewModel(@NonNull Application application) {
        super(application);
        db = DatabaseProvider.getDatabase(application);

        savingLive = db.savingDao().getSavingLive();
        operationsLive = db.operationDao().getAllLive();

        Calendar cal = Calendar.getInstance();
        String month = String.format(Locale.US, "%02d", cal.get(Calendar.MONTH) + 1);
        String year = String.valueOf(cal.get(Calendar.YEAR));

        balance = db.operationDao().getBalanceLive();
        monthIncome = db.operationDao().getMonthIncomeLive(month, year);
        monthExpense = db.operationDao().getMonthExpenseLive(month, year);
    }

    public LiveData<Double> getBalance() { return balance; }
    public LiveData<Double> getMonthIncome() { return monthIncome; }
    public LiveData<Double> getMonthExpense() { return monthExpense; }
    public LiveData<SavingEntity> getSavingLive() { return savingLive; }

    public void addOperation(OperationEntity op) {
        executor.execute(() -> db.operationDao().insert(op));
    }

    public void addInterest(SavingEntity saving) {
        executor.execute(() -> {
            long now = System.currentTimeMillis();
            double increment = saving.amount * saving.percent / 100.0;
            saving.amount += increment;
            saving.lastUpdate = now;
            db.savingDao().update(saving);

            OperationEntity incrementOp = new OperationEntity();
            incrementOp.amount = increment;
            incrementOp.category = "Вклад";
            incrementOp.comment = "Начисление процентов";
            incrementOp.date = now;
            incrementOp.type = "INCOME";
            db.operationDao().insert(incrementOp);

            db.savingDao().insertHistory(new com.example.flowmoney.data.entity.SavingHistoryEntity(
                    saving.id, increment, now
            ));
        });
    }

    public void closeSaving() {
        executor.execute(() -> db.savingDao().deleteAll());
    }
}
