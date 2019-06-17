package ba.unsa.etf.rma.aktivnosti;

import android.Manifest;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.AlarmClock;
import android.provider.CalendarContract;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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
import ba.unsa.etf.rma.klase.NetworkUtil;
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

    private static SQLiteDatabase db = null;
    private static BazaOpenHelper bazaOpenHelper;

    private BroadcastReceiver internetBroadCast = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int status = NetworkUtil.getConnectivityStatusString(context);

            if ("android.net.conn.CONNECTIVITY_CHANGE".equals(intent.getAction())) {
                if (status == NetworkUtil.NETWORK_STATUS_NOT_CONNECTED) {
                    Log.d("TAG-net", "Nema Interneta");
                    ucitajIzSqlite();
                } else {
                    Log.d("TAG-net", "Ima Interneta");
                    ucitajSaFirebase();
                }
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.kvizovi_akt);

        initialize();

        bazaOpenHelper = new BazaOpenHelper(this);
        try {
            db = bazaOpenHelper.getWritableDatabase();
        } catch (SQLException e) {
            db = bazaOpenHelper.getReadableDatabase();
        }

        IntentFilter filter = new IntentFilter();
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(internetBroadCast, filter);
        // todo unregister reciver



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
                //int brojMinuta = procitajEvente((Kviz) parent.getItemAtPosition(position));

                //if (brojMinuta == -1) { todo odkomentarisati
                    Intent intent = new Intent(KvizoviAkt.this, IgrajKvizAkt.class);
                    intent.putExtra("kviz", (Kviz) parent.getItemAtPosition(position));
                    startActivity(intent);
                    //intent.putExtra("requestCode", IGRAJ_KVIZ);
                    //startActivityForResult(intent, IGRAJ_KVIZ);

                    //postaviAlarm((Kviz) parent.getItemAtPosition(position));
                //}
                //else {
                 //   dajAlert("Imate događaj u kalendaru za " + brojMinuta + " minuta!");
                //}
            }
        });


        spPostojeceKategorije.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Kategorija kategorija = (Kategorija) spPostojeceKategorije.getSelectedItem();

                if (kategorija != null) {
                    if (kategorija.getId().equals("-1")) {
                        if (isNetworkAvailable()){
                            ucitajSaFirebase();
                        }
                        else {
                            prikazaniKvizovi.clear();
                            prikazaniKvizovi.addAll(sviKvizovi);
                            adapter.notifyDataSetChanged();
                        }
                        // new DohvatiKvizove(KvizoviAkt.this, getResources()).execute();
                    } else {
                        if (isNetworkAvailable()) {
                            new DohvatiKvizove2(KvizoviAkt.this, getResources(), kategorija).execute();
                        }
                        else {
                            prikazaniKvizovi.clear();
                            for (Kviz k : sviKvizovi)
                                if (k.getKategorija() != null && k.getKategorija().getNaziv().equals(kategorija.getNaziv()) || kategorija.getId().equals("-1"))
                                    prikazaniKvizovi.add(k);
                            adapter.notifyDataSetChanged();
                        }
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

    private void initialize(){
        lvKvizovi = findViewById(R.id.lvKvizovi);
        spPostojeceKategorije = findViewById(R.id.spPostojeceKategorije);

        adapter = new MyAdapter(KvizoviAkt.this, prikazaniKvizovi, getResources());
        lvKvizovi.setAdapter(adapter);
        lvKvizovi.addFooterView(lvFooterView = adapter.getFooterView(lvKvizovi, "Dodaj kviz"));

        sAdapter = new ArrayAdapter<>(KvizoviAkt.this, android.R.layout.simple_spinner_item, kategorije);
        sAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spPostojeceKategorije.setAdapter(sAdapter);
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


        for (Kategorija k : kategorije)
            bazaOpenHelper.dodajKategoriju(k, db);
        Log.d("KATEGORIJE", "Upisane sve kategorije u SQLite");

        for (Kviz k : listaKvizova)
            bazaOpenHelper.dodajKviz(k, db);
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


    public void ucitajIzSqlite(){
        System.out.println("UCITAVANJE IZ SQLITE");
        sviKvizovi.clear();
        prikazaniKvizovi.clear();

        sviKvizovi.addAll(bazaOpenHelper.dohvatiKvizove(db));
        prikazaniKvizovi.addAll(bazaOpenHelper.dohvatiKvizove(db));
        for (Kviz k : sviKvizovi){
            System.out.println(k.toString());
        }
        adapter.notifyDataSetChanged();

        kategorije.clear();
        kategorije.addAll(bazaOpenHelper.dohvatiKategorije(db));
        for (Kategorija k : kategorije){
            System.out.println(k.toString());
        }
        sAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDohvatiDone(ArrayList<Pitanje> listaPitanja) { // todo preimenovati metodu
        bazaOpenHelper.obrisiSvaPitanja(db);
        for (Pitanje p : listaPitanja)
            bazaOpenHelper.dodajPitanje(p, db);
        Log.d("PITANJA", "Upisana pitanja u SQLite");
    }

    @Override
    public void onDohvatiRanglisteDone(ArrayList<Ranglista> rangliste) {
        bazaOpenHelper.obrisiSveRangliste(db);
        for (Ranglista rl : rangliste)
            bazaOpenHelper.dodajRanglistu(rl, db);
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


    public int procitajEvente(Kviz kviz) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CALENDAR}, 8);
            System.out.println("KALENDAR, zatrazi permisiju");
            // todo dodati da trazi permisiju prije nego se pozove procitajEvente
        } else if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CALENDAR) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_CALENDAR) == PackageManager.PERMISSION_GRANTED) {

            Context context = this;
            String[] projection = new String[]{CalendarContract.Events.CALENDAR_ID, CalendarContract.Events.TITLE, CalendarContract.Events.DESCRIPTION, CalendarContract.Events.DTSTART, CalendarContract.Events.DTEND, CalendarContract.Events.ALL_DAY, CalendarContract.Events.EVENT_LOCATION};

            Calendar startTime = Calendar.getInstance();

            startTime.set(Calendar.HOUR_OF_DAY, 0);
            startTime.set(Calendar.MINUTE, 0);
            startTime.set(Calendar.SECOND, 0);

            Calendar endTime = Calendar.getInstance();
            endTime.add(Calendar.DATE, 1);

            String selection = "(( " + CalendarContract.Events.DTSTART + " >= " + startTime.getTimeInMillis() + " ) AND ( " + CalendarContract.Events.DTSTART + " <= " + endTime.getTimeInMillis() + " ) AND ( deleted != 1 ))";
            Cursor cursor = context.getContentResolver().query(CalendarContract.Events.CONTENT_URI, projection, selection, null, null);


            List<String> events = new ArrayList<>();
            List<String> timestampoviPocetkaEventova = new ArrayList<>();

            if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {
                do {
                    events.add(cursor.getString(1));
                    // Log.d("start date", timestampToDate(Long.parseLong(cursor.getString(3))));
                    // Log.d("end date", timestampToDate(Long.parseLong(cursor.getString(4))));
                    timestampoviPocetkaEventova.add(cursor.getString(3));
                } while (cursor.moveToNext());
            }


            System.out.println("Nazivi eventova");
            for (String s : events) {
                System.out.println(s);
            }

            System.out.println("Datumi pocetka eventova");
            for (String timestamp : timestampoviPocetkaEventova) {
                System.out.println(timestampToDate(Long.parseLong(timestamp)));
            }


            int xMinuta = (int) Math.ceil((double) kviz.getPitanja().size() / 2);

            for (int i = 0; i < timestampoviPocetkaEventova.size(); i++) {
                Date datumEventa = timestampToDate(Long.parseLong(timestampoviPocetkaEventova.get(i)));
                int yMinuta = (int) brojMinutaDoPocetkaEventa(datumEventa);
                System.out.println("BROJ MINUTA DO POCETKA EVENTA: " + yMinuta);
                if (yMinuta > 0 && yMinuta < xMinuta){
                    return yMinuta;
                }
            }
        }
        return -1; // Sve uredu
    }


    private Date timestampToDate(long time) {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(time);
        String date = DateFormat.format("dd-MM-yyyy hh:mm:ss a", cal).toString();

        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss a");
        try {
            return format.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }


    private long brojMinutaDoPocetkaEventa(Date datumEventa){
        Date datumTrenutni = Calendar.getInstance().getTime();
        long razlika = datumEventa.getTime() - datumTrenutni.getTime();
        long sekunde = razlika / 1000;
        long minute = sekunde / 60;
        long sati = minute / 60;
        long dani = sati / 24;
        return minute;
    }


    public void dajAlert(String poruka) {
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle("Greska");
        alertDialog.setMessage(poruka);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }


    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


}