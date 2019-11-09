package com.example.klasy;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.IBinder;

import androidx.annotation.Nullable;

import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public class Bluetooth extends Service {
    private static final String TAG = "Jon";
    private BluetoothAdapter mBluetoothAdapter = null;
    private BluetoothSocket btSocket = null;
    private OutputStream outStream = null;
    private static String address = "20:18:08:34:FF:A7";
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private InputStream inStream = null;
    boolean stopWorker = false;
    public String temperaturaArduino = new String("");
    public String predkoscArduino = new String("");
    public String wilgotnoscArduino = new String("");
    public String powierzchniaArduino = new String("");
    public static boolean uruchomionySerivce = false;
    private final IBinder mBinder = new LocalBinder();
    public static boolean pierwszySyngal = false;

    final BroadcastReceiver myReceiver1 = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            Log.d("Bluetooth", "Zamykam serwis");
            stopSelf();
        }
    };

    @Override
    public void onCreate() {
        IntentFilter filter1 = new IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        registerReceiver(myReceiver1, filter1);
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        uruchomionySerivce = false;
        unregisterReceiver(myReceiver1);
        super.onDestroy();
    }

    public Bluetooth() {
        Connect();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public class LocalBinder extends Binder {
        public Bluetooth getService() {
            return Bluetooth.this;
        }
    }

    public String wyciagnijDane(String dane) {
        String output = new String();
        int p = -1;
        int k = -1;
        for (int i = 0; i < dane.length(); i++) {
            if (dane.charAt(i) == 'X')
                p = i;
            if (dane.charAt(i) == 'Y') {
                k = i;
                break;
            }

        }
        if (p != -1 && k != -1) {
            output = dane.substring(p + 1, k);
            char znak = 176;
            Log.d("OUT",output);
            temperaturaArduino = wyodrebnij(output, 't', 'w');
            wilgotnoscArduino = wyodrebnij(output, 'w', 'v') + "%";
            predkoscArduino = wyodrebnij(output, 'v', 'p') + " km/h";
            powierzchniaArduino = wyodrebnij(output, 'p', 'h');


            //Log.d("MAL",temperaturaArduino);
            /* W przypadku użycia handlera
            Message wiadomosc = new Message();
            wiadomosc.obj = output;
            handler.sendMessage(wiadomosc);
            */
            pierwszySyngal = true;
            return new String(dane.substring(k + 1, dane.length()));


        } else {
            return dane;
        }
    }

    public void beginListenForData() {
        try {
            inStream = btSocket.getInputStream();
        } catch (IOException e) {
        }

        Thread workerThread = new Thread(new Runnable() {
            public void run() {

                int licz = 1;
                String data2 = new String();

                while (!Thread.currentThread().isInterrupted() && !stopWorker) {
                    data2 = wyciagnijDane(data2);
                    //Log.d("DANE",data2);
                    try {
                        int bytesAvailable = inStream.available();

                        //
                        if (bytesAvailable > 0) {
                            byte[] packetBytes = new byte[bytesAvailable];
                            inStream.read(packetBytes);
                            String pom = new String(packetBytes, "UTF-8");

                            if (licz < 6) {
                                data2 += pom;
                                licz++;

                            } else {


                                licz = 1;
                                data2 += pom;

                            }
                        }
                    } catch (IOException ex) {
                        stopWorker = true;
                    }
                }
            }
        });

        workerThread.start();
    }

    public void Connect() {
        Log.d(TAG, address);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        Log.d(TAG, "Connecting to ... " + device);
        mBluetoothAdapter.cancelDiscovery();
        try {
            btSocket = device.createRfcommSocketToServiceRecord(MY_UUID);
            btSocket.connect();
            uruchomionySerivce = true;
            Log.d(TAG, "Connection made.");
        } catch (IOException e) {
            /*
            try {
                btSocket.close();
            } catch (IOException e2) {
                Log.d(TAG, "Unable to end the connection");
            }
            */
            Log.d(TAG, "Socket creation failed");
            stopSelf();
        }

        beginListenForData();
    }

    public void writeData(String data) {
        data += "\r";
        try {
            outStream = btSocket.getOutputStream();
        } catch (IOException e) {
            Log.d(TAG, "Bug BEFORE Sending stuff", e);
        }

        byte[] msgBuffer = data.getBytes();

        try {
            outStream.write(msgBuffer);
            Log.d(TAG, "wyslalem " + data);
        } catch (IOException e) {
            Log.d(TAG, "Bug while sending stuff", e);
        }
    }

    public String wyodrebnij(String dane, char poczatek, char koniec) {
        String output = new String("");
        int pIndeks = 0;
        int kIndeks = 0;
        for (int i = 0; i < dane.length(); i++) {
            if (dane.charAt(i) == poczatek) {
                pIndeks = i;
            }
            if (dane.charAt(i) == koniec) {
                kIndeks = i;
                break;
            }
        }
        output = dane.substring(pIndeks + 1, kIndeks);

        return output;

    }

}
