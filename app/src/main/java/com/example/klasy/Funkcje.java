package com.example.klasy;

import android.content.Context;
import android.util.Log;
import android.widget.Button;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class Funkcje {


    public static ArrayList<DzialkaEW> dajDzialki(Double x, Double y, ArrayList<DzialkaEW> obecne) {
        ArrayList<DzialkaEW> output = new ArrayList<>();
        XML out = null;

        try {
            // konwersja EPSG:4326 na EPSG:2180
            URL url = new URL("http://epsg.io/trans?" + "x=" + x + "&y=" + y + "&z=0&s_srs=4326&t_srs=2180");
            Log.d("URL1", url.getQuery() + "");
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String zapis = br.readLine();

            // wyodbrebnienie
            String yn = zapis.substring(7, 16);
            String xn = zapis.substring(25, 34);

            float ynFloat = Float.parseFloat(yn);
            float xnFloat = Float.parseFloat(xn);
            // Odczyt danych z EWID
            XML parser = new XML(obecne);
            URL urlSrodek = new URL("http://integracja.gugik.gov.pl/cgi-bin/KrajowaIntegracjaEwidencjiGruntow?SERVICE=WMS&request=getFeatureInfo&version=1.3.0&layers=powiaty,zsin,obreby,dzialki,geoportal,numery_dzialek,budynki&styles=&crs=EPSG:2180&width=507&height=789&format=image/png&transparent=true&query_layers=powiaty,zsin,obreby,dzialki,geoportal,numery_dzialek,budynki&i=101&j=371&INFO_FORMAT=text/xml\n" +
                    "&bbox=" + yn + "," + xn + "," + yn + "," + xn);
            URL urlLewo = new URL("http://integracja.gugik.gov.pl/cgi-bin/KrajowaIntegracjaEwidencjiGruntow?SERVICE=WMS&request=getFeatureInfo&version=1.3.0&layers=powiaty,zsin,obreby,dzialki,geoportal,numery_dzialek,budynki&styles=&crs=EPSG:2180&width=507&height=789&format=image/png&transparent=true&query_layers=powiaty,zsin,obreby,dzialki,geoportal,numery_dzialek,budynki&i=101&j=371&INFO_FORMAT=text/xml\n" +
                    "&bbox=" + yn + "," + (xnFloat - 000004) + "," + yn + "," + (xnFloat - 000004));
            URL urlPrawo = new URL("http://integracja.gugik.gov.pl/cgi-bin/KrajowaIntegracjaEwidencjiGruntow?SERVICE=WMS&request=getFeatureInfo&version=1.3.0&layers=powiaty,zsin,obreby,dzialki,geoportal,numery_dzialek,budynki&styles=&crs=EPSG:2180&width=507&height=789&format=image/png&transparent=true&query_layers=powiaty,zsin,obreby,dzialki,geoportal,numery_dzialek,budynki&i=101&j=371&INFO_FORMAT=text/xml\n" +
                    "&bbox=" + yn + "," + (xnFloat + 000004) + "," + yn + "," + (xnFloat + 000004));
            URL urlGora = new URL("http://integracja.gugik.gov.pl/cgi-bin/KrajowaIntegracjaEwidencjiGruntow?SERVICE=WMS&request=getFeatureInfo&version=1.3.0&layers=powiaty,zsin,obreby,dzialki,geoportal,numery_dzialek,budynki&styles=&crs=EPSG:2180&width=507&height=789&format=image/png&transparent=true&query_layers=powiaty,zsin,obreby,dzialki,geoportal,numery_dzialek,budynki&i=101&j=371&INFO_FORMAT=text/xml\n" +
                    "&bbox=" + (ynFloat + 000004) + "," + xn + "," + (ynFloat + 000004) + "," + xn);
            URL urlDol = new URL("http://integracja.gugik.gov.pl/cgi-bin/KrajowaIntegracjaEwidencjiGruntow?SERVICE=WMS&request=getFeatureInfo&version=1.3.0&layers=powiaty,zsin,obreby,dzialki,geoportal,numery_dzialek,budynki&styles=&crs=EPSG:2180&width=507&height=789&format=image/png&transparent=true&query_layers=powiaty,zsin,obreby,dzialki,geoportal,numery_dzialek,budynki&i=101&j=371&INFO_FORMAT=text/xml\n" +
                    "&bbox=" + (ynFloat - 000004) + "," + xn + "," + (ynFloat - 000004) + "," + xn);

            Log.d("URL", urlDol.getQuery() + "");
            urlConnection = (HttpURLConnection) urlSrodek.openConnection();
            HttpURLConnection urlConnection1 = (HttpURLConnection) urlLewo.openConnection();
            HttpURLConnection urlConnection2 = (HttpURLConnection) urlPrawo.openConnection();
            HttpURLConnection urlConnection3 = (HttpURLConnection) urlGora.openConnection();
            HttpURLConnection urlConnection4 = (HttpURLConnection) urlDol.openConnection();
            try {
                in = new BufferedInputStream(urlConnection.getInputStream());
                InputStream in1 = new BufferedInputStream(urlConnection1.getInputStream());
                InputStream in2 = new BufferedInputStream(urlConnection2.getInputStream());
                InputStream in3 = new BufferedInputStream(urlConnection3.getInputStream());
                InputStream in4 = new BufferedInputStream(urlConnection4.getInputStream());
                parser.parse(in);
                parser.parse(in1);
                parser.parse(in2);
                parser.parse(in3);
                parser.parse(in4);
                output = parser.getDzialki();

            } finally {
                urlConnection.disconnect();
            }


        } catch (IOException e) {
            e.printStackTrace();
        }

        return output;
    }

    public static Button stworzPrzycisk(Context context, String tekst) {
        Button btn = new Button(context);
        btn.setText(tekst);
        return btn;
    }

    public static String getData() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String data = sdf.format(c.getTime());
        return data;
    }

    public static boolean saveFile(Context context, String fileName, String text) {
        try {
            FileOutputStream fos = context.openFileOutput(fileName + ".txt", Context.MODE_PRIVATE);
            Writer out = new OutputStreamWriter(fos);
            out.write(text);
            out.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static String load(Context context, String fileName, String path) {
        try {
            //FileInputStream fis = context.openFileInput(path+ File.separator+fileName + ".txt");
            FileInputStream fis = new FileInputStream(new File(path + File.separator + fileName + ".txt"));
            BufferedReader r = new BufferedReader(new InputStreamReader(fis));
            String s = "";
            String txt = "";
            while ((s = r.readLine()) != null) {
                txt += s;
            }
            r.close();
            return txt;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static int dps(Context context, int dps) {
        float scale = context.getResources().getDisplayMetrics().density;
        int pixels = (int) (dps * scale + 0.5f);
        return pixels;

    }
}
