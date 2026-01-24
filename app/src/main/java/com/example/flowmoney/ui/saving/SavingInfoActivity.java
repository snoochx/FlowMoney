package com.example.flowmoney.ui.saving;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import com.example.flowmoney.DatabaseProvider;
import com.example.flowmoney.R;
import com.example.flowmoney.data.database.AppDatabase;
import com.example.flowmoney.data.entity.SavingEntity;

public class SavingInfoActivity extends AppCompatActivity {

    private AppDatabase db;
    private SavingEntity saving;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saving_info);

        db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "flowmoney-db")
                .fallbackToDestructiveMigration()
                .allowMainThreadQueries()
                .build();

        int savingId = getIntent().getIntExtra("savingId", -1);
        if (savingId == -1) finish();

        saving = db.savingDao().getSaving();

        TextView tvName = findViewById(R.id.tvSavingName);
        TextView tvAmount = findViewById(R.id.tvBalanceAmount);
        TextView tvPercent = findViewById(R.id.tvSavingPercent);
        LinearLayout transferCard = findViewById(R.id.transferCard);
        LinearLayout closeCard = findViewById(R.id.closeCard);
        LinearLayout historyCard = findViewById(R.id.historyCard);

        if (saving != null) {
            tvName.setText(saving.name);
            tvAmount.setText("Баланс: " + String.format("%.2f", saving.amount));
            tvPercent.setText(String.format("+%.2f%%", saving.percent));
        }

        transferCard.setOnClickListener(v -> {
            // заглушка
        });

        db = DatabaseProvider.getDatabase(this);
        closeCard.setOnClickListener(v -> {
            new Thread(() -> db.savingDao().deleteAll()).start();
            finish();
        });


        historyCard.setOnClickListener(v -> {
            Intent intent = new Intent(this, SavingHistoryActivity.class);
            intent.putExtra("savingId", saving.id);
            startActivity(intent);
        });
    }
}
