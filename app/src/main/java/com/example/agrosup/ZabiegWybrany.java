package com.example.agrosup;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Looper;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.room.Room;

import com.example.baza.AppDatabase;
import com.example.klasy.Bluetooth;
import com.example.klasy.DzialkaEW;
import com.example.klasy.Funkcje;
import com.example.klasy.Operator;
import com.example.klasy.Pole;
import com.example.klasy.SeedingProperties;
import com.example.klasy.SprayingProperties;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;

public class ZabiegWybrany extends AppCompatActivity {

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
    LinearLayout mContentView;
    ConstraintLayout interfejs;
    LinearLayout spreadingPropertiesLayout;
    TextView predkoscWypisz;
    TextView powierzchniaWypisz;
    TextView tempWypisz;
    TextView wilgWypisz;
    TextView liczbaImpulsow;

    String liczbaImpulsowZapisz;

    LayoutInflater inflater;
    View viewKalibracja;

    Thread thread;

    Button btnGdyDojedziesz;
    ArrayList<DzialkaEW> dzialki = new ArrayList<>();
    Operator operator;
    float powierzchniaPola;
    float powierzchniaPolaArduinoFloat = 0;

    TextView tvLoading;
    LocationCallback mLocationCallback;
    AppDatabase appDatabase;
    String dataRozpoczeciaZabiegu;

    String powierzchniaArduino;
    int statusWyboru = 0; // 1-rozpoczal szukanie 2- wybral pole 3-wolny zabieg

    TextView tvRodzaj;
    TextView tvOdmiana;
    TextView tvDawka;


    // Seeding properties
    SeedingProperties seedingProperties;
    List<SeedingProperties> listLastSeedingProperites;

    // sprayer properties
    TextView tvOpis;
    SprayingProperties sprayingProperties;
    List<SprayingProperties> listLastSprayingProperties;

    private String machine;


    //GPS
    private LocationRequest mLocationRequest;
    private long UPDATE_INTERVAL = 10000;  /* 10 secs */
    private long FASTEST_INTERVAL = 2000; /* 2 sec */
    Context context;
    FusedLocationProviderClient fuseLocationProvider = null;


    //
    private Bluetooth mService = null;
    private boolean mBound = false;
    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Bluetooth.LocalBinder binder = (Bluetooth.LocalBinder) service;
            mService = binder.getService();
            mBound = true;
            mService.writeData("K" + operator.getOdlegloscCzujnika()); // poinformowanie o odległości czujnika
            mService.writeData("M" + operator.getMaszyna().getSzerokosc()); // poinformowanie o szerokosci maszyny
            if (operator.getObecnie() != null) {
                if (operator.getObecnie().getPoleNr() != 0) {
                    powierzchniaPola = appDatabase.polaDao().findByNumer(operator.getObecnie().getPoleNr()).getPowierzchnia();
                    statusWyboru = 2;
                }
                if (operator.getObecnie().getPoleNr() == 0) {
                    statusWyboru = 3;
                }
                zaktualizujDaneZArduino();
                btnZakonczZabieg();
            } else {
                Log.d("COS", "DZIALA");
                zaktualizujDaneZArduino();
                btnGdyDojedziesz();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mService = null;
            mBound = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.zabieg_wybrany);

        // Inicjalize components
        mContentView = findViewById(R.id.Siewnik_ll_layout);
        predkoscWypisz = findViewById(R.id.Siewnik_tv_predkosc);
        powierzchniaWypisz = findViewById(R.id.Siewnik_tv_powierzchnia);
        tempWypisz = findViewById(R.id.Siewnik_tv_temp);
        wilgWypisz = findViewById(R.id.Siewnik_tv_wilg);
        interfejs = findViewById(R.id.Siewnik_cl_interfejs);
        powierzchniaWypisz.setVisibility(View.INVISIBLE);
        context = getApplicationContext();
        spreadingPropertiesLayout = findViewById(R.id.Siewnik_ll_spreadingProp);
        tvRodzaj = findViewById(R.id.Siewnik_SP_rodzaj);
        tvOdmiana = findViewById(R.id.Siewnik_SP_odmiana);
        tvDawka = findViewById(R.id.Siewnik_SP_dawka);
        inflater = getLayoutInflater();
        viewKalibracja =  inflater.inflate(R.layout.kalibracja, null);
        liczbaImpulsow = (TextView) viewKalibracja.findViewById(R.id.tv_kalibracja_impuls);

        // Database connection
        appDatabase = Room.databaseBuilder(this, AppDatabase.class, "AgroSup").allowMainThreadQueries().build();
        operator = appDatabase.operatorDao().getOperator();


        // Bluetooth service
        if (Bluetooth.uruchomionySerivce) {
            Intent intent = new Intent(this, Bluetooth.class);
            bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        }

        // Machine
        machine = operator.getMaszyna().getTyp();


    }

    private void progressBar() {
        tvLoading = new TextView(this);
        ProgressBar progressBar = new ProgressBar(this);
        progressBar.setId(View.generateViewId());
        tvLoading.setId(View.generateViewId());

        tvLoading.setText("Trwa wyszukiwanie pól");
        interfejs.addView(progressBar);
        interfejs.addView(tvLoading);

        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(interfejs);
        constraintSet.centerHorizontally(progressBar.getId(), interfejs.getId());
        constraintSet.centerHorizontally(tvLoading.getId(), interfejs.getId());
        constraintSet.connect(tvLoading.getId(), ConstraintSet.TOP, progressBar.getId(), ConstraintSet.BOTTOM, 10);
        constraintSet.applyTo(interfejs);


    }

    public Button btnKalibracja() {




        Button btnKalibracja = new Button(this);
        btnKalibracja.setText("Kalibracja");
        btnKalibracja.setOnClickListener((m) -> {
            viewKalibracja =  inflater.inflate(R.layout.kalibracja, null);
            liczbaImpulsow = (TextView) viewKalibracja.findViewById(R.id.tv_kalibracja_impuls);

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            mService.writeData("2");
            builder.setTitle("Kalibracja").
                    setMessage("Przejedź 100 metrów").
                    setCancelable(false).
                    setView( viewKalibracja ).
                    setPositiveButton("Zapisz", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            operator.setOdlegloscCzujnika( liczbaImpulsowZapisz);
                            appDatabase.operatorDao().updateOperator(operator);
                            mService.writeData("3");

                        }
                    }).
                    setNegativeButton("Anuluj", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            mService.writeData("3");
                        }
                    })
            ;
            AlertDialog dialog = builder.create();
            dialog.show();

        });
        return btnKalibracja;
    }

    public Button btnWolny() {
        Button btnWolny = new Button(this);
        btnWolny.setText("Wolny");
        btnWolny.setOnClickListener((m) -> {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Czy jesteś pewien?").
                    setMessage("Zabieg bez pola").
                    setPositiveButton("Tak", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            getFusedLocationProviderClient(context).removeLocationUpdates(mLocationCallback);
                            statusWyboru = 3;
                            operator.zacznijZabieg(new Pole(0, "Wolne"), dataRozpoczeciaZabiegu);
                            appDatabase.zabiegiDao().insertAll(operator.getObecnie());
                            appDatabase.operatorDao().updateOperator(operator);
                            int uid = appDatabase.zabiegiDao().getUid(operator.getObecnie().getStart());
                            operator.getObecnie().setUid(uid);
                            btnZakonczZabieg();
                        }
                    });
            AlertDialog dialog = builder.create();
            dialog.show();
        });
        return btnWolny;
    }

    public void zaktualizujDaneZArduino() {
        thread = new Thread(new Runnable() {
            @Override
            public void run() {

                while (!Thread.interrupted()) {
                    if (Bluetooth.pierwszySyngal) {
                        String temperaturaArduino = mService.temperaturaArduino;
                        String wilgotnoscArduino = mService.wilgotnoscArduino;
                        String predkoscArduino = mService.predkoscArduino;
                        powierzchniaArduino = mService.powierzchniaArduino;


                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (statusWyboru == 0) {
                                    powierzchniaWypisz.setVisibility(View.INVISIBLE);
                                } else {
                                    powierzchniaWypisz.setVisibility(View.VISIBLE);
                                }
                                predkoscWypisz.setText(predkoscArduino);
                                liczbaImpulsowZapisz = predkoscArduino.substring(0,predkoscArduino.length()-4);
                                liczbaImpulsow.setText("Liczba impulsów: " + liczbaImpulsowZapisz);
                                if (statusWyboru == 1) {
                                    powierzchniaWypisz.setText(powierzchniaArduino + " ha");
                                }
                                if (statusWyboru == 2) {
                                    powierzchniaWypisz.setText(powierzchniaArduino + "/" + powierzchniaPola + " ha");
                                }
                                if (statusWyboru == 3) {
                                    powierzchniaWypisz.setText(powierzchniaArduino + " ha");
                                }

                                tempWypisz.setText(temperaturaArduino + " " + (char) 176 + "C");
                                wilgWypisz.setText(wilgotnoscArduino);
                                powierzchniaPolaArduinoFloat = Float.parseFloat(powierzchniaArduino);
                            }
                        });
                        SystemClock.sleep(200);//
                    }
                }


            }
        });
        thread.start();


    }

    public void btnGdyDojedziesz() {
        interfejs.removeAllViews();
        btnGdyDojedziesz = new Button(this);
        btnGdyDojedziesz.setId(View.generateViewId());
        btnGdyDojedziesz.setText("Zacznij zabieg");
        btnGdyDojedziesz.setTextSize(25);
        btnGdyDojedziesz.setOnClickListener((l) -> {


            if (machine.equals(new String("Siewnik"))) {
                if (seedingProperties.checkNotEmpty()) {
                    if (listLastSeedingProperites.size() == 0) {
                        appDatabase.seedingPropertiesDao().insertAll(seedingProperties);

                    } else {
                        if (!(listLastSeedingProperites.get(0).getRodzaj().equals(seedingProperties.getRodzaj()) &&
                                listLastSeedingProperites.get(0).getOdmiana().equals(seedingProperties.getOdmiana()) &&
                                listLastSeedingProperites.get(0).getDawka().equals(seedingProperties.getDawka())))
                            appDatabase.seedingPropertiesDao().insertAll(seedingProperties);
                    }
                }
            }
            if (machine.equals(new String("Opryskiwacz"))) {
                if (sprayingProperties.checkNotEmpty()) {
                    if (listLastSprayingProperties.size() == 0) {
                        appDatabase.sprayingPropertiesDao().insertAll(sprayingProperties);
                    } else {
                        if (!(listLastSprayingProperties.get(0).getOpis().equals(sprayingProperties.getOpis())))
                            appDatabase.sprayingPropertiesDao().insertAll(sprayingProperties);
                    }
                }
            }


            dataRozpoczeciaZabiegu = Funkcje.getData();
            startLocationUpdates();
            mService.writeData("1");
            statusWyboru = 1;
            interfejs.removeAllViews();
            progressBar();
            rysujWybor(dzialki);


        });
        interfejs.addView(btnGdyDojedziesz);
        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(interfejs);
        constraintSet.centerHorizontally(btnGdyDojedziesz.getId(), interfejs.getId());
        constraintSet.applyTo(interfejs);

        ustawieniaMaszyny();


    }

    public void ustawieniaMaszyny() {
        if (machine.equals(new String("Siewnik"))) {
            showLastProperties();
            SpreadingProperties();
        }
        if (machine.equals(new String("Opryskiwacz"))) {
            showLastSprayerProperties();
            SprayerProperties();
        }
    }

    public void pokazZnalezionePola(Location loc) {
        Thread thread2 = new Thread(() -> {
            try {

                double x = loc.getLongitude();
                double y = loc.getLatitude();

                dzialki = Funkcje.dajDzialki(x, y, dzialki);
                Log.d("LICZBA DZIALEK", ":" + dzialki.size());


                runOnUiThread(() -> {

                    if (operator.getObecnie() == null) {
                        if (statusWyboru == 1) {
                            interfejs.removeAllViews();
                            progressBar();
                            rysujWybor(dzialki);
                        }
                    }

                });


            } catch (Exception e) {
                e.printStackTrace();
            }


            ////lok


        });
        thread2.start();
    }

    public void btnZakonczZabieg() {
        Button btnZakonczZabieg = new Button(this);
        btnZakonczZabieg.setId(View.generateViewId());
        btnZakonczZabieg.setText("Zakończ zabieg");
        btnZakonczZabieg.setOnClickListener((l) -> {
            powierzchniaWypisz.setVisibility(View.INVISIBLE);
            operator.skonczZabieg(powierzchniaPolaArduinoFloat); // DO POPRAWY POWIERZCHNIA

            EditText etUwagi = new EditText(this);
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Uwagi").
                    setView(etUwagi).
                    setPositiveButton("Zapisz", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            operator.getObecnie().setUwagi(etUwagi.getText().toString());
                            mService.writeData("0");
                            statusWyboru = 0;
                            appDatabase.zabiegiDao().updateZabieg(operator.getObecnie());
                            operator.setObecnie(null);
                            appDatabase.operatorDao().updateOperator(operator);
                            btnGdyDojedziesz();

                        }

                    });
            AlertDialog dialog = builder.create();
            dialog.show();


        });
        interfejs.removeAllViews();
        interfejs.addView(btnZakonczZabieg);
        btnZakonczZabieg.getLayoutParams().width = ViewGroup.LayoutParams.WRAP_CONTENT;

    }

    public void rysujWybor(ArrayList<DzialkaEW> dzialki) {

        LinearLayout ll = new LinearLayout(this);
        ll.setId(View.generateViewId());
        ll.setOrientation(LinearLayout.VERTICAL);

        for (DzialkaEW item : dzialki) {
            if (czyIstniejePolezDzialka(item.getNr())) {
                Pole pole = getPolePoNumerzeDzialki(item.getNr());
                Button btn = new Button(this);
                btn.setId(View.generateViewId());
                btn.setText(pole.getNazwa());
                btn.setOnClickListener((m) -> {

                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Czy jesteś pewien?").
                            setMessage(pole.getNazwa()).
                            setPositiveButton("Tak", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    powierzchniaPola = pole.getPowierzchnia();
                                    getFusedLocationProviderClient(context).removeLocationUpdates(mLocationCallback);

                                    statusWyboru = 2;
                                    operator.zacznijZabieg(pole, dataRozpoczeciaZabiegu);

                                    appDatabase.zabiegiDao().insertAll(operator.getObecnie());
                                    appDatabase.operatorDao().updateOperator(operator);
                                    int uid = appDatabase.zabiegiDao().getUid(operator.getObecnie().getStart());
                                    operator.getObecnie().setUid(uid);
                                    btnZakonczZabieg();
                                }
                            });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                });
                ll.addView(btn);
            }
        }
        ll.addView(btnWolny());
        ll.addView(btnKalibracja());
        interfejs.addView(ll);
        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(interfejs);
        constraintSet.connect(ll.getId(), ConstraintSet.TOP, tvLoading.getId(), ConstraintSet.BOTTOM, 10);
        constraintSet.centerHorizontally(ll.getId(), interfejs.getId());
        constraintSet.applyTo(interfejs);

    }

    public Pole getPolePoNumerzeDzialki(String numerDzialki) {

        int numerPola = appDatabase.dzialkiDao().findByNumerDzialki(numerDzialki);

        return appDatabase.polaDao().findByNumer(numerPola);
    }

    public boolean czyIstniejePolezDzialka(String numerDzialki) {
        Log.d("numer dzialki", numerDzialki);
        int numerPola;
        numerPola = appDatabase.dzialkiDao().findByNumerDzialki(numerDzialki);
        if (numerPola == 0) {
            return false;
        } else {
            return true;
        }
    }

    //GPS
    //Trigger new location updates at interval
    protected void startLocationUpdates() {
        // Create the location request to start receiving updates
        mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);

        // Create LocationSettingsRequest object using location request
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        LocationSettingsRequest locationSettingsRequest = builder.build();

        // Check whether location settings are satisfied
        // https://developers.google.com/android/reference/com/google/android/gms/location/SettingsClient
        SettingsClient settingsClient = LocationServices.getSettingsClient(this);
        settingsClient.checkLocationSettings(locationSettingsRequest);

        // new Google API SDK v11 uses getFusedLocationProviderClient(this)
        fuseLocationProvider = getFusedLocationProviderClient(this);
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                // do work here
                onLocationChanged(locationResult.getLastLocation());
                //getFusedLocationProviderClient(context).removeLocationUpdates(mLocationCallback);
            }
        };
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        getFusedLocationProviderClient(this).requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());

    }

    public void onLocationChanged(Location location) {
        pokazZnalezionePola(location);
        Log.d("Dokladnosc", "" + location.getAccuracy());

        // New location has now been determined
        /*String msg = "Updated Location: " +
                Double.toString(location.getLatitude()) + "," +
                Double.toString(location.getLongitude());
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();*/
        // You can now create a LatLng Object for use with maps
        //LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
    }


    // Seeder
    private void SpreadingProperties() {
        // 2 ostatnie
        if (seedingProperties == null) {
            seedingProperties = new SeedingProperties();
            tvRodzaj.setText("Rodzaj:");
            tvOdmiana.setText("Odmiana:");
            tvDawka.setText("Dawka:");
        } else {
            SeedingProperties temp = new SeedingProperties();
            temp.setRodzaj(seedingProperties.getRodzaj());
            temp.setOdmiana(seedingProperties.getOdmiana());
            temp.setDawka(seedingProperties.getDawka());
            seedingProperties = temp;
            tvRodzaj.setText("Rodzaj:\n" + seedingProperties.getRodzaj());
            tvOdmiana.setText("Odmiana:\n" + seedingProperties.getOdmiana());
            tvDawka.setText("Dawka:\n" + seedingProperties.getDawka() + "kg/ha");
        }


        tvRodzaj.setOnClickListener((l) -> {
            EditText etRodzaj = new EditText(this);
            etRodzaj.setText(seedingProperties.getRodzaj());
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Rodzaj").
                    setView(etRodzaj).
                    setPositiveButton("Zapisz", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            seedingProperties.setRodzaj(etRodzaj.getText().toString());
                            tvRodzaj.setText("Rodzaj:\n" + seedingProperties.getRodzaj());
                        }
                    });
            AlertDialog dialog = builder.create();
            dialog.show();
        });
        tvOdmiana.setOnClickListener((l) -> {
            EditText etOdmiana = new EditText(this);
            etOdmiana.setText(seedingProperties.getOdmiana());
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Odmiana").
                    setView(etOdmiana).
                    setPositiveButton("Zapisz", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            seedingProperties.setOdmiana(etOdmiana.getText().toString());
                            tvOdmiana.setText("Rodzaj:\n" + seedingProperties.getOdmiana());
                        }
                    });
            AlertDialog dialog = builder.create();
            dialog.show();
        });
        tvDawka.setOnClickListener((l) -> {
            EditText etDawka = new EditText(this);
            etDawka.setText(seedingProperties.getDawka());
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Dawka").
                    setView(etDawka).
                    setMessage("Podaj w kg/ha").
                    setPositiveButton("Zapisz", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            seedingProperties.setDawka(etDawka.getText().toString());
                            tvDawka.setText("Dawka:\n" + seedingProperties.getDawka() + "kg/ha");
                        }
                    });
            AlertDialog dialog = builder.create();
            dialog.show();
        });
        interfejs.addView(spreadingPropertiesLayout);
    }

    public void showLastProperties() {

        listLastSeedingProperites = appDatabase.seedingPropertiesDao().getLast();

        LinearLayout ll = new LinearLayout(this);
        ll.setId(View.generateViewId());
        ll.setOrientation(LinearLayout.VERTICAL);

        for (SeedingProperties item : listLastSeedingProperites) {
            TextView properties = new TextView(this);
            properties.setTextSize(20);
            properties.setHeight(150);
            properties.setText((item).getRodzaj() + "," + item.getOdmiana() + "," + item.getDawka());
            properties.setOnClickListener((l) -> {
                seedingProperties = item;
                btnGdyDojedziesz();
            });
            ll.addView(properties);
        }

        interfejs.addView(ll);

        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(interfejs);
        constraintSet.connect(ll.getId(), ConstraintSet.BOTTOM, spreadingPropertiesLayout.getId(), ConstraintSet.TOP, 10);
        constraintSet.centerHorizontally(ll.getId(), interfejs.getId());
        constraintSet.applyTo(interfejs);


    }

    // Sprayer
    private void SprayerProperties() {
        tvOpis = new TextView(this);

        tvOpis.setLayoutParams(new LinearLayout.LayoutParams
                (LinearLayout.LayoutParams.MATCH_PARENT, Funkcje.dps(this, 50)));


        tvOpis.setBackgroundColor(Color.CYAN);
        if (sprayingProperties == null) {
            sprayingProperties = new SprayingProperties();
            tvOpis.setText("Opis:");
        } else {
            SprayingProperties temp = new SprayingProperties();
            temp.setOpis(sprayingProperties.getOpis());
            sprayingProperties = temp;
            tvOpis.setText("Opis:\n" + sprayingProperties.getOpis());
        }


        tvOpis.setOnClickListener((l) -> {
                    EditText etRodzaj = new EditText(this);
                    etRodzaj.setText(sprayingProperties.getOpis());
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Opis").
                            setView(etRodzaj).
                            setPositiveButton("Zapisz", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    sprayingProperties.setOpis(etRodzaj.getText().toString());
                                    tvOpis.setText("Opis:\n" + sprayingProperties.getOpis());
                                }
                            });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
        );
        spreadingPropertiesLayout.removeAllViews();
        spreadingPropertiesLayout.addView(tvOpis);
        interfejs.addView(spreadingPropertiesLayout);
    }

    public void showLastSprayerProperties() {

        listLastSprayingProperties = appDatabase.sprayingPropertiesDao().getLast();

        LinearLayout ll = new LinearLayout(this);
        ll.setId(View.generateViewId());
        ll.setOrientation(LinearLayout.VERTICAL);

        for (SprayingProperties item : listLastSprayingProperties) {
            TextView properties = new TextView(this);
            properties.setTextSize(20);
            properties.setHeight(150);
            properties.setText(item.getOpis());
            properties.setOnClickListener((l) -> {
                sprayingProperties = item;
                btnGdyDojedziesz();
            });
            ll.addView(properties);
        }

        interfejs.addView(ll);

        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(interfejs);
        constraintSet.connect(ll.getId(), ConstraintSet.BOTTOM, spreadingPropertiesLayout.getId(), ConstraintSet.TOP, 10);
        constraintSet.centerHorizontally(ll.getId(), interfejs.getId());
        constraintSet.applyTo(interfejs);


    }

    @Override
    protected void onDestroy() {
        if (thread != null)
            thread.interrupt();
        Log.d("onDestroy", ":ZabiegWybrany");
        if (fuseLocationProvider != null)
            getFusedLocationProviderClient(context).removeLocationUpdates(mLocationCallback);
        if (mBound)
            unbindService(mConnection);
        super.onDestroy();
    }

}




