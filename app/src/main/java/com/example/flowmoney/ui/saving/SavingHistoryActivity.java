package com.example.flowmoney.ui.saving;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.example.flowmoney.R;
import com.example.flowmoney.SavingHistoryAdapter;
import com.example.flowmoney.data.database.AppDatabase;
import com.example.flowmoney.data.entity.SavingHistoryEntity;

import java.util.List;

public class SavingHistoryActivity extends AppCompatActivity {

    private RecyclerView rvHistory;
    private AppDatabase db;
    private int savingId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saving_history);

        savingId = getIntent().getIntExtra("savingId", -1);

        db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "flowmoney-db")
                .fallbackToDestructiveMigration()
                .allowMainThreadQueries()
                .build();

        rvHistory = findViewById(R.id.rvSavingHistory);
        rvHistory.setLayoutManager(new LinearLayoutManager(this));

        loadHistory();
    }

    private void loadHistory() {
        List<SavingHistoryEntity> history = db.savingDao().getHistoryForSaving(savingId);
        SavingHistoryAdapter adapter = new SavingHistoryAdapter(history);
        rvHistory.setAdapter(adapter);
    }
}
