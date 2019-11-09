package com.example.agrosup;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.room.Room;

import com.example.baza.AppDatabase;
import com.example.klasy.Operator;

//public class MainActivity extends AppCompatActivity  implements ArduinoListener {
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    /*
    TextView tvObroty;
    TextView tvPredkosc;
    TextView tvKomunikat;
    Arduino arduino;
    */

    // Declaration of components
    Button MainActivity_btn_Zabiegi;
    Button MainActivity_btn_Dane;
    Button MainActivity_btn_Ustawienia;


    protected boolean hasPermission(String[] permission) {
        for (int i = 0; i < permission.length; i++) {
            if (getApplicationContext().checkSelfPermission(permission[i]) != PackageManager.PERMISSION_GRANTED)
                return false;
        }
        return true;
    }

    protected void checkPermission(String[] permission) {
        if (!hasPermission(permission))
            ActivityCompat.requestPermissions(this, permission, 1);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            for (int i = 0; i < grantResults.length; i++) {
                Log.d(grantResults[i]+"","RESULT");
                if (grantResults[i] == -1)
                    finish();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Initialization of components
        MainActivity_btn_Zabiegi = findViewById(R.id.MainActivity_btn_Zabiegi);
        MainActivity_btn_Dane = findViewById(R.id.MainActivity_btn_Dane);
        MainActivity_btn_Ustawienia = findViewById(R.id.MainActivity_btn_Ustawienia);

        // OnClick Listener
        MainActivity_btn_Zabiegi.setOnClickListener(this);
        MainActivity_btn_Dane.setOnClickListener(this);
        MainActivity_btn_Ustawienia.setOnClickListener(this);


        // Permissions for GPS, Bluetooth

        String[] permissions = {
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.BLUETOOTH_ADMIN,
                Manifest.permission.BLUETOOTH,
                Manifest.permission.READ_EXTERNAL_STORAGE};
        checkPermission(permissions);


        // Create new Operator if not exist
        AppDatabase appDatabase = Room.databaseBuilder(this, AppDatabase.class, "AgroSup").allowMainThreadQueries().build();
        if (appDatabase.operatorDao().getCount() == 0) {
            Operator operator = new Operator("operator");
            appDatabase.operatorDao().insert(operator);
        }


        //String c = Funkcje.load(getApplicationContext(), "a", "/storage/emulated/0/");
        //Log.d("C:", c + "");
    }

    @Override
    public void onClick(View v) {
        View clicked = v;

        if (clicked == MainActivity_btn_Zabiegi) {
            Intent intent = new Intent(getApplicationContext(), Zabiegi.class);
            startActivity(intent);
        }
        if (clicked == MainActivity_btn_Dane) {
            Intent intent = new Intent(getApplicationContext(), Dane.class);
            startActivity(intent);
        }
        if (clicked == MainActivity_btn_Ustawienia) {
            Intent intent = new Intent(getApplicationContext(), Ustawienia.class);
            startActivity(intent);
        }
    }
/*
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
        display("Arduino attached!");
        arduino.open(device);
    }

    @Override
    public void onArduinoDetached() {
        display("Arduino detached");
    }

    @Override
    public void onArduinoMessage(byte[] bytes) {
        String messange = new String(bytes);


        int p=0;
        for(int i=0;i<messange.length();i++){
            if(messange.charAt(p) == 'n') {
                break;
            }
            p++;
        }
        final String a1=messange.substring(0,p);
        final String a2=messange.substring(p+1,messange.length());

        Handler handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                // Any UI task, example
                tvPredkosc.setText(a2);
                tvObroty.setText(a1);
            }
        };
        handler.sendEmptyMessage(1);
    }

    @Override
    public void onArduinoOpened() {
        String str = "Hello World !";
        //arduino.send(str.getBytes());
    }

    @Override
    public void onUsbPermissionDenied() {
        display("Permission denied... New attempt in 3 sec");
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                arduino.reopen();
            }
        }, 3000);
    }

    public void display(final String message){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tvKomunikat.append(message+"\n");
            }
        });
    }
*/

}
