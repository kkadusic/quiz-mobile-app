package ba.unsa.etf.rma.aktivnosti;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.OpenableColumns;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

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
    private Button btnImportKviz;

    private static final int MY_REQUEST_CODE = 1;
    private static final int MY_REQUEST_CODE2 = 2;
    private static final int READ_REQUEST_CODE = 42;

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
    private ArrayList<Kategorija> kategorije = new ArrayList<Kategorija>() {
        {
            add(new Kategorija());
        }
    };

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
        btnImportKviz = findViewById(R.id.btnImportKviz);

        adapterPitanja = new ListaPitanjaAdapter(this, pitanja, getResources());
        lvDodanaPitanja.setAdapter(adapterPitanja);

        adapterMogucaPitanja = new ListaMogucihPitanjaAdapter(this, mogucaPitanja, getResources());
        lvMogucaPitanja.setAdapter(adapterMogucaPitanja);


        etNaziv.setText(k.getNaziv());

        spKategorije.setSelection(adapterSpiner.getPosition(k.getKategorija()));


        Boolean ima = false;
        for (int i = 0; i < kategorije.size(); i++) {
            if (kategorije.get(i).getNaziv().equals("Nova kategorija")) ima = true;
        }
        if (!ima) kategorije.add(new Kategorija("Nova kategorija", "0"));


        spKategorije.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long id) {
                if (position == kategorije.size() - 1) {
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


        btnImportKviz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                performFileSearch();
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


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == MY_REQUEST_CODE && resultCode == RESULT_OK) {
            Bundle bundle = intent.getExtras();
            Pitanje pitanje = (Pitanje) bundle.getParcelable("nekoPitanje");

            pitanja.add(pitanja.size() - 1, pitanje);
            adapterPitanja.notifyDataSetChanged();
        } else if (requestCode == MY_REQUEST_CODE2 && resultCode == RESULT_OK) {
            Bundle bundle = intent.getExtras();
            Kategorija kategorija = (Kategorija) bundle.getParcelable("kategorije");

            kategorije.add(kategorije.size() - 1, kategorija);
            adapterSpiner.notifyDataSetChanged();
        }

        // za datoteku
        else if (requestCode == READ_REQUEST_CODE && resultCode == RESULT_OK) {
            Uri uri = null;
            if (intent != null) {
                uri = intent.getData();

                try {
                    readTextFromUri(uri);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }

        if (resultCode == RESULT_CANCELED) {

        }
    }


    public void performFileSearch() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("text/*");
        startActivityForResult(intent, READ_REQUEST_CODE);
    }


    private String readTextFromUri(Uri uri) throws IOException {
        InputStream inputStream = getContentResolver().openInputStream(uri);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder stringBuilder = new StringBuilder();

        String line;
        int ukupanBrojLinija = dajUkupanBrojLinijaDatoteke(uri); // eventualno premjestiti na pocetak
        boolean prvaLinija = true;
        ArrayList<String> naziviSvihPitanja = new ArrayList<>();


        while ((line = reader.readLine()) != null) {
            String[] values = line.split(",");
            int brojElemenataLinije = values.length;
            boolean imaIstaKategorija = false;

            if (prvaLinija) {
                String nazivKviza = values[0];
                String nazivKategorije = values[1];
                int brojPitanja = Integer.valueOf(values[2]);

                etNaziv.setText(nazivKviza);

                for (int i = 0; i < kategorije.size(); i++) {
                    if (kategorije.get(i).getNaziv().equals(nazivKategorije)) {
                        imaIstaKategorija = true;
                    }
                }

                if (!imaIstaKategorija) {
                    kategorije.add(kategorije.size() - 1, new Kategorija(nazivKategorije, "0"));
                    spKategorije.setSelection(kategorije.size() - 2);
                    adapterSpiner.notifyDataSetChanged();
                }

                if ((brojPitanja != ukupanBrojLinija - 1) || brojPitanja < 0) {
                    dajAlert("Kviz kojeg imporujete ima neispravan broj pitanja!");
                    resetujPolja();
                }

                prvaLinija = false;

            } else {
                String nazivPitanja = values[0];
                int brojOdgovora = Integer.valueOf(values[1]);
                String indeksTacnogOdgovora = values[2]; // prebaciti u int?
                ArrayList<String> odgovori = new ArrayList<>();

                if (brojOdgovora != brojElemenataLinije - 3) {
                    dajAlert("Kviz kojeg importujete ima neispravan broj odgovora!");
                    resetujPolja();
                }


                for (int i = 4; i < brojElemenataLinije; i++) {
                    if (values[i].contains(",")) {
                        dajAlert("Odgovor sadrži zarez");
                        resetujPolja();
                    }
                    else if (odgovori.contains(values[i])) {
                        dajAlert("Kviz kojeg importujete nije ispravan postoji ponavljanje odgovora!");
                        resetujPolja();
                    }
                    else
                        odgovori.add(values[i]);
                }


                if (!naziviSvihPitanja.contains(nazivPitanja)) {
                    pitanja.add(pitanja.size() - 1, new Pitanje(nazivPitanja, nazivPitanja, odgovori, indeksTacnogOdgovora));
                    naziviSvihPitanja.add(nazivPitanja);
                }
                else {
                    dajAlert("Kviz nije ispravan postoje dva pitanja sa istim nazivom!");
                    resetujPolja();
                }
            }

        }

        adapterPitanja.notifyDataSetChanged();
        //fileInputStream.close();
        //parcelFileDescriptor.close();
        return stringBuilder.toString();
    }


    public int dajUkupanBrojLinijaDatoteke(Uri uri) throws IOException {
        InputStream inputStream = getContentResolver().openInputStream(uri);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        int ukupanBrojLinija = 0;

        while ((line = reader.readLine()) != null) {
            ukupanBrojLinija++;
        }
        reader.close();
        return ukupanBrojLinija;
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

    public void resetujPolja(){
        etNaziv.setText("");
        pitanja.clear();
    }



















    /*
    public void readFile() throws IOException {
        FileInputStream fis;
        fis = openFileInput("test.txt");
        StringBuffer fileContent = new StringBuffer("");

        byte[] buffer = new byte[1024];


        while ((n = fis.read(buffer)) != -1)
        {
            fileContent.append(new String(buffer, 0, n));
        }
    }
    */


    public void citaj1() throws IOException {
        //File file = new File("sdcard/Download","dat.txt");
        File file = new File("/sdcard/Download/dat.txt");
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            int i = 0;

            while ((line = br.readLine()) != null) {
                if (i == 0) etNaziv.setText(line);
                i++;
            }
            br.close();
        } catch (IOException e) {
            //Handle error
        }
    }


    public void citaj2() {
        File sdcard = Environment.getExternalStorageDirectory();
        File file = new File(sdcard, "dat2.txt");
        StringBuilder text = new StringBuilder();
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            while ((line = br.readLine()) != null) {
                etNaziv.setText(line);
                text.append(line);
                text.append('\n');
            }
            br.close();
        } catch (IOException e) {
            //You'll need to add proper error handling here
        }
        etNaziv.setText(text.toString());
    }


    public void citaj3() {
        try {
            Context ctx = getApplicationContext();
            FileInputStream fileInputStream = ctx.openFileInput("dat2.txt");
            String fileData = readFromFileInputStream(fileInputStream);

            if (fileData.length() > 0) {
                etNaziv.setText(fileData);
                Toast.makeText(ctx, "Load saved data complete.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(ctx, "Not load any data.", Toast.LENGTH_SHORT).show();
            }
        } catch (FileNotFoundException ex) {
        }
    }


    private String readFromFileInputStream(FileInputStream fileInputStream) {
        StringBuffer retBuf = new StringBuffer();

        try {
            if (fileInputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                String lineData = bufferedReader.readLine();
                while (lineData != null) {
                    retBuf.append(lineData);
                    lineData = bufferedReader.readLine();
                }
            }
        } catch (IOException ex) {

        } finally {
            return retBuf.toString();
        }
    }



    /*
    private String readTextFromUri(Uri uri) throws IOException {
        InputStream inputStream = getContentResolver().openInputStream(uri);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder stringBuilder = new StringBuilder();
        String line;
        int brojacLinija = 0;


        if ((line = reader.readLine()) != null) {
            String[] values = line.split(",");
            etNaziv.setText(values[0]);

            for (int i = 0; i < Integer.parseInt(values[2]); i++) {
                pitanja.add(new Pitanje());
                adapterPitanja.notifyDataSetChanged();
            }

            for (String str : values) {
                etNaziv.setText(str);
            }

}


        while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
                String[] values = line.split(",");
                }





                //fileInputStream.close();
                //parcelFileDescriptor.close();
                return stringBuilder.toString();
                }
     */


}
