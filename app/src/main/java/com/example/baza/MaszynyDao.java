package com.example.baza;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.klasy.Maszyna;

import java.util.List;

@Dao
public interface MaszynyDao {
    @Query("SELECT * FROM maszyny")
    List<Maszyna> getAll();

    @Insert
    void insertAll(Maszyna... maszyna);

    @Delete
    void delete(Maszyna maszyna);
}