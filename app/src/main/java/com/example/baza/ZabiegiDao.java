package com.example.baza;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.klasy.Zabieg;

import java.util.List;

@Dao
public interface ZabiegiDao {
    @Query("SELECT * FROM zabiegi")
    List<Zabieg> getAll();

    @Query("SELECT uid FROM zabiegi WHERE start IN (:czas) LIMIT 1")
    int getUid(String czas);

    @Update
    void updateZabieg(Zabieg zabieg);

    @Insert
    void insertAll(Zabieg... zabiegi);

    @Delete
    void delete(Zabieg zabieg);
}