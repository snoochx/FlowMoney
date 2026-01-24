package com.example.flowmoney.data.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "operations")
public class OperationEntity {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public double amount;
    public String type;
    public String category;
    public Long date;
    public String comment;
}
