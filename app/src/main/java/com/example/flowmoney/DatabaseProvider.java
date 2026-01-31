package com.example.flowmoney;

import android.content.Context;

import com.example.flowmoney.data.database.AppDatabase;

public class DatabaseProvider {
    private static AppDatabase db;

    public static AppDatabase getDatabase(Context context) {
        if (db == null) {
            synchronized (DatabaseProvider.class) {
                if (db == null) {
                    db = AppDatabase.getInstance(context);
                }
            }
        }
        return db;
    }
}
