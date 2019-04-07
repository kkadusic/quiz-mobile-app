package ba.unsa.etf.rma.aktivnosti;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
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
import android.widget.Toast;

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

    private static final int MY_REQUEST_CODE = 1999;
    private static final int MY_REQUEST_CODE2 = 3000;

    private ListaPitanjaAdapter adapterPitanja;
    private ArrayList<Pitanje> pitanja = new ArrayList<Pitanje>() {
        {
            add(new Pitanje());
        }
    };

    private ArrayList<Pitanje> mogucaPitanja = new ArrayList<Pitanje>();
    private ListaMogucihPitanjaAdapter adapterMogucaPitanja;

    private Integer pos;
    private Boolean novi;
    private Kviz k;
    private ArrayList<Kategorija> kategorije = new ArrayList<>();

    private ArrayAdapter<Kategorija> adapterSpiner;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dodaj_kviz_akt);


        lvDodanaPitanja = (ListView) findViewById(R.id.lvDodanaPitanja);
        lvMogucaPitanja = (ListView) findViewById(R.id.lvMogucaPitanja);
        spKategorije = (Spinner) findViewById(R.id.spKategorije);

        Bundle bundle = getIntent().getExtras();
        kategorije = bundle.getParcelableArrayList("kategorije");
        k = bundle.getParcelable("nekiKviz");
        pos = bundle.getInt("p");
        novi = bundle.getBoolean("novi");

        // SPINER
        adapterSpiner = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, kategorije);
        adapterSpiner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spKategorije.setAdapter(adapterSpiner);

        etNaziv = (EditText) findViewById(R.id.etNaziv);
        btnDodajKviz = (Button) findViewById(R.id.btnDodajKviz);

        adapterPitanja = new ListaPitanjaAdapter(this, pitanja, getResources());
        lvDodanaPitanja.setAdapter(adapterPitanja);

        adapterMogucaPitanja = new ListaMogucihPitanjaAdapter(this, mogucaPitanja, getResources());
        lvMogucaPitanja.setAdapter(adapterMogucaPitanja);


        etNaziv.setText(k.getNaziv());







        spKategorije.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long id) {
                if (position == spKategorije.getCount() - 1) {
                    Intent myIntent = new Intent(DodajKvizAkt.this, DodajKategorijuAkt.class);
                    myIntent.putExtra("kategorije", (Parcelable) new Kategorija("", ""));
                    startActivityForResult(myIntent, MY_REQUEST_CODE2);
                } else {

                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {

            }
        });


        for (Pitanje p : k.getPitanja()) {
            pitanja.add(pitanja.size() - 1, p);
        }
        napuniMogucaPitanja();


        btnDodajKviz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!etNaziv.getText().toString().isEmpty()) {
                    Kviz kviz = new Kviz();
                    kviz.setNaziv(etNaziv.getText().toString());
                    ArrayList<Pitanje> pom = pitanja;
                    pom.remove(pom.size() - 1); // da ne dodaje dodajPitanje element
                    kviz.setPitanja(pom);

                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("nekiKviz", (Parcelable) new Kviz(kviz.getNaziv(), kviz.getPitanja(), new Kategorija()));
                    returnIntent.putExtra("kategorije", kategorije);
                    returnIntent.putExtra("novi", novi);
                    returnIntent.putExtra("p", pos);
                    setResult(RESULT_OK, returnIntent);
                    finish();
                } else {
                    etNaziv.getBackground().setColorFilter(Color.parseColor("#6AFF0000"), PorterDuff.Mode.SRC_ATOP);
                }
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
                } else { // novo pitanje
                    Intent myIntent = new Intent(DodajKvizAkt.this, DodajPitanjeAkt.class);
                    myIntent.putExtra("nekoPitanje", (Parcelable) new Pitanje("", "", new ArrayList<String>(), ""));
                    startActivityForResult(myIntent, MY_REQUEST_CODE);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == MY_REQUEST_CODE && resultCode == RESULT_OK) {
            Bundle bundle = intent.getExtras();
            Pitanje pitanje = (Pitanje) bundle.getParcelable("nekoPitanje");

            pitanja.add(pitanja.size() - 1, pitanje);
            adapterPitanja.notifyDataSetChanged();

        }
        if (requestCode == MY_REQUEST_CODE2 && resultCode == RESULT_OK) {
            Bundle bundle = intent.getExtras();
            Kategorija kategorija = (Kategorija) bundle.getParcelable("kategorije");

            kategorije.add(kategorije.size() - 1, kategorija);
            adapterSpiner.notifyDataSetChanged();
        }
        if (resultCode == RESULT_CANCELED) {

        }
    }


}
