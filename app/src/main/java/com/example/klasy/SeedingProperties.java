package com.example.klasy;

import android.util.Log;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "SeedingProperties")
public class SeedingProperties {
    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    @PrimaryKey(autoGenerate = true)
    private int uid;
    private String rodzaj = new String("");
    private String odmiana = new String("");
    private String dawka = new String("");

    public String getRodzaj() {
        return rodzaj;
    }


    public void setRodzaj(String rodzaj) {
        this.rodzaj = rodzaj;
    }

    public String getOdmiana() {
        return odmiana;
    }

    public void setOdmiana(String odmiana) {
        this.odmiana = odmiana;
    }

    public String getDawka() {
        return dawka;
    }

    public void setDawka(String dawka) {
        this.dawka = dawka;
    }

    public boolean checkNotEmpty() {
        if (rodzaj.isEmpty() || odmiana.isEmpty() || dawka.isEmpty()) {
            return false;
        } else {
            return true;
        }
    }
}
