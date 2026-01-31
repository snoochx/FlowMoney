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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainViewModel extends AndroidViewModel {

    private final AppDatabase db;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    private final LiveData<AccountEntity> mainAccountFromDb;
    private final LiveData<Double> operationsBalance;

    private final androidx.lifecycle.MediatorLiveData<AccountEntity> mainAccount = new androidx.lifecycle.MediatorLiveData<>();
    private final LiveData<Double> monthIncome;
    private final LiveData<Double> monthExpense;

    public MainViewModel(@NonNull Application application) {
        super(application);

        db = DatabaseProvider.getDatabase(application);

        mainAccountFromDb = db.accountDao().getMainAccount();
        Calendar cal = Calendar.getInstance();
        String month = String.format(Locale.US, "%02d", cal.get(Calendar.MONTH) + 1);
        String year = String.valueOf(cal.get(Calendar.YEAR));
        monthIncome = db.operationDao().getMonthIncomeLive(month, year);
        monthExpense = db.operationDao().getMonthExpenseLive(month, year);

        operationsBalance = db.operationDao().getBalanceLive();
        mainAccount.addSource(mainAccountFromDb, acc -> combineMainAccount(acc, operationsBalance.getValue()));
        mainAccount.addSource(operationsBalance, bal -> combineMainAccount(mainAccountFromDb.getValue(), bal));
    }

    private void combineMainAccount(AccountEntity acc, Double balance) {
        if (acc == null || balance == null) return;

        AccountEntity copy = new AccountEntity();
        copy.id = acc.id;
        copy.name = acc.name;
        copy.isMain = acc.isMain;
        copy.balance = balance;
        mainAccount.setValue(copy);

        executor.execute(() -> {
            AccountEntity mainInDb = db.accountDao().getMainAccountSync();
            if (mainInDb != null) {
                mainInDb.balance = balance;
                db.accountDao().update(mainInDb);
            }
        });
    }

    public LiveData<AccountEntity> getMainAccount() {
        return mainAccount;
    }

    public LiveData<Double> getMonthIncome() { return monthIncome; }
    public LiveData<Double> getMonthExpense() { return monthExpense; }

    public void addOperation(OperationEntity op) {
        executor.execute(() -> {
            db.accountDao().addOperationAndUpdateMain(op);
            double newBalance = db.operationDao().getTotalBalanceSync();
            AccountEntity main = db.accountDao().getMainAccountSync();
            if (main != null) {
                main.balance = newBalance;
                db.accountDao().update(main);
                mainAccount.postValue(main);
            }
        });
    }
}
