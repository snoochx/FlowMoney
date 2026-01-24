package com.example.flowmoney.ui.analytics;

import android.content.res.ColorStateList;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.example.flowmoney.R;
import com.example.flowmoney.data.database.AppDatabase;
import com.example.flowmoney.data.entity.OperationEntity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AnalyticsActivity extends AppCompatActivity {

    private RecyclerView rvAnalytics;
    private AnalyticsAdapter adapter;
    private AppDatabase db;

    private MaterialButtonToggleGroup toggleGroup;
    private MaterialButton btnExpenses, btnIncome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analytics);

        rvAnalytics = findViewById(R.id.rvAnalytics);
        toggleGroup = findViewById(R.id.toggleGroup);
        btnExpenses = findViewById(R.id.btnExpenses);
        btnIncome = findViewById(R.id.btnIncome);

        rvAnalytics.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AnalyticsAdapter();
        rvAnalytics.setAdapter(adapter);

        db = Room.databaseBuilder(getApplicationContext(),
                        AppDatabase.class, "flowmoney-db")
                .fallbackToDestructiveMigration()
                .allowMainThreadQueries()
                .build();

        loadExpenses();
        setToggleButtonColors(R.id.btnExpenses);

        toggleGroup.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (!isChecked) return;
            if (checkedId == R.id.btnExpenses) loadExpenses();
            else if (checkedId == R.id.btnIncome) loadIncome();
            setToggleButtonColors(checkedId);
        });
    }

    private void setToggleButtonColors(int checkedId) {
        btnExpenses.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.white)));
        btnExpenses.setTextColor(getColor(R.color.black));
        btnIncome.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.white)));
        btnIncome.setTextColor(getColor(R.color.black));

        if (checkedId == R.id.btnExpenses) {
            btnExpenses.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.red_700)));
            btnExpenses.setTextColor(getColor(R.color.white));
        } else if (checkedId == R.id.btnIncome) {
            btnIncome.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.green_700)));
            btnIncome.setTextColor(getColor(R.color.white));
        }
    }

    private void loadExpenses() {
        List<OperationEntity> all = db.operationDao().getAll();
        List<OperationEntity> expenses = new ArrayList<>();
        for (OperationEntity op : all) if ("EXPENSE".equals(op.type)) expenses.add(op);

        adapter.setData(prepareListWithHeaders(expenses));
    }

    private void loadIncome() {
        List<OperationEntity> all = db.operationDao().getAll();
        List<OperationEntity> income = new ArrayList<>();
        for (OperationEntity op : all) if ("INCOME".equals(op.type)) income.add(op);

        adapter.setData(prepareListWithHeaders(income));
    }

    private List<AnalyticsListItem> prepareListWithHeaders(List<OperationEntity> operations) {
        List<AnalyticsListItem> list = new ArrayList<>();
        Calendar calToday = Calendar.getInstance();
        Calendar calYesterday = Calendar.getInstance();
        calYesterday.add(Calendar.DAY_OF_YEAR, -1);

        Calendar lastCal = null;

        for (OperationEntity op : operations) {
            Calendar opCal = Calendar.getInstance();
            opCal.setTime(new java.util.Date(op.date));

            String header;
            if (isSameDay(opCal, calToday)) header = "Сегодня";
            else if (isSameDay(opCal, calYesterday)) header = "Вчера";
            else header = new java.text.SimpleDateFormat("EEEE", Locale.getDefault()).format(opCal.getTime());

            if (lastCal == null || !isSameDay(opCal, lastCal)) {
                list.add(new AnalyticsHeaderItem(header));
                lastCal = opCal;
            }

            list.add(new AnalyticsOperationItem(op));
        }
        return list;
    }

    private boolean isSameDay(Calendar cal1, Calendar cal2) {
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR)
                && cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
    }
}
