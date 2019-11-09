package com.example.baza;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.example.klasy.Dzialka;
import com.example.klasy.Maszyna;
import com.example.klasy.Operator;
import com.example.klasy.Pole;
import com.example.klasy.SeedingProperties;
import com.example.klasy.SprayingProperties;
import com.example.klasy.Zabieg;

@Database(entities = {Dzialka.class, Pole.class, Maszyna.class, Zabieg.class, Operator.class, SeedingProperties.class, SprayingProperties.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    public abstract DzialkiDao dzialkiDao();
    public abstract PolaDao polaDao();
    public abstract MaszynyDao maszynyDao();
    public abstract ZabiegiDao zabiegiDao();
    public abstract OperatorDao operatorDao();
    public abstract SeedingPropertiesDao seedingPropertiesDao();
    public abstract SprayingPropertiesDao sprayingPropertiesDao();
}