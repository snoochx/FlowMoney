package com.example.flowmoney.ui.main;

import android.app.Activity;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.AutoCompleteTextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.flowmoney.R;
import com.example.flowmoney.data.entity.OperationEntity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;

public class AddOperationActivity extends AppCompatActivity {
    private EditText etAmount, etComment;
    private Button btnSave;
    private MaterialButtonToggleGroup toggleGroup;
    private MaterialButton btnExpenses, btnIncome;
    private MainViewModel viewModel;
    private AutoCompleteTextView actvCategory;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_operation);

        etAmount = findViewById(R.id.etAmount);
        etComment = findViewById(R.id.etComment);
        actvCategory = findViewById(R.id.actvCategory);
        btnSave = findViewById(R.id.btnSave);

        toggleGroup = findViewById(R.id.toggleGroup);
        toggleGroup.setSelectionRequired(true);

        btnExpenses = findViewById(R.id.btnExpenses);
        btnIncome = findViewById(R.id.btnIncome);

        CategoryAdapter adapter = new CategoryAdapter(this);
        actvCategory.setAdapter(adapter);

        actvCategory.setOnClickListener(v -> {
            if (actvCategory.isPopupShowing()) {
                actvCategory.dismissDropDown();
            } else {
                actvCategory.showDropDown();
            }
        });

        actvCategory.post(() -> {
            actvCategory.setDropDownWidth(actvCategory.getWidth());
        });

        viewModel = new ViewModelProvider(this).get(MainViewModel.class);

        setToggleButtonColors(toggleGroup.getCheckedButtonId());
        toggleGroup.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (!isChecked) return;
            setToggleButtonColors(checkedId);
        });

        btnSave.setOnClickListener(v -> saveOperation());
    }


    private void saveOperation() {
        String amountStr = etAmount.getText().toString().trim();
        String category = actvCategory.getText().toString().trim();
        String comment = etComment.getText().toString().trim();

        if (TextUtils.isEmpty(amountStr) || TextUtils.isEmpty(category)) {
            Toast.makeText(this, "Введите сумму и категорию", Toast.LENGTH_SHORT).show();
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(amountStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Некорректная сумма", Toast.LENGTH_SHORT).show();
            return;
        }

        OperationEntity op = new OperationEntity();
        op.amount = amount;
        op.category = category;
        op.comment = comment;
        op.date = System.currentTimeMillis();
        op.type = (toggleGroup.getCheckedButtonId() == btnExpenses.getId()) ? "EXPENSE" : "INCOME";

        viewModel.addOperation(op);

        setResult(Activity.RESULT_OK);
        finish();
    }

    private void setToggleButtonColors(int checkedId) {
        btnExpenses.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.white)));
        btnExpenses.setTextColor(getColor(R.color.black));
        btnIncome.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.white)));
        btnIncome.setTextColor(getColor(R.color.black));

        if (checkedId == btnExpenses.getId()) {
            btnExpenses.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.red_700)));
            btnExpenses.setTextColor(getColor(R.color.white));
        } else if (checkedId == btnIncome.getId()) {
            btnIncome.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.green_700)));
            btnIncome.setTextColor(getColor(R.color.white));
        }
    }
}
