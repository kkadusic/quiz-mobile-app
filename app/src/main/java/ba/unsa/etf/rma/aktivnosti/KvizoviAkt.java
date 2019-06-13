package ba.unsa.etf.rma.aktivnosti;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.AlarmClock;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import ba.unsa.etf.rma.R;
import ba.unsa.etf.rma.adapteri.MyAdapter;
import ba.unsa.etf.rma.dto.Pitanje;
import ba.unsa.etf.rma.dto.Ranglista;
import ba.unsa.etf.rma.klase.DohvatiKvizove;
import ba.unsa.etf.rma.klase.DohvatiKvizove2;
import ba.unsa.etf.rma.dto.Kategorija;
import ba.unsa.etf.rma.dto.Kviz;
import ba.unsa.etf.rma.klase.DohvatiPitanja;
import ba.unsa.etf.rma.klase.DohvatiRangListu;
import ba.unsa.etf.rma.klase.NetworkChangeReceiver;
import ba.unsa.etf.rma.sqlite.BazaOpenHelper;

public class KvizoviAkt extends AppCompatActivity implements DohvatiKvizove.IDohvatiKvizoveDone,
        DohvatiKvizove2.IDohvatiFilterKvizoveDone, DohvatiPitanja.IDohvatiPitanjaDone,
        DohvatiRangListu.IDohvatiRangListeDone {

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

    public static SQLiteDatabase db = null;
    public static BazaOpenHelper bazaOpenHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.kvizovi_akt);


        bazaOpenHelper = new BazaOpenHelper(this);
        try {
            db = bazaOpenHelper.getWritableDatabase();
        } catch (SQLException e) {
            db = bazaOpenHelper.getReadableDatabase();
        }

        // bazaOpenHelper.obrisiSveIzTabela(db);
        // new DohvatiKvizove(KvizoviAkt.this, getResources()).execute();
        ucitajSaFirebase();


        Intent i = new Intent(KvizoviAkt.this, NetworkChangeReceiver.class);
        KvizoviAkt.this.sendBroadcast(i);


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

                postaviAlarm((Kviz) parent.getItemAtPosition(position));

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
                        // prikazaniKvizovi.clear();
                        // prikazaniKvizovi.addAll(sviKvizovi);
                        // adapter.notifyDataSetChanged();

                        // bazaOpenHelper.obrisiSveIzTabela(db);
                        // new DohvatiKvizove(KvizoviAkt.this, getResources()).execute();
                        ucitajSaFirebase();
                    } else {
                        prikazaniKvizovi.clear();
                        adapter.notifyDataSetChanged();

                        new DohvatiKvizove2(KvizoviAkt.this, getResources(), kategorija).execute();
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
        /*
        for (int i = 0; i < listaKategorija.size(); i++) { todo namjestiti
            if (listaKategorija.get(i).getNaziv().equals("Svi")) {
                kategorije.add(listaKategorija.get(i));
                listaKategorija.remove(listaKategorija.get(i));
            }
        }
        */
        kategorije.addAll(listaKategorija);
        sAdapter.notifyDataSetChanged();


        // Ucitavanje kvizova i kategorija u lokalnu bazu
        for (Kategorija k : listaKategorija) {
            bazaOpenHelper.dodajKategoriju(k, db);
        }
        Log.d("KATEGORIJE", "Upisane sve kategorije u SQLite");

        for (Kviz k : listaKvizova) {
            bazaOpenHelper.dodajKviz(k, db);
        }
        Log.d("KVIZOVI", "Upisani kvizovi u SQLite");
    }

    @Override
    public void onDohvatiFilterKvizoveDone(ArrayList<Kviz> filtriraniKvizovi) {
        prikazaniKvizovi.clear();
        prikazaniKvizovi.addAll(filtriraniKvizovi);
        adapter.notifyDataSetChanged();
    }

    public void ucitajSaFirebase() {
        bazaOpenHelper.obrisiSveIzTabela(db);
        new DohvatiKvizove(KvizoviAkt.this, getResources()).execute();
        new DohvatiPitanja(KvizoviAkt.this, getResources()).execute();
        new DohvatiRangListu(KvizoviAkt.this, getResources()).execute();
    }

    @Override
    public void onDohvatiDone(ArrayList<Pitanje> listaPitanja) { // todo preimenovati metodu
        // Ucitavanje pitanja u lokalnu bazu
        bazaOpenHelper.obrisiSvaPitanja(db);
        for (Pitanje p : listaPitanja) {
            bazaOpenHelper.dodajPitanje(p, db);
        }
        Log.d("PITANJA", "Upisana pitanja u SQLite");
    }

    @Override
    public void onDohvatiRanglisteDone(ArrayList<Ranglista> rangliste) {
        bazaOpenHelper.obrisiSveRangliste(db);
        for (Ranglista rl : rangliste) {
            bazaOpenHelper.dodajRanglistu(rl, db);
        }
        Log.d("RANGLISTE", "Upisane rangliste u SQLite");
    }


    public void postaviAlarm(Kviz kviz) {
        Calendar rightNow = Calendar.getInstance();
        int satiTrenutno = rightNow.get(Calendar.HOUR_OF_DAY);
        Log.d("VRIJEME", "Trenutno sati: " + satiTrenutno);
        int minuteTrenutno = rightNow.get(Calendar.MINUTE);
        Log.d("VRIJEME", "Trenutno minuta: " + minuteTrenutno);

        if (kviz.getPitanja().size() > 0) {
            int potrebnoMinuta = (int) Math.ceil((double) kviz.getPitanja().size() / 2) + 1 + minuteTrenutno;

            if (potrebnoMinuta > 59) {
                satiTrenutno++;
                potrebnoMinuta -= 60;
            }

            Log.d("VRIJEME", "Postavi u " + satiTrenutno + " " + potrebnoMinuta);

            Intent i = new Intent(AlarmClock.ACTION_SET_ALARM);
            i.putExtra(AlarmClock.EXTRA_SKIP_UI, true);
            i.putExtra(AlarmClock.EXTRA_HOUR, satiTrenutno);
            i.putExtra(AlarmClock.EXTRA_MINUTES, potrebnoMinuta);
            i.putExtra(AlarmClock.EXTRA_MESSAGE, "Alarm za kviz");
            startActivity(i);
        }
    }
}