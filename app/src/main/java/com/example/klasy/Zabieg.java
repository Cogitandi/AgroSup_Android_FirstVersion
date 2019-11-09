package com.example.klasy;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

@Entity(tableName = "Zabiegi")
public class Zabieg {

    @PrimaryKey(autoGenerate = true)
    private int uid;
    private int poleNr;
    private String nazwaPola;
    private String nazwaZabiegu;
    private String operator;
    private float powierzchnia;
    private String start;
    private String koniec;
    private String uwagi = new String("");

    public String getUwagi() {
        return uwagi;
    }

    public void setUwagi(String uwagi) {
        this.uwagi = uwagi;
    }



    public void setPoleNr(int poleNr) {
        this.poleNr = poleNr;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public void setKoniec(String koniec) {
        this.koniec = koniec;
    }

    public int getUid() {
        return uid;
    }

    public int getPoleNr() {
        return poleNr;
    }

    public void setNazwaPola(String nazwaPola) {
        this.nazwaPola = nazwaPola;
    }

    public void setNazwaZabiegu(String nazwaZabiegu) {
        this.nazwaZabiegu = nazwaZabiegu;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }



    public String getNazwaPola() {
        return nazwaPola;
    }

    public String getNazwaZabiegu() {
        return nazwaZabiegu;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public void setPowierzchnia(float powierzchnia) {
        this.powierzchnia = powierzchnia;
    }

    public String getOperator() {
        return operator;
    }

    public float getPowierzchnia() {
        return powierzchnia;
    }

    public String getStart() {
        return start;
    }

    public String getKoniec() {
        return koniec;
    }

    public Zabieg(int poleNr, String nazwaPola, String nazwaZabiegu, String operator, String start) {
        this.poleNr = poleNr;
        this.nazwaPola = nazwaPola;
        this.nazwaZabiegu = nazwaZabiegu;
        this.operator = operator;
        this.powierzchnia = 0;
        this.start = start;

    }

    public void zabiegKoniec(float powierzchnia) {
        this.powierzchnia += powierzchnia;
        this.koniec = Funkcje.getData();

    }

}
