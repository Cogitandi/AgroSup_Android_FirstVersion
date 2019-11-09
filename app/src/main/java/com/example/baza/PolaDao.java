package com.example.baza;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.klasy.Pole;

import java.util.List;

@Dao
public interface PolaDao {
    @Query("SELECT * FROM pola")
    List<Pole> getAll();

    @Query("SELECT numerPola FROM pola")
    List<Integer> getNumeryPol();

    @Query("SELECT * FROM pola WHERE numerPola IN (:numer) LIMIT 1")
    Pole findByNumer(int numer);

    @Update
    void updatePowierzchnia(Pole pole);

    @Insert
    void insertAll(Pole... pola);

    @Delete
    void delete(Pole pole);
}