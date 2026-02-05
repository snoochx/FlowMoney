package com.example.flowmoney.ui.account;

import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;

import com.example.flowmoney.R;
import com.example.flowmoney.data.database.AppDatabase;
import com.example.flowmoney.data.entity.AccountEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

public class TransferActivity extends AppCompatActivity {

    private TextView tvFromAccount, tvToAccount;
    private EditText etTransferAmount;
    private Button btnSendTransfer;
    private ImageView btnSwapAccounts;
    private LinearLayout fromContainer, toContainer;
    private AppDatabase db;
    private List<AccountEntity> accountsList = new ArrayList<>();
    private AccountEntity fromAccount, toAccount;
    private AlertDialog alertDialog;
    private double mainBalance = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transfer);

        mainBalance = getIntent().getDoubleExtra("mainBalance", 0.0);

        db = AppDatabase.getInstance(this);

        tvFromAccount = findViewById(R.id.tvFromAccount);
        tvToAccount = findViewById(R.id.tvToAccount);
        etTransferAmount = findViewById(R.id.etTransferAmount);
        btnSendTransfer = findViewById(R.id.btnSendTransfer);
        btnSwapAccounts = findViewById(R.id.btnSwapAccounts);

        fromContainer = findViewById(R.id.fromAccountContainer);
        toContainer = findViewById(R.id.toAccountContainer);

        db.accountDao().getAllLive().observe(this, accounts -> {
            accountsList.clear();
            if (accounts != null) accountsList.addAll(accounts);
            AccountEntity mainAccount = null;
            for (AccountEntity acc : accountsList) {
                if (acc.isMain) {
                    mainAccount = acc;
                    break;
                }
            }

            if (fromAccount == null && mainAccount != null) {
                fromAccount = mainAccount;
                tvFromAccount.setText(fromAccount.name);
            }
            if (toAccount == null) {
                for (AccountEntity acc : accountsList) {
                    if (!acc.isMain) {
                        toAccount = acc;
                        tvToAccount.setText(toAccount.name);
                        break;
                    }
                }
            }
        });

        btnSwapAccounts.setOnClickListener(v -> {
            ObjectAnimator rotate = ObjectAnimator.ofFloat(btnSwapAccounts, "rotation", 0f, 180f);
            rotate.setDuration(200);
            rotate.start();
            swapAccounts();
        });

        fromContainer.setOnClickListener(v -> showAccountPicker(true));
        toContainer.setOnClickListener(v -> showAccountPicker(false));
        btnSendTransfer.setOnClickListener(v -> sendTransfer());
    }

    private void swapAccounts() {
        AccountEntity temp = fromAccount;
        fromAccount = toAccount;
        toAccount = temp;

        tvFromAccount.setText(fromAccount.name);
        tvToAccount.setText(toAccount.name);
    }

    private void showAccountPicker(boolean isFrom) {
        if (accountsList.isEmpty()) return;

        View dialogView = getLayoutInflater().inflate(R.layout.dialog_select_account, null);
        androidx.recyclerview.widget.RecyclerView rvAccounts = dialogView.findViewById(R.id.rvAccounts);

        AccountsPickerAdapter adapter = new AccountsPickerAdapter(accountsList, account -> {
            if (isFrom) {
                fromAccount = account;
                tvFromAccount.setText(account.name);
            } else {
                toAccount = account;
                tvToAccount.setText(account.name);
            }
            alertDialog.dismiss();
        });

        rvAccounts.setAdapter(adapter);
        rvAccounts.setLayoutManager(new androidx.recyclerview.widget.LinearLayoutManager(this));

        rvAccounts.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL) {
            @Override
            public void onDraw(android.graphics.Canvas c, androidx.recyclerview.widget.RecyclerView parent, androidx.recyclerview.widget.RecyclerView.State state) {
                int childCount = parent.getChildCount();
                for (int i = 0; i < childCount - 1; i++) {
                    android.view.View child = parent.getChildAt(i);
                    int left = parent.getPaddingLeft();
                    int right = parent.getWidth() - parent.getPaddingRight();
                    android.view.ViewGroup.MarginLayoutParams params = (android.view.ViewGroup.MarginLayoutParams) child.getLayoutParams();
                    int top = child.getBottom() + params.bottomMargin;
                    int bottom = top + 1;

                    android.graphics.Paint paint = new android.graphics.Paint();
                    paint.setColor(android.graphics.Color.parseColor("#DDDDDD"));
                    c.drawRect(left, top, right, bottom, paint);
                }
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);

        alertDialog = builder.create();
        if (alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }
        alertDialog.show();
    }

    private void sendTransfer() {
        double amount = Double.parseDouble(etTransferAmount.getText().toString());

        if (fromAccount.balance < amount) {
            runOnUiThread(() ->
                    Toast.makeText(this, "Недостаточно средств", Toast.LENGTH_SHORT).show()
            );
            return;
        }

        Executors.newSingleThreadExecutor().execute(() -> {
            AccountEntity from = db.accountDao().getByIdSync(fromAccount.id);
            AccountEntity to = db.accountDao().getByIdSync(toAccount.id);

            from.balance -= amount;
            to.balance += amount;

            db.accountDao().update(from);
            db.accountDao().update(to);

            runOnUiThread(() -> {
                Toast.makeText(this, "Перевод выполнен", Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK);
                finish();
            });
        });
    }
}
