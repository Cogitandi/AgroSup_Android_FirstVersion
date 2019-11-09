package com.example.baza;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.klasy.SprayingProperties;

import java.util.List;

@Dao
public interface SprayingPropertiesDao {
    @Query("SELECT * FROM SprayingProperties  ORDER BY UID DESC LIMIT 4")
    List<SprayingProperties> getLast();

    @Update
    void update(SprayingProperties sprayingProperties);

    @Insert
    void insertAll(SprayingProperties... sprayingProperties);

    @Delete
    void delete(SprayingProperties sprayingProperties);
}