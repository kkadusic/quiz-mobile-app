package ba.unsa.etf.rma.aktivnosti;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;

import java.io.Serializable;
import java.util.ArrayList;

import ba.unsa.etf.rma.R;
import ba.unsa.etf.rma.adapteri.ListaKvizovaAdapter;
import ba.unsa.etf.rma.klase.Kategorija;
import ba.unsa.etf.rma.klase.Kviz;
import ba.unsa.etf.rma.klase.Pitanje;

public class KvizoviAkt extends AppCompatActivity {
    private Spinner spiner;
    private ListView lista;

    private ListaKvizovaAdapter adapter;
    private ArrayList<Kviz> kvizovi = new ArrayList<Kviz>() {
        {
            add(new Kviz());
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.kvizovi_akt);


        spiner = (Spinner) findViewById(R.id.spPostojeceKategorije);
        lista = (ListView) findViewById(R.id.lvKvizovi);

        adapter = new ListaKvizovaAdapter(this, kvizovi, getResources());
        lista.setAdapter(adapter);

        String[] arraySpinner = new String[]{"Nauka", "Sport", "Jezici"};
        Spinner s = (Spinner) findViewById(R.id.spPostojeceKategorije);
        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, arraySpinner);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        s.setAdapter(adapter2);

        napuni();

        lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View view, int position, long arg) {
                Intent myIntent = new Intent(KvizoviAkt.this, DodajKvizAkt.class);
                if (position == lista.getCount() - 1) {
                    startActivity(myIntent);
                } else {
                    myIntent.putExtra("nekiKviz", kvizovi.get(position));
                    startActivity(myIntent);
                }
            }
        });

    }

    public void napuni() {
        ArrayList<String> odgovori = new ArrayList<>();
        odgovori.add("odg1");
        odgovori.add("odg2");
        odgovori.add("odg3");

        Pitanje p1 = new Pitanje("Pitanje 1", "1+1=?", odgovori, "1");
        Pitanje p2 = new Pitanje("Pitanje 2", "2+2=?", odgovori, "2");
        Pitanje p3 = new Pitanje("Pitanje 3", "3+3=?", odgovori, "3");

        ArrayList<Pitanje> pitanja = new ArrayList<>();
        pitanja.add(p1);
        pitanja.add(p2);
        pitanja.add(p3);

        Kategorija k1 = new Kategorija("Kat1", "1");
        Kategorija k2 = new Kategorija("Kat2", "2");
        Kategorija k3 = new Kategorija("Kat3", "3");

        Kviz kviz1 = new Kviz("Kviz 1", pitanja, k1);
        Kviz kviz2 = new Kviz("Kviz 2", pitanja, k2);
        Kviz kviz3 = new Kviz("Kviz 3", pitanja, k3);

        kvizovi.add(kvizovi.size()-1, kviz1);
        kvizovi.add(kvizovi.size()-1, kviz2);
        kvizovi.add(kvizovi.size()-1, kviz3);
    }

}
