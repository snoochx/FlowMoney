package com.example.flowmoney.data.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "saving_history")
public class SavingHistoryEntity {

    @PrimaryKey(autoGenerate = true)
    public int id;

    public int savingId;
    public double amount;
    public long timestamp;

    public SavingHistoryEntity(int savingId, double amount, long timestamp) {
        this.savingId = savingId;
        this.amount = amount;
        this.timestamp = timestamp;
    }
}
