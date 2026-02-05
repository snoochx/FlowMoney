package com.example.flowmoney.ui.main;

import android.app.Activity;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.AutoCompleteTextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.flowmoney.R;
import com.example.flowmoney.data.CategoryType;
import com.example.flowmoney.data.entity.OperationEntity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;

import java.util.List;

public class AddOperationActivity extends AppCompatActivity {
    private EditText etAmount, etComment;
    private Button btnSave;
    private MaterialButtonToggleGroup toggleGroup;
    private MaterialButton btnExpenses, btnIncome;
    private MainViewModel viewModel;
    private AutoCompleteTextView actvCategory;
    private androidx.appcompat.app.AlertDialog categoryDialog;

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

        viewModel = new ViewModelProvider(this).get(MainViewModel.class);

        actvCategory.setOnClickListener(v -> showCategoryPickerDialog());

        setToggleButtonColors(toggleGroup.getCheckedButtonId());
        toggleGroup.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (!isChecked) return;
            setToggleButtonColors(checkedId);
        });

        btnSave.setOnClickListener(v -> saveOperation());
    }

    private void showCategoryPickerDialog() {
        List<CategoryType> categories = List.of(CategoryType.values());

        View dialogView = getLayoutInflater().inflate(R.layout.dialog_select_category, null);
        androidx.recyclerview.widget.RecyclerView rvCategories = dialogView.findViewById(R.id.rvCategories);

        CategoryPickerAdapter adapter = new CategoryPickerAdapter(categories, category -> {
            actvCategory.setText(category.title);
            categoryDialog.dismiss();
        });

        rvCategories.setAdapter(adapter);
        rvCategories.setLayoutManager(new androidx.recyclerview.widget.LinearLayoutManager(this));

        rvCategories.addItemDecoration(new androidx.recyclerview.widget.DividerItemDecoration(this,
                androidx.recyclerview.widget.DividerItemDecoration.VERTICAL) {
            @Override
            public void onDraw(android.graphics.Canvas c,
                               androidx.recyclerview.widget.RecyclerView parent,
                               androidx.recyclerview.widget.RecyclerView.State state) {
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

        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setView(dialogView);

        categoryDialog = builder.create();
        if (categoryDialog.getWindow() != null) {
            categoryDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }
        categoryDialog.show();
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
