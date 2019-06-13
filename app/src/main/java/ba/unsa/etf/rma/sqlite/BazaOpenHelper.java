package ba.unsa.etf.rma.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;

import ba.unsa.etf.rma.aktivnosti.KvizoviAkt;
import ba.unsa.etf.rma.dto.Kategorija;
import ba.unsa.etf.rma.dto.Kviz;
import ba.unsa.etf.rma.dto.Pitanje;
import ba.unsa.etf.rma.dto.Ranglista;

public class BazaOpenHelper extends SQLiteOpenHelper { //implements Serializable?

    private static final String DATABASE_NAME = "kvizovi.db";
    private static final int DATABASE_VERSION = 14;


    // Kreiranje tabele Kategorija
    private static final String DATABASE_TABLE_KATEGORIJA = "Kategorija";
    private static final String KATEGORIJA_ID = "_id";
    private static final String KATEGORIJA_NAZIV = "naziv";
    private static final String KATEGORIJA_ID_IKONICE = "idIkonice";

    private static final String DATABASE_CREATE_KATEGORIJA = "CREATE TABLE " +
            DATABASE_TABLE_KATEGORIJA + " ("
            + KATEGORIJA_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + KATEGORIJA_NAZIV + " TEXT NOT NULL, "
            + KATEGORIJA_ID_IKONICE + " INTEGER NOT NULL);";


    // Kreiranje tabele Pitanje
    private static final String DATABASE_TABLE_PITANJE = "Pitanje";
    private static final String PITANJE_ID = "_id";
    private static final String PITANJE_NAZIV = "naziv";
    private static final String PITANJE_INDEX_TACNOG = "indexTacnog";
    private static final String PITANJE_ODGOVORI = "odgovori";

    private static final String DATABASE_CREATE_PITANJE = "CREATE TABLE " +
            DATABASE_TABLE_PITANJE + " ("
            + PITANJE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + PITANJE_NAZIV + " TEXT NOT NULL, "
            + PITANJE_INDEX_TACNOG + " INTEGER NOT NULL, "
            + PITANJE_ODGOVORI + " TEXT NOT NULL);";


    // Kreiranje tabele Kviz
    private static final String DATABASE_TABLE_KVIZ = "Kviz";
    private static final String KVIZ_ID = "_id";
    private static final String KVIZ_NAZIV = "naziv";
    private static final String KVIZ_ID_KATEGORIJE = "idKategorije";
    private static final String KVIZ_PITANJA = "pitanja";

    private static final String DATABASE_CREATE_KVIZ = "CREATE TABLE " +
            DATABASE_TABLE_KVIZ + " ("
            + KVIZ_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + KVIZ_NAZIV + " TEXT NOT NULL, "
            + KVIZ_ID_KATEGORIJE + " TEXT NOT NULL, "
            + KVIZ_PITANJA + " TEXT NOT NULL);";


    // Kreiranje tabele Ranglista
    private static final String DATABASE_TABLE_RANGLISTA = "Ranglista";
    private static final String RANGLISTA_ID = "_id";
    private static final String RANGLISTA_KVIZ_NAZIV = "nazivKviza";
    private static final String RANGLISTA_POZICIJA = "pozicija";
    private static final String RANGLISTA_IGRAC_NAZIV = "nazivIgraca";
    private static final String RANGLISTA_PROCENAT_TACNIH = "procenatTacnih";

    private static final String DATABASE_CREATE_RANGLISTA = "CREATE TABLE " +
            DATABASE_TABLE_RANGLISTA + " ("
            + RANGLISTA_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + RANGLISTA_KVIZ_NAZIV + " TEXT NOT NULL, "
            + RANGLISTA_POZICIJA + " TEXT NOT NULL, "
            + RANGLISTA_IGRAC_NAZIV + " TEXT NOT NULL, "
            + RANGLISTA_PROCENAT_TACNIH + " TEXT NOT NULL);";


    public BazaOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DATABASE_CREATE_KATEGORIJA);
        db.execSQL(DATABASE_CREATE_PITANJE);
        db.execSQL(DATABASE_CREATE_KVIZ);
        db.execSQL(DATABASE_CREATE_RANGLISTA);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE_KATEGORIJA);
        db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE_PITANJE);
        db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE_KVIZ);
        db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE_RANGLISTA);
        onCreate(db);
    }


    private String convertArrayToString(String[] array) {
        String str = "";
        for (int i = 0; i < array.length; i++) {
            str = str + array[i];
            // Do not append comma at the end of last element
            if (i < array.length - 1) {
                str = str + ",";
            }
        }
        return str;
    }

    private String[] convertStringToArray(String str) {
        return str.split(",");
    }


    public long dodajKategoriju(Kategorija kategorija, SQLiteDatabase db) {
        ContentValues noveVrijednosti = new ContentValues();
        noveVrijednosti.put(KATEGORIJA_NAZIV, kategorija.getNaziv());
        noveVrijednosti.put(KATEGORIJA_ID_IKONICE, kategorija.getId());

        long newRowId = db.insert(DATABASE_TABLE_KATEGORIJA, null, noveVrijednosti);
        if (newRowId == -1) return -1;

        return newRowId;
    }

    public ArrayList<Kategorija> dohvatiKategorije(SQLiteDatabase db) {
        Cursor todoCursor;
        ArrayList<Kategorija> kategorije = new ArrayList<>();

        todoCursor = db.rawQuery("SELECT * FROM " + DATABASE_TABLE_KATEGORIJA, null);

        int INDEX_ID = todoCursor.getColumnIndexOrThrow(KATEGORIJA_ID);
        int INDEX_NAZIV = todoCursor.getColumnIndexOrThrow(KATEGORIJA_NAZIV);
        int INDEX_ID_IKONICE = todoCursor.getColumnIndexOrThrow(KATEGORIJA_ID_IKONICE);

        while (todoCursor.moveToNext()) {
            Kategorija kategorija = new Kategorija();
            kategorija.setNaziv(todoCursor.getString(INDEX_NAZIV));
            kategorija.setId(Integer.toString(todoCursor.getInt(INDEX_ID_IKONICE)));
            kategorije.add(kategorija);
        }

        todoCursor.close();
        return kategorije;
    }


    public long dodajPitanje(Pitanje pitanje, SQLiteDatabase db) {
        String[] odgovoriStringArray = pitanje.getOdgovori().toArray(new String[0]);

        ContentValues noveVrijednosti = new ContentValues();
        noveVrijednosti.put(PITANJE_NAZIV, pitanje.getNaziv());
        noveVrijednosti.put(PITANJE_INDEX_TACNOG, pitanje.dajIndeksTacnog());
        noveVrijednosti.put(PITANJE_ODGOVORI, convertArrayToString(odgovoriStringArray));

        long newRowId = db.insert(DATABASE_TABLE_PITANJE, null, noveVrijednosti);
        if (newRowId == -1) return -1;

        return newRowId;
    }

    public ArrayList<Pitanje> dohvatiPitanja(SQLiteDatabase db) {
        Cursor todoCursor;
        ArrayList<Pitanje> pitanja = new ArrayList<>();

        todoCursor = db.rawQuery("SELECT * FROM " + DATABASE_TABLE_PITANJE, null);

        int INDEX_ID = todoCursor.getColumnIndexOrThrow(PITANJE_ID);
        int INDEX_NAZIV = todoCursor.getColumnIndexOrThrow(PITANJE_NAZIV);
        int INDEX_INDEX_TACNOG = todoCursor.getColumnIndexOrThrow(PITANJE_INDEX_TACNOG);
        int INDEX_ODGOVORI = todoCursor.getColumnIndexOrThrow(PITANJE_ODGOVORI);

        while (todoCursor.moveToNext()) {
            Pitanje p = new Pitanje();
            p.setNaziv(todoCursor.getString(INDEX_NAZIV));
            p.setTekstPitanja(todoCursor.getString(INDEX_NAZIV));
            p.getOdgovori().addAll((Arrays.asList(convertStringToArray(todoCursor.getString(INDEX_ODGOVORI)))));
            p.setTacan(p.getOdgovori().get(todoCursor.getInt(INDEX_INDEX_TACNOG)));
            pitanja.add(p);
        }

        todoCursor.close();
        return pitanja;
    }


    public long dodajKviz(Kviz kviz, SQLiteDatabase db) {
        String[] pitanjaStringArray = kviz.getNaziviPitanja().toArray(new String[0]);

        ContentValues noveVrijednosti = new ContentValues();
        noveVrijednosti.put(KVIZ_NAZIV, kviz.getNaziv());
        noveVrijednosti.put(KVIZ_ID_KATEGORIJE, kviz.getKategorija().getNaziv());
        noveVrijednosti.put(KVIZ_PITANJA, convertArrayToString(pitanjaStringArray));

        long newRowId = db.insert(DATABASE_TABLE_KVIZ, null, noveVrijednosti);
        if (newRowId == -1) return -1;

        return newRowId;
    }

    public ArrayList<Kviz> dohvatiKvizove(SQLiteDatabase db) {
        Cursor todoCursor;
        ArrayList<Kviz> kvizovi = new ArrayList<>();

        todoCursor = db.rawQuery("SELECT * FROM " + DATABASE_TABLE_KVIZ, null);

        int INDEX_ID = todoCursor.getColumnIndexOrThrow(KVIZ_ID);
        int INDEX_NAZIV = todoCursor.getColumnIndexOrThrow(KVIZ_NAZIV);
        int INDEX_ID_KATEGORIJE = todoCursor.getColumnIndexOrThrow(KVIZ_ID_KATEGORIJE);
        int INDEX_PITANJA = todoCursor.getColumnIndexOrThrow(KVIZ_PITANJA);

        ArrayList<Kategorija> sveKategorije = dohvatiKategorije(db);
        ArrayList<Pitanje> svaPitanja = dohvatiPitanja(db);

        while (todoCursor.moveToNext()) {
            Kviz kviz = new Kviz();
            kviz.setNaziv(todoCursor.getString(INDEX_NAZIV));

            for (Kategorija kat : sveKategorije) {
                if (todoCursor.getString(INDEX_ID_KATEGORIJE).equals(kat.getNaziv())) {
                    kviz.setKategorija(kat);
                }
            }

            ArrayList<String> naziviPitanjaKviza = new ArrayList<>((Arrays.asList(convertStringToArray(todoCursor.getString(INDEX_PITANJA)))));
            for (Pitanje p : svaPitanja) {
                for (String s : naziviPitanjaKviza) {
                    if (p.getNaziv().equals(s))
                        kviz.getPitanja().add(p);
                }
            }

            kvizovi.add(kviz);
        }

        todoCursor.close();
        return kvizovi;
    }


    public long dodajRanglistu(Ranglista ranglista, SQLiteDatabase db) {
        ContentValues noveVrijednosti = new ContentValues();
        noveVrijednosti.put(RANGLISTA_KVIZ_NAZIV, ranglista.getNazivKviza());
        noveVrijednosti.put(RANGLISTA_POZICIJA, ranglista.getPozicija());
        noveVrijednosti.put(RANGLISTA_IGRAC_NAZIV, ranglista.getNazivIgraca());
        noveVrijednosti.put(RANGLISTA_PROCENAT_TACNIH, ranglista.getProcenatTacnih());

        long newRowId = db.insert(DATABASE_TABLE_RANGLISTA, null, noveVrijednosti);
        if (newRowId == -1) return -1;

        return newRowId;
    }

    public ArrayList<Ranglista> dohvatiRangliste(SQLiteDatabase db) {
        Cursor todoCursor;
        ArrayList<Ranglista> rangliste = new ArrayList<>();

        todoCursor = db.rawQuery("SELECT * FROM " + DATABASE_TABLE_RANGLISTA, null);

        int INDEX_ID = todoCursor.getColumnIndexOrThrow(RANGLISTA_ID);
        int INDEX_KVIZ_NAZIV = todoCursor.getColumnIndexOrThrow(RANGLISTA_KVIZ_NAZIV);
        int INDEX_POZICIJA = todoCursor.getColumnIndexOrThrow(RANGLISTA_POZICIJA);
        int INDEX_IGRAC_NAZIV = todoCursor.getColumnIndexOrThrow(RANGLISTA_IGRAC_NAZIV);
        int INDEX_PROCENAT_TACNIH = todoCursor.getColumnIndexOrThrow(RANGLISTA_PROCENAT_TACNIH);

        while (todoCursor.moveToNext()) {
            Ranglista rl = new Ranglista();
            rl.setNazivKviza(todoCursor.getString(INDEX_KVIZ_NAZIV));
            rl.setPozicija(todoCursor.getString(INDEX_POZICIJA));
            rl.setNazivIgraca(todoCursor.getString(INDEX_IGRAC_NAZIV));
            rl.setProcenatTacnih(todoCursor.getString(INDEX_PROCENAT_TACNIH));
            rangliste.add(rl);
        }

        todoCursor.close();
        return rangliste;
    }


    public void obrisiSveIzTabela(SQLiteDatabase db){
        db.execSQL("DELETE FROM Kategorija");
        db.execSQL("DELETE FROM Pitanje");
        db.execSQL("DELETE FROM Kviz");
        db.execSQL("DELETE FROM Ranglista");
    }

    public void obrisiSvaPitanja(SQLiteDatabase db){
        db.execSQL("DELETE FROM Pitanje");
    }

    public void obrisiSveRangliste(SQLiteDatabase db){
        db.execSQL("DELETE FROM Ranglista");
    }

}
