package com.example.baza;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.klasy.Operator;
import com.example.klasy.Pole;

import java.util.List;

@Dao
public interface OperatorDao {
    @Query("SELECT * FROM Operator LIMIT 1")
    Operator getOperator();

    @Query("SELECT COUNT(uid) FROM Operator")
    int getCount();

    @Update
    void updateOperator(Operator operator);

    @Insert
    void insert(Operator operator);

    @Delete
    void delete(Operator operator);
}