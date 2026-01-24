package com.example.flowmoney.data.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "savings")
public class SavingEntity {

    @PrimaryKey(autoGenerate = true)
    public int id;

    public String name;
    public double amount;
    public double percent;
    public long createdAt;
    public long lastUpdate;

    public SavingEntity(String name, double amount, double percent, long createdAt, long lastUpdate) {
        this.name = name;
        this.amount = amount;
        this.percent = percent;
        this.createdAt = createdAt;
        this.lastUpdate = lastUpdate;
    }
}
