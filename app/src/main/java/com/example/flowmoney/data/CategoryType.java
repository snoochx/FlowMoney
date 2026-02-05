package com.example.flowmoney.data;

import androidx.annotation.NonNull;

import com.example.flowmoney.R;

public enum CategoryType {

    FOOD("Еда", R.drawable.ic_food, "#FF7043"),
    TRANSPORT("Транспорт", R.drawable.ic_transport, "#42A5F5"),
    SALARY("Зарплата", R.drawable.ic_salary, "#66BB6A"),
    ENTERTAINMENT("Развлечения", R.drawable.ic_entertainment, "#AB47BC"),
    SAVING("Вклад", R.drawable.ic_saving, "#FFA000"),
    HEALTH("Здоровье", R.drawable.ic_health, "#E91E63"),
    EDUCATION("Образование", R.drawable.ic_education, "#3F51B5"),
    SHOPPING("Покупки", R.drawable.ic_shopping, "#FF9800"),
    BILLS("Счета", R.drawable.ic_bills, "#009688"),
    GIFT("Подарки", R.drawable.ic_gift, "#9C27B0"),
    TRAVEL("Путешествия", R.drawable.ic_travel, "#2196F3"),
    SPORTS("Спорт", R.drawable.ic_sports, "#4CAF50"),
    PETS("Домашние животные", R.drawable.ic_pets, "#FF5722"),
    BEAUTY("Красота", R.drawable.ic_beauty, "#E91E63"),
    HOBBY("Хобби", R.drawable.ic_hobby, "#795548"),
    CHARITY("Благотворительность", R.drawable.ic_charity, "#009688"),
    INVESTMENT("Инвестиции", R.drawable.ic_investment, "#3F51B5"),
    TAXES("Налоги", R.drawable.ic_taxes, "#F44336"),
    RENT("Аренда", R.drawable.ic_rent, "#FF9800"),
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
