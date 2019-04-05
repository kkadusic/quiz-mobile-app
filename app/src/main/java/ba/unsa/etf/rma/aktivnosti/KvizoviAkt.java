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
    private ArrayList<Kviz> kvizovi = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.kvizovi_akt);

        napuni();
        spiner = (Spinner) findViewById(R.id.spPostojeceKategorije);
        lista = (ListView) findViewById(R.id.lvKvizovi);

        adapter = new ListaKvizovaAdapter(this, kvizovi, getResources());
        lista.setAdapter(adapter);

        String[] arraySpinner = new String[]{"Nauka", "Sport", "Jezici"};
        Spinner s = (Spinner) findViewById(R.id.spPostojeceKategorije);
        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, arraySpinner);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        s.setAdapter(adapter2);

        lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View view, int position, long arg) {
                if (position == lista.getCount() - 1) {
                    Intent appInfo = new Intent(KvizoviAkt.this, DodajKvizAkt.class);
                    startActivity(appInfo);
                }
            }
        });

    }

    private void napuni() {
        ArrayList<Pitanje> pitanja = new ArrayList<>();
        Kategorija kategorija = new Kategorija();
        Kviz a = new Kviz("Kviz 1", pitanja, kategorija);
        Kviz b = new Kviz("Kviz 2", pitanja, kategorija);
        Kviz c = new Kviz("Kviz 3", pitanja, kategorija);
        kvizovi.add(a);
        kvizovi.add(b);
        kvizovi.add(c);
    }

}
