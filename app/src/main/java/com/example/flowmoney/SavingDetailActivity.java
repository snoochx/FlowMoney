package com.example.flowmoney;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.example.flowmoney.data.database.AppDatabase;
import com.example.flowmoney.data.entity.SavingHistoryEntity;

import java.util.List;

public class SavingDetailActivity extends AppCompatActivity {

    private RecyclerView rvHistory;
    private SavingHistoryAdapter adapter;
    private AppDatabase db;
    private int savingId;
    private TextView tvEmpty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saving_detail);

        rvHistory = findViewById(R.id.rvHistory);
        rvHistory.setLayoutManager(new LinearLayoutManager(this));
        rvHistory.setPadding(16,16,16,16);
        rvHistory.setClipToPadding(false);

        tvEmpty = findViewById(R.id.tvEmpty);
        rvHistory.setAdapter(adapter);

        db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "flowmoney-db")
                .fallbackToDestructiveMigration()
                .allowMainThreadQueries()
                .build();

        savingId = getIntent().getIntExtra("savingId", -1);
        loadHistory();
    }

    private void loadHistory() {
        List<SavingHistoryEntity> history = db.savingDao().getHistoryForSaving(savingId);

        if (history == null || history.isEmpty()) {
            tvEmpty.setVisibility(View.VISIBLE);
            tvEmpty.setText("Пополнений нет");
            rvHistory.setVisibility(View.GONE);
        } else {
            tvEmpty.setVisibility(View.GONE);
            rvHistory.setVisibility(View.VISIBLE);
            adapter = new SavingHistoryAdapter(history);
            rvHistory.setAdapter(adapter);

        }
    }
}
