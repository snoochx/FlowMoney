package com.example.flowmoney;

import android.content.Context;

import androidx.room.Room;

import com.example.flowmoney.data.database.AppDatabase;

public class DatabaseProvider {
    private static AppDatabase db;

    public static AppDatabase getDatabase(Context context) {
        if (db == null) {
            db = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "flowmoney-db")
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return db;
    }
}
