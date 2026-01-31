package com.example.flowmoney.ui.account;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.flowmoney.R;
import com.example.flowmoney.data.database.AppDatabase;
import com.example.flowmoney.data.entity.AccountEntity;

import java.util.concurrent.Executors;

public class CreateAccountActivity extends AppCompatActivity {

    private EditText etName;
    private Button btnCreate;
    private AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        db = AppDatabase.getInstance(this);

        etName = findViewById(R.id.etAccountName);
        btnCreate = findViewById(R.id.btnCreateAccount);

        btnCreate.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            if (name.isEmpty()) {
                Toast.makeText(this, "Введите название счета", Toast.LENGTH_SHORT).show();
                return;
            }

            Executors.newSingleThreadExecutor().execute(() -> {
                AccountEntity existing = db.accountDao().getByName(name);
                if (existing != null) {
                    runOnUiThread(() ->
                            Toast.makeText(this, "Счет с таким названием уже существует", Toast.LENGTH_SHORT).show()
                    );
                    return;
                }

                AccountEntity account = new AccountEntity();
                account.name = name;
                account.balance = 0.0;
                account.isMain = false;
                long newId = db.accountDao().insert(account);
                account.id = (int) newId;

                runOnUiThread(() -> {
                    Toast.makeText(this, "Счет создан", Toast.LENGTH_SHORT).show();

                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("newAccountCreated", true);
                    setResult(RESULT_OK, resultIntent);
                    finish();
                });
            });
        });
    }
}
