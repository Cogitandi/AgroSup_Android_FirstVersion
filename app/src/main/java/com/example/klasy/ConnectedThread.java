package com.example.klasy;

import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

public class ConnectedThread extends Thread {
    private final BluetoothSocket mmSocket;
    private final InputStream mmInStream;
    private final OutputStream mmOutStream;
    public static final int RESPONSE_MESSAGE = 10;
    Handler uih;

    public ConnectedThread(BluetoothSocket socket, Handler uih) {
        mmSocket = socket;
        InputStream tmpIn = null;
        OutputStream tmpOut = null;
        this.uih = uih;
        Log.i("[THREAD-CT]", "Creating thread");
        try {
            tmpIn = socket.getInputStream();
            tmpOut = socket.getOutputStream();
        } catch (IOException e) {
            Log.e("[THREAD-CT]", "Error:" + e.getMessage());
        }
        mmInStream = tmpIn;
        mmOutStream = tmpOut;
        try {
            mmOutStream.flush();
        } catch (IOException e) {
            return;
        }
        Log.i("[THREAD-CT]", "IO's obtained");
    }

    public void run() {
        BufferedReader br;
        br = new BufferedReader(new InputStreamReader(mmInStream));
        Log.d("W KOD","KOD12");
        while (true) {
            Log.d("W KOD","PRZED");
            try {
                Log.d("W KOD","PO");
                //Log.d("W KOD","KOD125");
                String resp = br.readLine();
                Log.d("W KOD","KONIEC");
                Message msg = new Message();
                msg.what = RESPONSE_MESSAGE;
                msg.obj = resp;
                uih.sendMessage(msg);
            } catch (IOException e) {
                //Log.d("W KOD","BLAD");
                break;
            }
        }
        Log.i("[THREAD-CT]", "While loop ended");
    }

    public void write(byte[] bytes) {
        try {
            Log.i("[THREAD-CT]", "Writting bytes");
            mmOutStream.write(bytes);
        } catch (IOException e) {
        }
    }

    public void cancel() {
        try {
            mmSocket.close();
        } catch (IOException e) {
        }
    }
}
