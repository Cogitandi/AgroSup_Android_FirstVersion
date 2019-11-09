package com.example.klasy;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "Pola")
public class Pole {
    @PrimaryKey(autoGenerate = true)
    private int uid;
    private int numerPola;
    private String nazwa;
    private float powierzchnia;

    public int getUid() {
        return uid;
    }

    public int getNumerPola() {
        return numerPola;
    }

    public String getNazwa() {
        return nazwa;
    }

    public float getPowierzchnia() {
        return powierzchnia;
    }


    public Pole(int numerPola, String nazwa) {
        this.numerPola = numerPola;
        this.nazwa = nazwa;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public void setNumerPola(int numerPola) {
        this.numerPola = numerPola;
    }

    public void setPowierzchnia(float powierzchnia) {
        this.powierzchnia = powierzchnia;
    }
}
