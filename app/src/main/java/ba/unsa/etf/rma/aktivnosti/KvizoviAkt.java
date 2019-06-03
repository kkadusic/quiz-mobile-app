package ba.unsa.etf.rma.aktivnosti;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import java.util.ArrayList;

import ba.unsa.etf.rma.R;
import ba.unsa.etf.rma.adapteri.MyAdapter;
import ba.unsa.etf.rma.klase.DohvatiKvizove;
import ba.unsa.etf.rma.klase.DohvatiKvizove2;
import ba.unsa.etf.rma.dto.Kategorija;
import ba.unsa.etf.rma.dto.Kviz;
import ba.unsa.etf.rma.klase.DohvatiRangListu;

public class KvizoviAkt extends AppCompatActivity implements DohvatiKvizove.IDohvatiKvizoveDone, DohvatiKvizove2.IDohvatiFilterKvizoveDone {

    static final int DODAJ_KVIZ = 100;
    static final int AZURIRAJ_KVIZ = 101;
    static final int IGRAJ_KVIZ = 102;

    private ListView lvKvizovi;
    private Spinner spPostojeceKategorije;
    private View lvFooterView;

    private ArrayList<Kategorija> kategorije = new ArrayList<>();
    private ArrayList<Kviz> sviKvizovi = new ArrayList<>();
    private ArrayList<Kviz> prikazaniKvizovi = new ArrayList<>();

    private ArrayAdapter<Kategorija> sAdapter = null;
    private MyAdapter adapter = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.kvizovi_akt);


        new DohvatiKvizove(KvizoviAkt.this, KvizoviAkt.this).execute();


        lvKvizovi = findViewById(R.id.lvKvizovi);
        spPostojeceKategorije = findViewById(R.id.spPostojeceKategorije);

        initialize();

        sAdapter = new ArrayAdapter<>(KvizoviAkt.this, android.R.layout.simple_spinner_item, kategorije);
        sAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spPostojeceKategorije.setAdapter(sAdapter);

        lvFooterView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(KvizoviAkt.this, DodajKvizAkt.class);
                intent.putParcelableArrayListExtra("kategorije", kategorije);
                intent.putParcelableArrayListExtra("kvizovi", sviKvizovi);
                intent.putExtra("requestCode", DODAJ_KVIZ);
                startActivityForResult(intent, DODAJ_KVIZ);
            }
        }
        );

        lvKvizovi.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(KvizoviAkt.this, DodajKvizAkt.class);
                intent.putParcelableArrayListExtra("kategorije", kategorije);
                intent.putParcelableArrayListExtra("kvizovi", sviKvizovi);
                intent.putExtra("kviz", (Kviz) parent.getItemAtPosition(position));
                intent.putExtra("requestCode", AZURIRAJ_KVIZ);
                startActivityForResult(intent, AZURIRAJ_KVIZ);
                return true;
            }
        });


        lvKvizovi.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(KvizoviAkt.this, IgrajKvizAkt.class);
                intent.putExtra("kviz", (Kviz) parent.getItemAtPosition(position));
                startActivity(intent);
                //intent.putExtra("requestCode", IGRAJ_KVIZ);
                //startActivityForResult(intent, IGRAJ_KVIZ);
            }
        });


        spPostojeceKategorije.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Kategorija kategorija = (Kategorija) spPostojeceKategorije.getSelectedItem();

                if (kategorija != null) {
                    if (kategorija.getId().equals("-1")) {
                        /*
                        prikazaniKvizovi.clear();
                        prikazaniKvizovi.addAll(sviKvizovi);
                        adapter.notifyDataSetChanged();
                        */
                        new DohvatiKvizove(KvizoviAkt.this, KvizoviAkt.this).execute();
                    } else {
                        prikazaniKvizovi.clear();
                        adapter.notifyDataSetChanged();
                        new DohvatiKvizove2(KvizoviAkt.this, KvizoviAkt.this, kategorija).execute();
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                spPostojeceKategorije.setSelection(0);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK) {
            if (data != null) {
                Kviz novi = data.getParcelableExtra("kviz");
                if (requestCode == AZURIRAJ_KVIZ) {
                    int index = dajIndexKviza(data.getStringExtra("staroImeKviza"));
                    sviKvizovi.set(index, novi);
                } else if (requestCode == DODAJ_KVIZ)
                    sviKvizovi.add(novi);

                //spPostojeceKategorije.setSelection(spPostojeceKategorije.getSelectedItemPosition());
                spPostojeceKategorije.setSelection(0);

                azurirajKategorije(data);
                prikazaniKvizovi.clear();
                prikazaniKvizovi.addAll(sviKvizovi);
                adapter.notifyDataSetChanged();
            }
        } else if (resultCode == RESULT_CANCELED)
            azurirajKategorije(data);
    }

    private int dajIndexKviza(String staroImeKviza) {
        for (int i = 0; i < sviKvizovi.size(); i++)
            if (sviKvizovi.get(i).getNaziv().equals(staroImeKviza))
                return i;
        return sviKvizovi.size() - 1;
    }

    public void azurirajKategorije(@Nullable Intent data) {
        kategorije.clear();
        assert data != null;
        kategorije.addAll(data.<Kategorija>getParcelableArrayListExtra("kategorije"));
        kategorije.remove(kategorije.size() - 1);
        sAdapter.notifyDataSetChanged();
    }

    private void initialize() {
        // kategorije.add(new Kategorija("Svi", "-1"));
        adapter = new MyAdapter(KvizoviAkt.this, prikazaniKvizovi, getResources());
        lvKvizovi.setAdapter(adapter);
        lvKvizovi.addFooterView(lvFooterView = adapter.getFooterView(lvKvizovi, "Dodaj kviz"));
    }

    @Override
    public void onDohvatiDone(ArrayList<Kviz> listaKvizova, ArrayList<Kategorija> listaKategorija) {
        sviKvizovi.clear();
        prikazaniKvizovi.clear();

        sviKvizovi.addAll(listaKvizova);
        prikazaniKvizovi.addAll(listaKvizova);
        adapter.notifyDataSetChanged();

        kategorije.clear();
        for (Kategorija k : listaKategorija) {
            if (k.getNaziv().equals("Svi")) {
                kategorije.add(k);
                listaKategorija.remove(k);
            }
        }
        kategorije.addAll(listaKategorija);
        sAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDohvatiFilterKvizoveDone(ArrayList<Kviz> lista) {
        prikazaniKvizovi.clear();
        prikazaniKvizovi.addAll(lista);
        adapter.notifyDataSetChanged();
    }
}