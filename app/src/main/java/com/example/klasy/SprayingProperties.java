package com.example.klasy;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "SprayingProperties")
public class SprayingProperties {
    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    @PrimaryKey(autoGenerate = true)
    private int uid;
    private String opis = new String("");

    public String getOpis() {
        return opis;
    }

    public void setOpis(String opis) {
        this.opis = opis;
    }

    public boolean checkNotEmpty() {
        if (opis.isEmpty()) {
            return false;
        } else {
            return true;
        }
    }
}
