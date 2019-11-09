package com.example.klasy;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

@Entity(tableName = "Maszyny")
public class Maszyna {
    @NonNull
    @PrimaryKey(autoGenerate = true)
    public int uid;
    private int szerokosc;
    private String nazwa;

    public void setNazwaZabiegu(String nazwaZabiegu) {
        this.nazwaZabiegu = nazwaZabiegu;
    }

    private String nazwaZabiegu = new String("");
    private String typ;

    public Maszyna(String nazwa, int szerokosc, String typ) {
        this.nazwa = nazwa;
        this.szerokosc = szerokosc;
        this.typ = typ;
    }

    public String getNazwaZabiegu() {
        return nazwaZabiegu;
    }

    public int getSzerokosc() {
        return szerokosc;
    }

    public String getNazwa() {
        return nazwa;
    }
    public String getTyp() {
        return typ;
    }


}
