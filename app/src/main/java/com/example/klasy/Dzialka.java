package com.example.klasy;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "Dzialki")
public class Dzialka {
    public int getUid() {
        return uid;
    }

    public int getNumerPola() {
        return numerPola;
    }

    public String getNumerDzialki() {
        return numerDzialki;
    }

    public float getPowierzchnia() {
        return powierzchnia;
    }

    public boolean isDoplaty() {
        return doplaty;
    }

    public boolean isPaliwo() {
        return paliwo;
    }

    @PrimaryKey(autoGenerate = true)
    private int uid;
    private int numerPola;
    private String numerDzialki;
    private float powierzchnia;
    private boolean doplaty;
    private boolean paliwo;

    public Dzialka(int numerPola, String numerDzialki, float powierzchnia, boolean doplaty, boolean paliwo) {
        this.numerPola = numerPola;
        this.numerDzialki = numerDzialki;
        this.powierzchnia = powierzchnia;
        this.doplaty = doplaty;
        this.paliwo = paliwo;
    }


    public void setUid(int uid) {
        this.uid = uid;
    }
}
