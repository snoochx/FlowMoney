package com.example.flowmoney.data;

import androidx.annotation.NonNull;

import com.example.flowmoney.R;

public enum CategoryType {

    FOOD("Еда", R.drawable.ic_food, "#FF7043"),
    TRANSPORT("Транспорт", R.drawable.ic_transport, "#42A5F5"),
    SALARY("Зарплата", R.drawable.ic_salary, "#66BB6A"),
    ENTERTAINMENT("Развлечения", R.drawable.ic_entertainment, "#AB47BC"),
    SAVING("Вклад", R.drawable.ic_saving, "#FFA000"),
    OTHER("Другое", R.drawable.ic_other, "#BDBDBD");

    public final String title;
    public final int iconRes;
    public final String colorHex;

    @NonNull
    @Override
    public String toString() {
        return title;
    }

    CategoryType(String title, int iconRes, String colorHex) {
        this.title = title;
        this.iconRes = iconRes;
        this.colorHex = colorHex;
    }

    public static CategoryType fromTitle(String title) {
        for (CategoryType c : values()) {
            if (c.title.equals(title)) return c;
        }
        return OTHER;
    }
}
