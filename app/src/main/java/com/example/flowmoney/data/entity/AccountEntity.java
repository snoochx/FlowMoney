package com.example.flowmoney.data.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "accounts")
public class AccountEntity {

    @PrimaryKey(autoGenerate = true)
    public int id;
    public String name;
    public double balance;
    public boolean isMain;
    public AccountEntity() {
    }

    public AccountEntity(String name, double balance, boolean isMain) {
        this.name = name;
        this.balance = balance;
        this.isMain = isMain;
    }
}
