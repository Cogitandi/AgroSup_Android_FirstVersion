package com.example.agrosup;

import androidx.room.Room;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.gridlayout.widget.GridLayout;
import androidx.gridlayout.widget.GridLayout.LayoutParams;

import android.text.Layout;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.baza.AppDatabase;
import com.example.klasy.Dzialka;
import com.example.klasy.Pole;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class Pola extends AppCompatActivity {


    /// Fullscreen mode
    private void hideSystemUI() {
        // Enables regular immersive mode.
        // For "lean back" mode, remove SYSTEM_UI_FLAG_IMMERSIVE.
        // Or for "sticky immersive," replace it with SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE
                        // Set the content to appear under the system bars so that the
                        // content doesn't resize when the system bars hide and show.
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        // Hide the nav bar and status bar
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            hideSystemUI();
        }
    }

    // Deklaracje
    LinearLayout layout;
    GridLayout layoutWypisz;
    GridLayout layoutNowePole;
    GridLayout layoutNowaDzialka;
    AppDatabase appDatabase;
    ArrayList<Pole> pola = new ArrayList<>();
    int licznik = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pola);

        // Inicjalizacja
        layout = findViewById(R.id.Pola_ll_layout);
        layoutWypisz = findViewById(R.id.Pola_gl_wypisz);
        layoutNowePole = findViewById(R.id.Pola_gl_nowePole);
        layoutNowaDzialka = findViewById(R.id.Pola_gl_nowaDzialka);


        // Baza
        appDatabase = Room.databaseBuilder(this, AppDatabase.class, "AgroSup").allowMainThreadQueries().build();


        wypisz();
        nowePole();
        nowaDzialka();
    }

    public void reset() {
        layoutWypisz.removeAllViews();
        //layoutNowePole.removeAllViews();
        //layoutNowaDzialka.removeAllViews();
        wypisz();
        //nowePole();
        //nowaDzialka();
    }

    public LayoutParams ustawienia(int c, int r) {
        LayoutParams param = new LayoutParams();
        param.height = LayoutParams.WRAP_CONTENT;
        param.width = LayoutParams.WRAP_CONTENT;
        //param.leftMargin = 50;
        param.topMargin = 25;
        param.setGravity(Gravity.CENTER);
        param.columnSpec = GridLayout.spec(c);
        param.rowSpec = GridLayout.spec(r);
        return param;
    }

    public void wypisz() {

        boolean even = false;
        pola.clear();
        pola.addAll(appDatabase.polaDao().getAll()); // pobranie z bazy pol
        // Wypisanie naglowkow
        if (pola.size() > 0) {
            TextView tvNumerPola = new TextView(this);
            TextView tvNazwa = new TextView(this);
            TextView tvPowierzchnia = new TextView(this);
            TextView tvNumerDzialki = new TextView(this);
            TextView tvDoplaty = new TextView(this);
            TextView tvPaliwo = new TextView(this);
            TextView emptyUsun = new TextView(this);

            tvNumerPola.setText("Numer pola");
            tvNazwa.setText("Nazwa");
            tvPowierzchnia.setText("Powierzchnia");
            tvNumerDzialki.setText("Numer działki");
            tvDoplaty.setText("Dopłaty");
            tvPaliwo.setText("Paliwo");
            emptyUsun.setText(" ");
            tvNumerPola.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
            tvNazwa.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
            tvPowierzchnia.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
            tvNumerDzialki.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
            tvDoplaty.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
            tvPaliwo.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));

            layoutWypisz.addView(tvNumerPola, ustawienia(0, 0));
            layoutWypisz.addView(tvNazwa, ustawienia(1, 0));
            layoutWypisz.addView(tvPowierzchnia, ustawienia(2, 0));
            layoutWypisz.addView(tvNumerDzialki, ustawienia(3, 0));
            layoutWypisz.addView(tvDoplaty, ustawienia(4, 0));
            layoutWypisz.addView(tvPaliwo, ustawienia(5, 0));
            layoutWypisz.addView(emptyUsun, ustawienia(6, 0));
        } else {
            TextView tvBrak = new TextView(this);
            tvBrak.setText("Nie dodałeś żadnych pól");
            layoutWypisz.addView(tvBrak);
        }
        for (Pole item : pola) {

            TextView tvNumerPola = new TextView(this);
            TextView tvNazwa = new TextView(this);
            TextView tvPowierzchnia = new TextView(this);
            TextView empty = new TextView(this);
            TextView empty2 = new TextView(this);
            TextView empty3 = new TextView(this);
            TextView empty4 = new TextView(this);

            tvNumerPola.setText(item.getNumerPola() + "");
            tvNazwa.setText(item.getNazwa());
            tvPowierzchnia.setText(item.getPowierzchnia() + "");
            empty.setText(" - ");
            empty2.setText(" - ");
            empty3.setText(" - ");
            empty4.setText(" ");

            if( even == false ) {
                tvNumerPola.setBackgroundColor(Color.rgb(232, 234, 246));
                tvNazwa.setBackgroundColor(Color.rgb(232, 234, 246));
                tvPowierzchnia.setBackgroundColor(Color.rgb(232, 234, 246));
                even = true;
            } else {
                tvNumerPola.setBackgroundColor(Color.rgb(197, 202, 233));
                tvNazwa.setBackgroundColor(Color.rgb(197, 202, 233));
                tvPowierzchnia.setBackgroundColor(Color.rgb(197, 202, 233));
                even = false;
            }



            layoutWypisz.addView(tvNumerPola);
            layoutWypisz.addView(tvNazwa);
            layoutWypisz.addView(tvPowierzchnia);
            layoutWypisz.addView(empty);
            layoutWypisz.addView(empty2);
            layoutWypisz.addView(empty3);
            layoutWypisz.addView(empty4);

            List<Dzialka> dzialki = appDatabase.dzialkiDao().findByNumer(item.getNumerPola());
            for (Dzialka dzialka : dzialki) {
                TextView tvNumerPola2 = new TextView(this);
                TextView tvNazwa2 = new TextView(this);
                TextView tvPowierzchnia2 = new TextView(this);
                TextView tvNumerDzialki = new TextView(this);
                TextView tvDoplaty = new TextView(this);
                TextView tvPaliwo = new TextView(this);
                Button btnUsun = new Button(this);

                tvNumerPola2.setText("");
                tvNazwa2.setText("");
                tvPowierzchnia2.setText(dzialka.getPowierzchnia() + "");
                tvNumerDzialki.setText(dzialka.getNumerDzialki());
                if (dzialka.isDoplaty()) {
                    tvDoplaty.setText(" TAK ");
                } else {
                    tvDoplaty.setText(" NIE ");
                }
                if (dzialka.isPaliwo()) {
                    tvPaliwo.setText(" TAK ");
                } else {
                    tvPaliwo.setText(" NIE ");
                }

                empty3.setText(" - ");
                btnUsun.setText("Usuń");
                btnUsun.setOnClickListener((al) -> {
                    appDatabase.dzialkiDao().delete(dzialka);
                    updatePowierzchniaPola(dzialka.getNumerPola());
                    reset();
                });
                layoutWypisz.addView(tvNumerPola2);
                layoutWypisz.addView(tvNazwa2);
                layoutWypisz.addView(tvPowierzchnia2);
                layoutWypisz.addView(tvNumerDzialki);
                layoutWypisz.addView(tvDoplaty);
                layoutWypisz.addView(tvPaliwo);
                layoutWypisz.addView(btnUsun);
            }

            //layoutWypisz.addView(gridLayout);
        }

    }

    public void nowePole() {
        Button btnDodaj = new Button(this);
        btnDodaj.setText("Stwórz nowe pole");

        btnDodaj.setOnClickListener((l) -> {
            if (licznik == 1) {
                Toast.makeText(Pola.this, "Dodawaj pojedyczno", Toast.LENGTH_LONG).show();
            } else {
                List<Integer> numeryPol = appDatabase.polaDao().getNumeryPol();
                Collections.sort(numeryPol);
                int nowyNrPola;
                if (numeryPol.size() == 0) {
                    nowyNrPola = 1;
                } else {
                    nowyNrPola = numeryPol.get(numeryPol.size() - 1) + 1;
                }


                TextView tvNr = new TextView(this);
                TextView tvNazwa = new TextView(this);
                TextView tvEmpty = new TextView(this);
                TextView tvEmpty2 = new TextView(this);

                EditText etNr = new EditText(this);
                EditText etNazwa = new EditText(this);
                Button btnZapisz = new Button(this);
                Button btnUsun = new Button(this);

                tvNr.setText("Numer");
                tvNazwa.setText(" Nazwa");
                etNr.setText("" + nowyNrPola);
                tvEmpty.setText(" ");
                tvEmpty2.setText(" ");
                btnZapisz.setText("Zapisz");
                btnUsun.setText("Usuń");
                etNr.setEnabled(false); // wylacz edycje
                etNazwa.setHint("nazwa pola");

                tvNr.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                tvNazwa.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));

                btnZapisz.setOnClickListener((g) -> {
                    String nazwa = new String(etNazwa.getText().toString());
                    if (!nazwa.isEmpty()) {

                        Pole pole = new Pole(nowyNrPola, nazwa);
                        appDatabase.polaDao().insertAll(pole);
                        Toast.makeText(this, "Dodano", Toast.LENGTH_LONG).show();
                        layoutNowePole.removeAllViews();
                        licznik--;
                        reset();
                    } else {
                        Toast.makeText(this, "Nazwa nie może być pusta", Toast.LENGTH_LONG).show();
                    }

                });
                btnUsun.setOnClickListener((h) -> {
                    layoutNowePole.removeAllViews();
                    licznik--;
                });


                layoutNowePole.addView(tvNr);
                layoutNowePole.addView(tvNazwa);
                layoutNowePole.addView(tvEmpty);
                layoutNowePole.addView(tvEmpty2);
                layoutNowePole.addView(etNr);
                layoutNowePole.addView(etNazwa);
                layoutNowePole.addView(btnZapisz);
                layoutNowePole.addView(btnUsun);
                licznik++;

            }

        });
        layout.addView(btnDodaj);

    }

    protected void updatePowierzchniaPola(int numerPola) {
        List<Dzialka> dzialkaUpdate = appDatabase.dzialkiDao().findByNumer(numerPola);
        float nowaPowierzchnia = 0;
        for (Dzialka item : dzialkaUpdate) {
            nowaPowierzchnia += item.getPowierzchnia();
        }
        Pole pole = appDatabase.polaDao().findByNumer(numerPola);
        pole.setPowierzchnia(nowaPowierzchnia);
        appDatabase.polaDao().updatePowierzchnia(pole);

    }

    public void nowaDzialka() {
        Button btnDodaj = new Button(this);
        btnDodaj.setText("Dołącz nową działke do pola");

        btnDodaj.setOnClickListener((l) -> {
            if (licznik == 1) {
                Toast.makeText(Pola.this, "Dodawaj pojedyczno", Toast.LENGTH_LONG).show();
            } else {
                TextView tvNr = new TextView(this);
                TextView tvDzialka = new TextView(this);
                TextView tvPowierzchnia = new TextView(this);
                TextView tvDoplaty = new TextView(this);
                TextView tvPaliwo = new TextView(this);
                TextView tvEmpty = new TextView(this);
                TextView tvEmpty2 = new TextView(this);


                Spinner sNumerPola = new Spinner(this);
                EditText etDzialka = new EditText(this);
                EditText etPowierzchnia = new EditText(this);
                CheckBox cbDoplaty = new CheckBox(this);
                CheckBox cbPaliwo = new CheckBox(this);
                Button btnZapisz = new Button(this);
                Button btnUsun = new Button(this);


                List<Integer> numeryPol = new ArrayList<>();
                numeryPol.addAll(appDatabase.polaDao().getNumeryPol());

                ArrayAdapter<Integer> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, numeryPol);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                sNumerPola.setAdapter(adapter);


                tvNr.setText("Numer pola");
                tvDzialka.setText("Numer działki");
                tvPowierzchnia.setText("Powierzchnia");
                tvDoplaty.setText("Dopłaty");
                tvPaliwo.setText("Paliwo");
                btnZapisz.setText("Zapisz");
                btnUsun.setText("Usuń");
                tvEmpty.setText(" ");
                tvEmpty2.setText(" ");

                tvNr.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                tvDzialka.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                tvPowierzchnia.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                tvDoplaty.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                tvPaliwo.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));


                btnZapisz.setOnClickListener((g) -> {
                    String error = new String();
                    int numerPola = 0;
                    String numerDzialki = new String();
                    float powierzchnia = 0;

                    try {
                        numerPola = (Integer.parseInt(sNumerPola.getSelectedItem().toString()));
                    } catch (Exception e) {
                        error += "Niepoprawny nr pola\n";
                    }

                    numerDzialki = etDzialka.getText().toString();
                    if (numerDzialki.isEmpty()) {
                        error += "Numer dzialki nie moze byc pusty\n";
                    }

                    try {
                        powierzchnia = (Float.parseFloat(etPowierzchnia.getText().toString()));
                    } catch (Exception e) {
                        error += "Niepoprawna powierzchnia\n";
                    }
                    if (error.isEmpty()) {
                        int finalNumerPola = numerPola;
                        String finalNumerDzialki = numerDzialki;
                        float finalPowierzchnia = powierzchnia;

                        Dzialka dzialka = new Dzialka(finalNumerPola, finalNumerDzialki, finalPowierzchnia, cbDoplaty.isChecked(), cbPaliwo.isChecked());
                        appDatabase.dzialkiDao().insertAll(dzialka);
                        updatePowierzchniaPola(finalNumerPola);
                        layoutNowaDzialka.removeAllViews();
                        licznik--;
                        reset();


                    } else {
                        Toast.makeText(Pola.this, error, Toast.LENGTH_LONG).show();
                    }


                });
                btnUsun.setOnClickListener((h) -> {
                    layoutNowaDzialka.removeAllViews();
                    licznik--;
                });

                layoutNowaDzialka.addView(tvNr);
                layoutNowaDzialka.addView(tvDzialka);
                layoutNowaDzialka.addView(tvPowierzchnia);
                layoutNowaDzialka.addView(tvDoplaty);
                layoutNowaDzialka.addView(tvPaliwo);
                layoutNowaDzialka.addView(tvEmpty);
                layoutNowaDzialka.addView(tvEmpty2);

                layoutNowaDzialka.addView(sNumerPola);
                layoutNowaDzialka.addView(etDzialka);
                layoutNowaDzialka.addView(etPowierzchnia);
                layoutNowaDzialka.addView(cbDoplaty);
                layoutNowaDzialka.addView(cbPaliwo);
                layoutNowaDzialka.addView(btnZapisz);
                layoutNowaDzialka.addView(btnUsun);
                licznik++;

            }
        });
        layout.addView(btnDodaj);
    }
}
