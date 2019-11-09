package com.example.agrosup;

import androidx.room.Room;
import android.graphics.Typeface;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import androidx.gridlayout.widget.GridLayout;
import android.view.View;
import android.widget.TextView;

import com.example.baza.AppDatabase;
import com.example.klasy.Zabieg;

import java.util.ArrayList;

public class historiaZabiegow extends AppCompatActivity {

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

    GridLayout layout;
    ArrayList<Zabieg> zabiegi = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.historia_zabiegow);

        layout = findViewById(R.id.historiaZabiegow_gl_layout);

        AppDatabase appDatabase = Room.databaseBuilder(this, AppDatabase.class, "AgroSup").allowMainThreadQueries().build();
        zabiegi.addAll( appDatabase.zabiegiDao().getAll() );

        wypiszNaglowki();
        wypiszZabiegi(zabiegi);


    }

    public void wypiszZabiegi(ArrayList<Zabieg> zabiegi) {
        for(Zabieg item: zabiegi) {
            TextView tvNazwaPola = new TextView(this);
            TextView tvOperator = new TextView(this);
            TextView tvPowierzchnia = new TextView(this);
            TextView tvStart = new TextView(this);
            TextView tvKoniec = new TextView(this);

            tvNazwaPola.setText(item.getNazwaPola());
            tvOperator.setText(item.getOperator());
            tvPowierzchnia.setText(""+item.getPowierzchnia() + " ha");
            tvStart.setText(""+item.getStart());
            tvKoniec.setText(""+item.getKoniec());

            layout.addView(tvNazwaPola);
            layout.addView(tvOperator);
            layout.addView(tvPowierzchnia);
            layout.addView(tvStart);
            layout.addView(tvKoniec);

        }
    }

    public void wypiszNaglowki() {
        TextView tvNazwaPola = new TextView(this);
        TextView tvOperator = new TextView(this);
        TextView tvPowierzchnia = new TextView(this);
        TextView tvStart = new TextView(this);
        TextView tvKoniec = new TextView(this);

        tvNazwaPola.setText("Nazwa pola");
        tvOperator.setText("Operator");
        tvPowierzchnia.setText("Powierzchnia");
        tvStart.setText("Start");
        tvKoniec.setText("Koniec");

        tvNazwaPola.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        tvOperator.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        tvPowierzchnia.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        tvStart.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        tvKoniec.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));

        layout.addView(tvNazwaPola);
        layout.addView(tvOperator);
        layout.addView(tvPowierzchnia);
        layout.addView(tvStart);
        layout.addView(tvKoniec);
    }
}
