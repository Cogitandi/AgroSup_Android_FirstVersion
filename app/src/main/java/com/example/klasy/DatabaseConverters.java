package com.example.klasy;

import androidx.room.TypeConverter;

import com.google.gson.Gson;

public class DatabaseConverters {

    // Maszyna
    @TypeConverter
    public Maszyna StringToMaszyna(String value) {
        Maszyna maszyna = new Gson().fromJson(value, Maszyna.class);
        return maszyna;
    }

    @TypeConverter
    public String MaszynaToString(Maszyna maszyna) {
        Gson gson = new Gson();
        return gson.toJson(maszyna);
    }

    // Zabieg
    @TypeConverter
    public Zabieg StringToZabieg(String value) {
        Zabieg zabieg = new Gson().fromJson(value, Zabieg.class);
        return zabieg;
    }

    @TypeConverter
    public String ZabiegToString(Zabieg zabieg) {
        Gson gson = new Gson();
        return gson.toJson(zabieg);
    }

}
