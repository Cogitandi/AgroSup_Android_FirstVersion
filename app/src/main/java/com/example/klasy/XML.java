package com.example.klasy;

import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class XML {
    //private List<Dzialka> dzialki= new ArrayList<>();
    private ArrayList<DzialkaEW> dzialki = new ArrayList<>();
    private DzialkaEW dzialka;
    private String text;

    int licznik = 0;

    public XML(ArrayList<DzialkaEW> obecna) {
        Log.d("ROZMIAR LISTY W XML",""+ dzialki.size());

        dzialki.addAll(obecna);
        Log.d("XML PO DODANIU", ""+dzialki.size());
    }
    public ArrayList<DzialkaEW> getDzialki() {
        return dzialki;
    }

    public void parse(InputStream is) {
        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser parser = factory.newPullParser();

            parser.setInput(is, null);

            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                String tagname = parser.getName();
                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        if (tagname.equalsIgnoreCase("Layer")) {
                            // create a new instance of employee
                            dzialka = new DzialkaEW();
                        }
                        break;

                    case XmlPullParser.TEXT:
                        text = parser.getText();
                        break;

                    case XmlPullParser.END_TAG:
                        if (tagname.equalsIgnoreCase("Layer")) {
                            // add employee object to list
                            //dzialki.add(dzialka);

                        } else if (tagname.equalsIgnoreCase("Attribute") && licznik == 0) {
                            dzialka.setId(text);
                            licznik++;
                        } else if (tagname.equalsIgnoreCase("Attribute") && licznik == 1) {
                            dzialka.setWoj(text);
                            licznik++;
                        } else if (tagname.equalsIgnoreCase("Attribute") && licznik == 2) {
                            dzialka.setPowiat(text);
                            licznik++;
                        } else if (tagname.equalsIgnoreCase("Attribute") && licznik == 3) {
                            dzialka.setGmina(text);
                            licznik++;
                        } else if (tagname.equalsIgnoreCase("Attribute") && licznik == 4) {
                            dzialka.setObreb(text);
                            licznik++;
                        } else if (tagname.equalsIgnoreCase("Attribute") && licznik == 5) {
                            dzialka.setNr(text);
                            licznik++;
                        } else if (tagname.equalsIgnoreCase("Attribute") && licznik == 6) {
                            dzialka.setKw(text);
                            licznik++;
                        } else if (tagname.equalsIgnoreCase("Attribute") && licznik == 7) {
                            dzialka.setData(text);
                            licznik++;
                        } else if (tagname.equalsIgnoreCase("Attribute") && licznik == 8) {
                            dzialka.setInf(text);
                            jezeliRozneDodaj(dzialki,dzialka);
                            //Log.d("NR",dzialka.getNr() );
                            licznik = 0;
                        }
                        break;

                    default:
                        break;
                }
                eventType = parser.next();
            }

        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void jezeliRozneDodaj(ArrayList<DzialkaEW> lista, DzialkaEW dzialka) {
        if( lista.size() ==0 ) {
            lista.add(dzialka);
        }
        for (int i = 0; i < lista.size(); i++) {
            if (lista.get(i).getNr().equals(dzialka.getNr())) {
                break;
            }
            if (i == lista.size() - 1) {
                lista.add(dzialka);
            }
        }
    }
}
