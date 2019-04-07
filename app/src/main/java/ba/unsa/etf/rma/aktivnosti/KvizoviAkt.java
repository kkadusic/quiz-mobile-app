package ba.unsa.etf.rma.aktivnosti;

import android.content.Intent;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;

import ba.unsa.etf.rma.R;
import ba.unsa.etf.rma.adapteri.ListaKvizovaAdapter;
import ba.unsa.etf.rma.klase.Kategorija;
import ba.unsa.etf.rma.klase.Kviz;
import ba.unsa.etf.rma.klase.Pitanje;

public class KvizoviAkt extends AppCompatActivity {
    private Spinner spPostojeceKategorije;
    private ListView lista;

    private static final int MY_REQUEST_CODE = 1999; // za kvizove
    private static final int MY_REQUEST_CODE2 = 2000; // za kategorije

    private ListaKvizovaAdapter adapter;
    private ArrayList<Kviz> kvizovi = new ArrayList<Kviz>() {
        {
            add(new Kviz());
        }
    };
    private ArrayList<Kategorija> kategorije;
    private ArrayAdapter<Kategorija> adapterSpiner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.kvizovi_akt);


        spPostojeceKategorije = (Spinner) findViewById(R.id.spPostojeceKategorije);
        lista = (ListView) findViewById(R.id.lvKvizovi);

        adapter = new ListaKvizovaAdapter(this, kvizovi, getResources());
        lista.setAdapter(adapter);


        // SPINER
        kategorije = napuniKategorijeNew();
        adapterSpiner = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, kategorije);
        adapterSpiner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spPostojeceKategorije.setAdapter(adapterSpiner);

        spPostojeceKategorije.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long id) {
                if (position == spPostojeceKategorije.getCount() - 1) {
                    Intent myIntent = new Intent(KvizoviAkt.this, DodajKategorijuAkt.class);
                    myIntent.putExtra("nekaKategorija", (Parcelable) new Kategorija("", ""));
                    startActivityForResult(myIntent, MY_REQUEST_CODE2);
                }
                else {
                    //KvizoviAkt.this.adapter.getFilter().filter(dajImenaKvizova());
                    Toast.makeText(getBaseContext(), kategorije.get(position).getNaziv(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {

            }
        });




        napuniListuKvizova();

        lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View view, int position, long arg) {
                Intent myIntent = new Intent(KvizoviAkt.this, DodajKvizAkt.class);
                Integer pos = position;
                Boolean novi = false;
                myIntent.putExtra("p", pos);

                if (position == lista.getCount() - 1) { //novi kviz
                    myIntent.putExtra("nekiKviz", (Parcelable) new Kviz("", new ArrayList<Pitanje>(), new Kategorija("", "")));
                    myIntent.putExtra("kategorije", kategorije);
                    novi = true;
                } else { // azurirani kviz
                    myIntent.putExtra("nekiKviz", (Parcelable) kvizovi.get(position));
                    myIntent.putExtra("kategorije", kategorije);
                    novi = false;
                }
                myIntent.putExtra("novi", novi);
                startActivityForResult(myIntent, MY_REQUEST_CODE);
            }
        });



    }

    public void napuniListuKvizova() {
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

    public ArrayList<String> napuniKategorije(){
        ArrayList<String> kategorije = new ArrayList<>();
        kategorije.add("Nauka");
        kategorije.add("Sport");
        kategorije.add("Jezici");
        kategorije.add("Nova kategorija");
        return kategorije;
    }

    public ArrayList<Kategorija> napuniKategorijeNew(){
        ArrayList<Kategorija> kategorije = new ArrayList<>();
        kategorije.add(new Kategorija("Nauka", "1"));
        kategorije.add(new Kategorija("Sport", "2"));
        kategorije.add(new Kategorija("Jezici", "3"));
        kategorije.add(new Kategorija("Nova kategorija", "4"));
        return kategorije;
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == MY_REQUEST_CODE && resultCode == RESULT_OK) {
            Bundle bundle = intent.getExtras();
            Kviz kviz = (Kviz) bundle.getParcelable("nekiKviz");


            Integer pos = bundle.getInt("p");
            Boolean novi = bundle.getBoolean("novi");

            if (!novi) {
                Kviz k = kvizovi.get(pos);
                kvizovi.remove(k);
            }
            //kvizovi.add(kvizovi.size() - 1, kviz);
            kvizovi.add(pos, kviz);
            adapter.notifyDataSetChanged();
        }
        if (requestCode == MY_REQUEST_CODE2 && resultCode == RESULT_OK){
            Bundle bundle = intent.getExtras();
            Kategorija kategorija = (Kategorija) bundle.getParcelable("nekaKategorija");
            kategorije.add(kategorije.size()-1, kategorija);
            adapterSpiner.notifyDataSetChanged();
        }
        if (resultCode == RESULT_CANCELED) {

        }
    }

    public CharSequence[] dajImenaKvizova(){
        ArrayList<String> kvizici = new ArrayList<>();
        for (Kviz k : kvizovi){
            kvizici.add(k.getNaziv());
        }
        CharSequence[] cs = kvizici.toArray(new CharSequence[kvizici.size()]);
        return cs;
    }

}
