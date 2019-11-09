package com.example.agrosup;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import com.example.baza.AppDatabase;
import com.example.klasy.Bluetooth;
import com.example.klasy.Funkcje;
import com.example.klasy.Maszyna;
import com.example.klasy.Operator;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import static java.lang.Thread.sleep;

public class Zabiegi extends AppCompatActivity {

    List<Maszyna> maszyny;
    LinearLayout layout;
    GridLayout siatka;
    TextView header;
    TextView tvPodlacz;
    AppDatabase appDatabase;
    Operator operator;
    BluetoothAdapter mBluetoothAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.zabiegi);

        // Inicjalize of components
        layout = findViewById(R.id.Zabiegi_ll_layout);
        header = findViewById(R.id.Zabiegi_tv_header);
        tvPodlacz = findViewById(R.id.Zabiegi_tv_arduino);

        // Database connection
        appDatabase = Room.databaseBuilder(this, AppDatabase.class, "AgroSup").allowMainThreadQueries().build();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {

        } else {
            Toast.makeText(this, "Musisz włączyć bluetooth", Toast.LENGTH_LONG).show();
            finish();
        }


    }

    @Override
    protected void onResume() {
        maszyny = appDatabase.maszynyDao().getAll();
        operator = appDatabase.operatorDao().getOperator();
        header.setText("Witaj " + operator.getImie());
        CheckBt();
        super.onResume();
    }

    private void CheckBt() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (mBluetoothAdapter == null) {
            Toast.makeText(getApplicationContext(), "Bluetooth nie wykryty!", Toast.LENGTH_SHORT).show();
        }

        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBTIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBTIntent, 0);
        }

        if (mBluetoothAdapter.isEnabled()) {
            // Launch bluetooth connection with module
            Intent intent = new Intent(this, Bluetooth.class);
            startService(intent);


            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (Bluetooth.uruchomionySerivce != true) {
                        Log.d("SERVICE STAN", Bluetooth.uruchomionySerivce + "");
                        try {
                            sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (operator.getObecnie() != null) {
                                czyPowrocic();
                            }
                            stworzWybor();
                        }
                    });


                }
            });
            thread.start();

        }
    }

    protected void czyPowrocic() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Czy chcesz powrócic do zabiegu?").
                setPositiveButton("Tak", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Log.d("Obecny zabieg", operator.getObecnie().getNazwaPola());
                        Intent intent = new Intent(getApplicationContext(), ZabiegWybrany.class);
                        startActivity(intent);
                    }
                }).setNegativeButton("Nie", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        }).setCancelable(false);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    protected void stworzWybor() {
        layout.removeView(tvPodlacz);
        if (siatka != null) siatka.removeAllViews();
        layout = findViewById(R.id.Zabiegi_ll_layout);
        siatka = new GridLayout(getApplicationContext());
        siatka.setColumnCount(2);

        operator = appDatabase.operatorDao().getOperator();
        for (final Maszyna item : maszyny) {
            if (operator.getObecnie() != null && operator.getMaszyna() != item)
                break;

            Button btn = stworzPrzycisk(item.getNazwa());
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    przenies(item);
                }
            });
            siatka.addView(btn);
        }

        layout.addView(siatka);
    }

    protected Button stworzPrzycisk(String tekst) {

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);


        Button btn = new Button(this);
        btn.setText(tekst);
        btn.setMinimumHeight(Funkcje.dps(this, 100));
        btn.setMinimumWidth(metrics.widthPixels / 2);
        return btn;
    }

    public void przenies(Maszyna maszyna) {
        operator.wybierzMaszyne(maszyna);
        appDatabase.operatorDao().updateOperator(operator);
        Intent i = new Intent(this, ZabiegWybrany.class);
        startActivity(i);
    }


    /*
    //// ************ ARDUINO ************

    protected void uruchomArduino() {
        if (arduino != null) {
            arduinoOK();
        } else {
            arduino = new Arduino(this);

        }
    }

    protected void arduinoOK() {
        if (arduino.isOpened()) {
            if (siatka == null) {
                layout.removeView(tvPodlacz);
                stworzWybor();
                istnieje = true;
            }
        }

    }

    protected void arduinoNOT() {
        if (siatka != null) {
            layout.removeView(siatka);
            layout.addView(tvPodlacz);
            siatka = null;
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        arduino.setArduinoListener(this);
        arduino.addVendorId(6790);


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        arduino.unsetArduinoListener();
        arduino.close();

    }

    @Override
    public void onArduinoAttached(UsbDevice device) {
        //display("Arduino attached!");
        arduino.open(device);
    }

    @Override
    public void onArduinoDetached() {
        arduinoNOT();
    }

    @Override
    public void onArduinoMessage(byte[] bytes) {
    }

    @Override
    public void onArduinoOpened() {
        arduinoOK();
    }

    @Override
    public void onUsbPermissionDenied() {
        //display("Permission denied... New attempt in 3 sec");
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                arduino.reopen();
            }
        }, 3000);
    }

*/

}


