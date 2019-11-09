package com.example.baza;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.klasy.Dzialka;

import java.util.List;

@Dao
public interface DzialkiDao {
    @Query("SELECT * FROM dzialki")
    List<Dzialka> getAll();

    @Query("SELECT * FROM dzialki WHERE numerPola IN (:numer) ")
    List<Dzialka> findByNumer(int numer);

    @Query("SELECT numerPola FROM dzialki WHERE numerDzialki IN (:numer) ")
    int findByNumerDzialki(String numer);

    @Query("SELECT numerPola FROM dzialki WHERE numerDzialki IN (:numerDzialki) LIMIT 1")
    int numerPola(int numerDzialki);

    @Insert
    void insertAll(Dzialka... dzialki);

    @Delete
    void delete(Dzialka dzialka);
}