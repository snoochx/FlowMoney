package com.example.flowmoney.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.flowmoney.data.entity.SavingEntity;
import com.example.flowmoney.data.entity.SavingHistoryEntity;

import java.util.List;

@Dao
public interface SavingDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(SavingEntity saving);

    @Update
    void update(SavingEntity saving);

    @Query("DELETE FROM savings")
    void deleteAll();

    @Query("SELECT * FROM savings LIMIT 1")
    SavingEntity getSaving();

    @Insert
    void insertHistory(SavingHistoryEntity history);

    @Query("SELECT * FROM savings LIMIT 1")
    LiveData<SavingEntity> getSavingLive();

    @Query("SELECT * FROM saving_history WHERE savingId = :savingId ORDER BY timestamp DESC")
    List<SavingHistoryEntity> getHistoryForSaving(int savingId);
}
