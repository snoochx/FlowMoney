package com.example.flowmoney.ui.saving;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import com.example.flowmoney.DatabaseProvider;
import com.example.flowmoney.R;
import com.example.flowmoney.data.database.AppDatabase;
import com.example.flowmoney.data.entity.SavingEntity;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CreateSavingActivity extends AppCompatActivity {

    private static final double FIXED_SAVING_PERCENT = 5.0;
    private static final double MIN_SAVING_AMOUNT = 1000.0;

    private AppDatabase db;

    ExecutorService executor = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_saving);

        db = DatabaseProvider.getDatabase(this);

        EditText etName = findViewById(R.id.etSavingName);
        EditText etAmount = findViewById(R.id.etSavingAmount);
        Button btnCreate = findViewById(R.id.btnCreateSaving);

        btnCreate.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String amountStr = etAmount.getText().toString().trim();

            if (TextUtils.isEmpty(name) || TextUtils.isEmpty(amountStr)) {
                Toast.makeText(this, "Введите имя и сумму вклада", Toast.LENGTH_SHORT).show();
                return;
            }

            double amount;
            try {
                amount = Double.parseDouble(amountStr);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Некорректная сумма", Toast.LENGTH_SHORT).show();
                return;
            }

            if (amount < MIN_SAVING_AMOUNT) {
                Toast.makeText(this, "Минимальная сумма вклада — 1000", Toast.LENGTH_SHORT).show();
                return;
            }

            SavingEntity saving = new SavingEntity(
                    name,
                    amount,
                    FIXED_SAVING_PERCENT,
                    System.currentTimeMillis(),
                    System.currentTimeMillis()
            );

            executor.execute(() -> db.savingDao().insert(saving));
            Toast.makeText(this, "Вклад создан", Toast.LENGTH_SHORT).show();
            finish();
        });
    }
}
