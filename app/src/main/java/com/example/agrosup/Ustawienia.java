package com.example.agrosup;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.gridlayout.widget.GridLayout;
import androidx.loader.content.CursorLoader;
import androidx.room.Room;

import com.example.baza.AppDatabase;
import com.example.klasy.Dzialka;
import com.example.klasy.Funkcje;
import com.example.klasy.Maszyna;
import com.example.klasy.Operator;
import com.example.klasy.Pole;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;

public class Ustawienia extends AppCompatActivity {

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

    //Declarations
    Context context;
    LinearLayout layout;
    GridLayout layoutOperator;
    GridLayout layoutMaszyny;
    GridLayout layoutDodaj;
    ArrayList<Maszyna> maszyny = new ArrayList<>();
    AppDatabase appDatabase;
    Operator operator;
    int licznik = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ustawienia);

        //Inicjalization
        context = getApplicationContext();
        layout = findViewById(R.id.Ustawienia_ll_layout);
        layoutOperator = findViewById(R.id.Ustawienia_gl_operator);
        layoutMaszyny = findViewById(R.id.Ustawienia_gl_maszyny);
        layoutDodaj = findViewById(R.id.Ustawienia_gl_dodaj);

        //Polaczenie z baza
        appDatabase = Room.databaseBuilder(this, AppDatabase.class, "AgroSup").allowMainThreadQueries().build();

        operator = appDatabase.operatorDao().getOperator();

        // Gui
        wypiszUstawienia();


        Button imp = new Button(this);
        imp.setText("Importuj");
        imp.setOnClickListener((l) -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("*/*");      //all files
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            startActivityForResult(intent, 111);
        });
        layout.addView(imp);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Uri currFileURI = data.getData();
        String txt = "";

        try {
            BufferedReader r = new BufferedReader(new InputStreamReader(getContentResolver().openInputStream(currFileURI)));
            String s = "";
            //txt = "";
            while ((s = r.readLine()) != null) {
                //txt += s;
                dodajDoListy(s);
            }
            r.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    protected void dodajDoListy(String text) {
        //dzialka
        int numerPola;
        String numerDzialki;
        float powierzchniaDzialki;
        String nazwaPola;

        int a=0;
        int b=0;
        int c=0;
        int d=0;

        for( int i=0;i<text.length();i++) {
            if( text.charAt(i) == ';' ) {
                if( a == 0 ) a = i;
                else if( b == 0 ) b = i;
                else if( c == 0 ) c = i;
                else if( d == 0 ) d = i;
            }
        }
        numerPola = Integer.parseInt( text.substring(0,a) );
        numerDzialki = text.substring(a+1,b);
        powierzchniaDzialki = Float.parseFloat( text.substring(b+1,c) );
        nazwaPola = text.substring(c+1,d);

        if( !nazwaPola.isEmpty() ) {
            Pole pole = new Pole(numerPola,nazwaPola);
            appDatabase.polaDao().insertAll(pole);
        }
        Dzialka dzialka = new Dzialka(numerPola,numerDzialki,powierzchniaDzialki/10000,true,true);
        appDatabase.dzialkiDao().insertAll(dzialka);
        Pole poleo = appDatabase.polaDao().findByNumer(dzialka.getNumerPola());
        poleo.setPowierzchnia(poleo.getPowierzchnia()+dzialka.getPowierzchnia());

        appDatabase.polaDao().updatePowierzchnia(poleo);
        Log.d("dane",dzialka.getNumerDzialki()+","+dzialka.getNumerPola() );
        //Log.d("dane", text.substring(0,a)+","+numerDzialki+","+","+nazwaPola);
    }

    protected void operator() {
        TextView tvOperator = new TextView(context);
        EditText etOperator = new EditText(context);
        Button btnZapiszOperator = new Button(context);
        TextView tvDlugoscKolo = new TextView(context);
        EditText etDlugoscKolo = new EditText(context);
        Button btnZapiszKolo = new Button(context);


        tvOperator.setText("Operator");
        etOperator.setHint("Wprowadź swoje imie");
        btnZapiszOperator.setText("Zapisz");
        tvDlugoscKolo.setText("Ilość impulsów");
        //etDlugoscKolo.setHint("w centrymetach");
        btnZapiszKolo.setText("Zapisz");

        tvOperator.setId(R.id.Ustawienia_et_operator);

        etOperator.setText(operator.getImie());
        etDlugoscKolo.setText(operator.getOdlegloscCzujnika());

        btnZapiszOperator.setOnClickListener((l) -> {
            operator.setImie(etOperator.getText().toString());
            appDatabase.operatorDao().updateOperator(operator);
            Toast.makeText(Ustawienia.this, "Zapisano", Toast.LENGTH_LONG).show();
        });
        btnZapiszKolo.setOnClickListener((l) -> {
            operator.setOdlegloscCzujnika(etDlugoscKolo.getText().toString());
            appDatabase.operatorDao().updateOperator(operator);
            Toast.makeText(Ustawienia.this, "Zapisano", Toast.LENGTH_LONG).show();

            /*
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder.setTitle("Kalibracja").
                    setMessage("Wprowadź ręcznie ilość impulsów lub przejedź 100 metrów").
                    setView(etDlugoscKolo).
                    setPositiveButton("Zapisz", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            operator.setOdlegloscCzujnika(etDlugoscKolo.getText().toString());
                            appDatabase.operatorDao().updateOperator(operator);
                            Toast.makeText(Ustawienia.this, "Zapisano", Toast.LENGTH_LONG).show();
                            recreate();
                        }
                    });
            AlertDialog dialog = builder.create();
            dialog.show();

            /*

            */
        });

        layoutOperator.addView(tvOperator);
        layoutOperator.addView(etOperator);
        layoutOperator.addView(btnZapiszOperator);
        layoutOperator.addView(tvDlugoscKolo);
        layoutOperator.addView(etDlugoscKolo);
        layoutOperator.addView(btnZapiszKolo);
        layout.addView(layoutOperator);
    }

    protected void wypiszUstawienia() {
        layoutOperator.removeAllViews();
        layoutMaszyny.removeAllViews();
        layoutDodaj.removeAllViews();
        layout.removeAllViews();
        operator();
        wypiszMaszyny();
        dodajMaszyne();
    }

    public void dodajMaszyne() {
        Button btnDodaj = new Button(context);
        btnDodaj.setText("Dodaj maszyne");
        btnDodaj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View l) {
                if (licznik == 1) {
                    Toast.makeText(Ustawienia.this, "Dodawaj pojedyczno", Toast.LENGTH_LONG).show();
                } else {
                    licznik++;
                    final EditText etNazwa = new EditText(context);
                    final EditText etNazwazabiegu = new EditText(context);
                    final EditText etSzerokosc = new EditText(context);
                    Spinner kindMachine = new Spinner(context);
                    final Button btnZapisz = new Button(context);
                    final Button btnUsun = new Button(context);

                    etNazwa.setHint("maszyna");
                    etNazwazabiegu.setHint("zabieg");
                    etSzerokosc.setHint("Szerokosc");
                    btnZapisz.setText("Zapisz");
                    btnUsun.setText("Usun");

                    List<String> machineKindList = new ArrayList<>();
                    machineKindList.add("Siewnik");
                    machineKindList.add("Opryskiwacz");
                    machineKindList.add("Inne");
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplication(), android.R.layout.simple_spinner_dropdown_item, machineKindList);
                    kindMachine.setAdapter(adapter);

                    btnZapisz.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View l) {
                            String nazwaMaszyny = etNazwa.getText().toString();
                            int szerokosc = 0;
                            String error = new String();

                            try {
                                szerokosc = Integer.parseInt(etSzerokosc.getText().toString());
                            } catch (Exception e) {
                                error = "Zla szerokość\n";
                            }
                            if (nazwaMaszyny.isEmpty()) {
                                error += "Nazwa maszyny nie mozna byc pusta\n";
                            }
                            if (szerokosc == 0) {
                                error += "Szerokość nie może być pusta\n";
                            }

                            if (error.isEmpty()) {
                                Maszyna maszyna = new Maszyna(nazwaMaszyny, szerokosc, kindMachine.getSelectedItem().toString());
                                appDatabase.maszynyDao().insertAll(maszyna);
                                Toast.makeText(Ustawienia.this, "Zapisano", Toast.LENGTH_LONG).show();
                                wypiszUstawienia();
                                licznik--;
                            } else {
                                Toast.makeText(Ustawienia.this, error, Toast.LENGTH_LONG).show();
                            }
                        }
                    });

                    btnUsun.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View l) {
                            layoutMaszyny.removeView(etNazwa);
                            layoutMaszyny.removeView(etSzerokosc);
                            layoutMaszyny.removeView(kindMachine);
                            layoutMaszyny.removeView(btnZapisz);
                            layoutMaszyny.removeView(btnUsun);
                            licznik--;

                        }
                    });

                    layoutMaszyny.addView(etNazwa);
                    layoutMaszyny.addView(etSzerokosc);
                    layoutMaszyny.addView(kindMachine);
                    layoutMaszyny.addView(btnZapisz);
                    layoutMaszyny.addView(btnUsun);
                }


            }
        });
        layout.addView(btnDodaj);
    }

    public void wypiszMaszyny() {
        // wyswietlanie maszyn
        maszyny.clear();
        maszyny.addAll(appDatabase.maszynyDao().getAll());

        for (final Maszyna item : maszyny) {
            final TextView tvNazwa = new TextView(context);

            final TextView tvSzerokosc = new TextView(context);
            final Button btnUsun = new Button(context);
            TextView empty = new TextView(context);
            TextView empty2 = new TextView(context);
            empty.setText("");
            empty2.setText("");

            tvNazwa.setText(item.getNazwa());
            tvSzerokosc.setText("" + item.getSzerokosc());
            btnUsun.setText("Usuń");
            btnUsun.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View l) {
                    appDatabase.maszynyDao().delete(item);
                    layoutMaszyny.removeView(tvNazwa);
                    layoutMaszyny.removeView(tvSzerokosc);
                    layoutMaszyny.removeView(btnUsun);
                    layoutMaszyny.removeView(empty);
                    layoutMaszyny.removeView(empty2);

                }
            });

            layoutMaszyny.addView(empty);
            layoutMaszyny.addView(tvNazwa);
            layoutMaszyny.addView(tvSzerokosc);
            layoutMaszyny.addView(btnUsun);
            layoutMaszyny.addView(empty2);

        }
        layout.addView(layoutMaszyny);
    }

}
