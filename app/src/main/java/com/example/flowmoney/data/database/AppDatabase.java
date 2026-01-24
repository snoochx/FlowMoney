package com.example.flowmoney.data.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.flowmoney.data.dao.OperationDao;
import com.example.flowmoney.data.dao.SavingDao;
import com.example.flowmoney.data.entity.OperationEntity;
import com.example.flowmoney.data.entity.SavingEntity;
import com.example.flowmoney.data.entity.SavingHistoryEntity;

@Database(entities = {OperationEntity.class, SavingEntity.class, SavingHistoryEntity.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {

    private static volatile AppDatabase INSTANCE;

    public abstract OperationDao operationDao();
    public abstract SavingDao savingDao();

    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, "flowmoney-db")
                            .fallbackToDestructiveMigration()
                            .allowMainThreadQueries()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
