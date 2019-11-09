package com.example.klasy;

import androidx.room.Database;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import java.util.Date;

@Entity(tableName = "Operator")
public class Operator {
    @PrimaryKey(autoGenerate = true)
    private int uid;
    private String imie;

    @TypeConverters(DatabaseConverters.class)
    private Maszyna maszyna;
    @TypeConverters(DatabaseConverters.class)
    private Zabieg obecnie;

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    private String odlegloscCzujnika;

    public void setImie(String imie) {
        this.imie = imie;
    }

    public void setMaszyna(Maszyna maszyna) {
        this.maszyna = maszyna;
    }

    public String getOdlegloscCzujnika() {
        return odlegloscCzujnika;
    }

    public void setOdlegloscCzujnika(String odlegloscCzujnika) {
        this.odlegloscCzujnika = odlegloscCzujnika;
    }

    public Operator(String imie) {
        this.imie = imie;
    }

    public String getImie() {
        return imie;
    }

    public void setObecnie(Zabieg obecnie) {
        this.obecnie = obecnie;
    }

    public Maszyna getMaszyna() {
        return maszyna;
    }

    public Zabieg getObecnie() {
        return obecnie;
    }

    public void wybierzMaszyne(Maszyna maszyna) {
        this.maszyna = maszyna;
    }

    public void zacznijZabieg(Pole pole, String dataStartu) {
        if (maszyna != null) {
            obecnie = new Zabieg(pole.getNumerPola(), pole.getNazwa(), maszyna.getNazwaZabiegu(), imie, dataStartu);
        }
    }

    public void skonczZabieg(float powierzchnia) {

        obecnie.zabiegKoniec(powierzchnia);
    }

}
