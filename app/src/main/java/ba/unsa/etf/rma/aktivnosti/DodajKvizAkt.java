package ba.unsa.etf.rma.aktivnosti;

import android.content.Intent;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;

import java.io.Serializable;
import java.util.ArrayList;

import ba.unsa.etf.rma.R;
import ba.unsa.etf.rma.adapteri.ListaKvizovaAdapter;
import ba.unsa.etf.rma.adapteri.ListaMogucihPitanjaAdapter;
import ba.unsa.etf.rma.adapteri.ListaPitanjaAdapter;
import ba.unsa.etf.rma.klase.Kategorija;
import ba.unsa.etf.rma.klase.Kviz;
import ba.unsa.etf.rma.klase.Pitanje;

public class DodajKvizAkt extends AppCompatActivity {

    private ListView lvDodanaPitanja;
    private ListView lvMogucaPitanja;
    private EditText etNaziv;
    private Spinner spKategorije;
    private Button btnDodajKviz;

    private ListaPitanjaAdapter adapterPitanja;
    private ArrayList<Pitanje> pitanja = new ArrayList<Pitanje>() {
        {
            add(new Pitanje());
        }
    };

    private ArrayList<Pitanje> pitanja2 = new ArrayList<>();

    private ArrayList<Pitanje> mogucaPitanja = new ArrayList<Pitanje>();
    private ListaMogucihPitanjaAdapter adapterMogucaPitanja;

    private Integer pos;
    private Boolean novi;
    private Kviz k;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dodaj_kviz_akt);

        //Kviz k = (Kviz) getIntent().getParcelableExtra("nekiKviz");
        ArrayList<String> kategorije = (ArrayList<String>) getIntent().getStringArrayListExtra("kategorije");


        Bundle bundle = getIntent().getExtras();
        //k = (Kviz) bundle.getParcelable("nekiKviz");
        k = (Kviz) getIntent().getParcelableExtra("nekiKviz");
        pos = bundle.getInt("p");
        novi = bundle.getBoolean("novi");

        lvDodanaPitanja = (ListView) findViewById(R.id.lvDodanaPitanja);
        lvMogucaPitanja = (ListView) findViewById(R.id.lvMogucaPitanja);
        spKategorije = (Spinner) findViewById(R.id.spKategorije);

        etNaziv = (EditText) findViewById(R.id.etNaziv);
        etNaziv.setText(k.getNaziv());
        btnDodajKviz = (Button) findViewById(R.id.btnDodajKviz);

        adapterPitanja = new ListaPitanjaAdapter(this, pitanja, getResources());
        lvDodanaPitanja.setAdapter(adapterPitanja);

        adapterMogucaPitanja = new ListaMogucihPitanjaAdapter(this, mogucaPitanja, getResources());
        lvMogucaPitanja.setAdapter(adapterMogucaPitanja);


        // SPINER
        Spinner s = (Spinner) findViewById(R.id.spKategorije);
        final ArrayAdapter<String> adapterSpiner = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, kategorije);
        adapterSpiner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        s.setAdapter(adapterSpiner);


        for (Pitanje p : k.getPitanja()) {
            pitanja.add(pitanja.size() - 1, p);
        }
        napuniMogucaPitanja();


        /*
        btnDodajKviz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(DodajKvizAkt.this, KvizoviAkt.class);
                Kviz k = new Kviz(etNaziv.getText().toString(), pitanja, new Kategorija());
                myIntent.putExtra("noviKviz", (Parcelable) k);
                startActivity(myIntent);
            }
        });
        */

        btnDodajKviz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Kviz kviz = new Kviz();
                kviz.setNaziv(etNaziv.getText().toString());
                ArrayList<Pitanje> pom = pitanja;
                pom.remove(pom.size() - 1); // da ne dodaje dodajPitanje element
                kviz.setPitanja(pom);

                Intent returnIntent = new Intent();
                returnIntent.putExtra("nekiKviz", (Parcelable) new Kviz(kviz.getNaziv(), kviz.getPitanja(), new Kategorija()));
                returnIntent.putExtra("novi", novi);
                returnIntent.putExtra("p", pos);
                setResult(RESULT_OK, returnIntent);
                finish();
            }
        });



        lvDodanaPitanja.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View view, int position, long arg) {
                if (position != lvDodanaPitanja.getCount() - 1) {
                    mogucaPitanja.add(mogucaPitanja.size(), pitanja.get(position));
                    pitanja.remove(position);
                    adapterMogucaPitanja.notifyDataSetChanged();
                    adapterPitanja.notifyDataSetChanged();
                }
                else {
                    Intent myIntent = new Intent(DodajKvizAkt.this, DodajPitanjeAkt.class);
                    startActivity(myIntent);
                }
            }
        });

        lvMogucaPitanja.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View view, int position, long arg) {
                pitanja.add(pitanja.size() - 1, mogucaPitanja.get(position));
                mogucaPitanja.remove(position);
                adapterPitanja.notifyDataSetChanged();
                adapterMogucaPitanja.notifyDataSetChanged();
            }
        });

    }

    public void napuniMogucaPitanja() {
        ArrayList<String> odgovori = new ArrayList<>();
        odgovori.add("odg1");
        odgovori.add("odg2");
        odgovori.add("odg3");

        Pitanje p1 = new Pitanje("Moguce 1", "1+1=?", odgovori, "1");
        Pitanje p2 = new Pitanje("Moguce 2", "2+2=?", odgovori, "2");
        Pitanje p3 = new Pitanje("Moguce 3", "3+3=?", odgovori, "3");

        mogucaPitanja.add(p1);
        mogucaPitanja.add(p2);
        mogucaPitanja.add(p3);
    }

}
