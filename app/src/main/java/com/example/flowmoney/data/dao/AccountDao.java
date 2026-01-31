package com.example.flowmoney.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.example.flowmoney.data.entity.AccountEntity;
import com.example.flowmoney.data.entity.OperationEntity;

import java.util.List;

@Dao
public interface AccountDao {

    @Insert
    long insert(AccountEntity account);

    @Update
    void update(AccountEntity account);

    @Delete
    void delete(AccountEntity account);

    @Insert
    void insertOperation(OperationEntity op);

    @Query("SELECT * FROM accounts ORDER BY id ASC")
    LiveData<List<AccountEntity>> getAllLive();

    @Query("SELECT * FROM accounts WHERE id = :id LIMIT 1")
    LiveData<AccountEntity> getByIdLive(int id);

    @Query("SELECT * FROM accounts WHERE name = :name LIMIT 1")
    AccountEntity getByName(String name);

    @Query("SELECT * FROM accounts WHERE isMain = 1 LIMIT 1")
    LiveData<AccountEntity> getMainAccount();

    @Query("SELECT * FROM accounts WHERE isMain = 1 LIMIT 1")
    AccountEntity getMainAccountSync();

    @Transaction
    default void addOperationAndUpdateMain(OperationEntity op) {
        insertOperation(op);

        AccountEntity main = getMainAccountSync();
        if (main == null) return;

        if ("EXPENSE".equals(op.type)) main.balance -= op.amount;
        else main.balance += op.amount;

        update(main);
    }
}
