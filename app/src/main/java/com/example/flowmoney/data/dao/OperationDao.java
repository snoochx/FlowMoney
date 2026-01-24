package com.example.flowmoney.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.flowmoney.data.entity.OperationEntity;

import java.util.List;

@Dao
public interface OperationDao {

    @Insert
    void insert(OperationEntity operation);

    @Query("SELECT * FROM operations ORDER BY date DESC")
    List<OperationEntity> getAll();

    @Query("DELETE FROM operations WHERE id = :id")
    void deleteById(int id);

    @Query("SELECT IFNULL(SUM(CASE WHEN type = 'INCOME' THEN amount ELSE 0 END), 0) - " +
            "IFNULL(SUM(CASE WHEN type = 'EXPENSE' THEN amount ELSE 0 END), 0) " +
            "FROM operations")
    LiveData<Double> getBalanceLive();

    @Query("SELECT IFNULL(SUM(amount), 0) FROM operations " +
            "WHERE type = 'INCOME' " +
            "AND strftime('%m', datetime(date / 1000, 'unixepoch')) = :month " +
            "AND strftime('%Y', datetime(date / 1000, 'unixepoch')) = :year")
    LiveData<Double> getMonthIncomeLive(String month, String year);

    @Query("SELECT IFNULL(SUM(amount), 0) FROM operations " +
            "WHERE type = 'EXPENSE' " +
            "AND strftime('%m', datetime(date / 1000, 'unixepoch')) = :month " +
            "AND strftime('%Y', datetime(date / 1000, 'unixepoch')) = :year")
    LiveData<Double> getMonthExpenseLive(String month, String year);

    @Query("SELECT * FROM operations WHERE category LIKE :account || '%' ORDER BY date DESC")
    List<OperationEntity> getOperationsByAccount(String account);

    @Query("SELECT * FROM operations WHERE strftime('%m', datetime(date / 1000, 'unixepoch')) = :month " +
            "AND strftime('%Y', datetime(date / 1000, 'unixepoch')) = :year ORDER BY date DESC")
    List<OperationEntity> getOperationsByMonth(String month, String year);

    @Query("SELECT * FROM operations ORDER BY date DESC")
    LiveData<List<OperationEntity>> getAllLive();

}
