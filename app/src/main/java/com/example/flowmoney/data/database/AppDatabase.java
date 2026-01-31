package com.example.flowmoney.data.database;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.flowmoney.data.dao.AccountDao;
import com.example.flowmoney.data.dao.OperationDao;
import com.example.flowmoney.data.entity.AccountEntity;
import com.example.flowmoney.data.entity.OperationEntity;

import java.util.concurrent.Executors;

@Database(
        entities = {
                OperationEntity.class,
                AccountEntity.class
        },
        version = 4,
        exportSchema = false
)
public abstract class AppDatabase extends RoomDatabase {

    private static volatile AppDatabase INSTANCE;

    public abstract OperationDao operationDao();
    public abstract AccountDao accountDao();

    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                                    context.getApplicationContext(),
                                    AppDatabase.class,
                                    "flowmoney-db"
                            )
                            .fallbackToDestructiveMigration()
                            .addCallback(new RoomDatabase.Callback() {
                                @Override
                                public void onCreate(@NonNull SupportSQLiteDatabase db) {
                                    super.onCreate(db);

                                    // Создаём основной счёт при первом запуске
                                    Executors.newSingleThreadExecutor().execute(() -> {
                                        AppDatabase database = INSTANCE;
                                        if (database == null) return;

                                        AccountEntity main = new AccountEntity(
                                                "Основной счёт",
                                                0.0,
                                                true
                                        );

                                        database.accountDao().insert(main);
                                    });
                                }
                            })
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
