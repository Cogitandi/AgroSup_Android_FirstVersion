package com.example.baza;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.klasy.SeedingProperties;

import java.util.List;

@Dao
public interface SeedingPropertiesDao {
    @Query("SELECT * FROM SeedingProperties  ORDER BY UID DESC LIMIT 4")
    List<SeedingProperties> getLast();

    @Update
    void update(SeedingProperties seedingProperties);

    @Insert
    void insertAll(SeedingProperties... seedingProperties);

    @Delete
    void delete(SeedingProperties seedingProperties);
}