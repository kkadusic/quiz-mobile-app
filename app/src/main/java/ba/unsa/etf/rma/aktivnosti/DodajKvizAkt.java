package ba.unsa.etf.rma.aktivnosti;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import ba.unsa.etf.rma.R;
import ba.unsa.etf.rma.adapteri.MyAdapter;
import ba.unsa.etf.rma.klase.FBWrite;
import ba.unsa.etf.rma.klase.DohvatiPitanja;
import ba.unsa.etf.rma.dto.Kategorija;
import ba.unsa.etf.rma.dto.Kviz;
import ba.unsa.etf.rma.dto.Pitanje;

import static ba.unsa.etf.rma.aktivnosti.KvizoviAkt.AZURIRAJ_KVIZ;

public class DodajKvizAkt extends AppCompatActivity implements DohvatiPitanja.IDohvatiPitanjaDone {

    static final int DODAJ_PITANJE = 200;
    static final int DODAJ_KATEGORIJU = 300;
    static final int IMPORTUJ_KVIZ = 103;

    private ListView lvDodanaPitanja;
    private View ldFooterView;
    private Spinner spKategorije;
    private EditText etNaziv;

    private ArrayList<Kviz> kvizovi = new ArrayList<>();
    private ArrayList<Kategorija> kategorije = new ArrayList<>();

    private ArrayList<Pitanje> dodana = new ArrayList<>();
    private ArrayList<Pitanje> moguca = new ArrayList<>();

    private MyAdapter adapterDodana;
    private ArrayAdapter<Pitanje> adapterMoguca;

    private Kviz trenutniKviz;
    private String staroImeKviza = null;

    private ArrayAdapter<Kategorija> sAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dodaj_kviz_akt);


        new DohvatiPitanja(DodajKvizAkt.this, DodajKvizAkt.this).execute("blabla");


        final Intent intent = getIntent();
        kvizovi = intent.getParcelableArrayListExtra("kvizovi");
        kategorije = intent.getParcelableArrayListExtra("kategorije");


        lvDodanaPitanja = findViewById(R.id.lvDodanaPitanja);
        ListView lvMogucaPitanja = findViewById(R.id.lvMogucaPitanja);
        spKategorije = findViewById(R.id.spKategorije);
        Button btnDodajKviz = findViewById(R.id.btnDodajKviz);
        Button btnImportKviz = findViewById(R.id.btnImportKviz);
        etNaziv = findViewById(R.id.etNaziv);

        trenutniKviz = intent.getParcelableExtra("kviz");

        if (trenutniKviz == null)
            trenutniKviz = new Kviz(null, null);

        sAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, kategorije);
        sAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        kategorije.add(new Kategorija("Dodaj kategoriju", "-2"));
        spKategorije.setAdapter(sAdapter);

        initialize();

        dodana = new ArrayList<>(trenutniKviz.getPitanja());
        adapterDodana = new MyAdapter(this, dodana, getResources());
        lvDodanaPitanja.setAdapter(adapterDodana);


        adapterMoguca = new ArrayAdapter<>(this, R.layout.element_liste, R.id.naziv, moguca);
        lvMogucaPitanja.setAdapter(adapterMoguca);

        if (intent.getIntExtra("requestCode", 0) == AZURIRAJ_KVIZ) {
            staroImeKviza = trenutniKviz.getNaziv();
            spKategorije.setSelection(sAdapter.getPosition(trenutniKviz.getKategorija()));
            etNaziv.setText(trenutniKviz.getNaziv());
        }

        btnDodajKviz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validanUnos()) {
                    if (!postojiKviz()) {
                        trenutniKviz.setNaziv(etNaziv.getText().toString());
                        trenutniKviz.setKategorija((Kategorija) spKategorije.getSelectedItem());
                        trenutniKviz.setPitanja(dodana);

                        Intent i = new Intent();
                        i.putExtra("kviz", trenutniKviz);
                        if (intent.getIntExtra("requestCode", 0) == AZURIRAJ_KVIZ)
                            i.putExtra("staroImeKviza", staroImeKviza);

                        i.putParcelableArrayListExtra("kategorije", kategorije);
                        setResult(RESULT_OK, i);
                        finish();

                        ArrayList<String> naziviPitanja = new ArrayList<>(); // id-evi Pitanja
                        for (Pitanje p : trenutniKviz.getPitanja()) {
                            naziviPitanja.add(p.getNaziv());
                        }


                        FBWrite fb = new FBWrite(DodajKvizAkt.this);
                        String nazivKviza = fb.napraviPolje("naziv", trenutniKviz.getNaziv());
                        String pitanjaKviza = fb.napraviPolje("pitanja", naziviPitanja);
                        String idKategorije = fb.napraviPolje("idKategorije", trenutniKviz.getKategorija().getNaziv());
                        String dokument = fb.napraviDokument(nazivKviza, pitanjaKviza, idKategorije);
                        new FBWrite(DodajKvizAkt.this).execute("Kvizovi", trenutniKviz.getNaziv(), dokument);

                    } else
                        Toast.makeText(DodajKvizAkt.this, "Kviz sa navedenim imenom već postoji!", Toast.LENGTH_SHORT).show();
                } else {
                    if (etNaziv.getText().length() == 0)
                        etNaziv.setError("Unesite naziv kviza!");

                    if (spKategorije.getSelectedItemPosition() == kategorije.size() - 1) {
                        TextView errorText = (TextView) spKategorije.getSelectedView();
                        errorText.setError("");
                        errorText.setTextColor(Color.RED);
                        // errorText.setText(getString(R.string.categoryError)); todo srediti
                    }
                }
            }
        });


        btnImportKviz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                performFileSearch();
            }
        });


        // Dodavanje novog pitanja
        ldFooterView.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                Intent intent = new Intent(DodajKvizAkt.this, DodajPitanjeAkt.class);
                                                intent.putParcelableArrayListExtra("dodana", dodana);
                                                intent.putParcelableArrayListExtra("moguca", moguca);
                                                startActivityForResult(intent, DODAJ_PITANJE);
                                            }
                                        }
        );

        lvDodanaPitanja.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                moguca.add(dodana.get(position));
                dodana.remove(dodana.get(position));
                adapterDodana.notifyDataSetChanged();
                adapterMoguca.notifyDataSetChanged();
            }
        });

        lvMogucaPitanja.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                dodana.add(moguca.get(position));
                moguca.remove(moguca.get(position));
                adapterDodana.notifyDataSetChanged();
                adapterMoguca.notifyDataSetChanged();
            }
        });


        // Dodavanje nove kategorije
        spKategorije.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == kategorije.size() - 1) {
                    Intent i = new Intent(DodajKvizAkt.this, DodajKategorijuAkt.class);
                    i.putParcelableArrayListExtra("kategorije", kategorije);
                    startActivityForResult(i, DODAJ_KATEGORIJU);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                spKategorije.setSelection(0);
            }
        });
    }

    private void initialize() {
        adapterDodana = new MyAdapter(this, dodana, getResources());

        lvDodanaPitanja.setAdapter(adapterDodana);
        lvDodanaPitanja.addFooterView(ldFooterView = adapterDodana.getFooterView(lvDodanaPitanja, "Dodaj pitanje"));
    }

    private boolean postojiKviz() {
        boolean changeMode = getIntent().getIntExtra("requestCode", 0) == AZURIRAJ_KVIZ;
        String nazivKviza = etNaziv.getText().toString();

        if (!changeMode || !nazivKviza.equalsIgnoreCase(staroImeKviza))
            for (Kviz k : kvizovi)
                if (k.getNaziv().equalsIgnoreCase(nazivKviza)) {
                    etNaziv.setError("Kviz sa istim imenom već postoji!");
                    return true;
                }

        return false;
    }

    private boolean validanUnos() {
        return (etNaziv.getText() != null && etNaziv.getText().length() != 0
                && spKategorije.getSelectedItemPosition() != kategorije.size() - 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent intent) {
        if (resultCode == RESULT_OK) {
            if (intent != null) {

                if (requestCode == DODAJ_PITANJE) {
                    Pitanje novoPitanje = intent.getParcelableExtra("novoPitanje");
                    dodana.add(novoPitanje);
                    // trenutniKviz.setPitanja(dodana);
                    adapterDodana.notifyDataSetChanged();
                }

                if (requestCode == DODAJ_KATEGORIJU) {
                    Kategorija novaKategorija = intent.getParcelableExtra("novaKategorija");
                    kategorije.add(kategorije.size() - 1, novaKategorija);
                    // trenutniKviz.setKategorija(novaKategorija);
                    sAdapter.notifyDataSetChanged();
                    spKategorije.setSelection(kategorije.size() - 2);
                }

                if (requestCode == IMPORTUJ_KVIZ) {
                    Uri uri = null;
                    uri = intent.getData();
                    try {
                        readTextFromUri(uri);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }
        }
    }


    @Override
    public void onBackPressed() {
        Intent i = new Intent();
        i.putParcelableArrayListExtra("kategorije", kategorije);
        setResult(RESULT_CANCELED, i);
        finish();
    }

    @Override
    public void onDohvatiDone(ArrayList<Pitanje> svaPitanja) {
        for (Pitanje p : svaPitanja) {
            if (!dodana.contains(p))
                moguca.add(p);
        }
        adapterMoguca.notifyDataSetChanged();
    }


    public void performFileSearch() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("text/*");
        startActivityForResult(intent, IMPORTUJ_KVIZ);
    }


    private String readTextFromUri(Uri uri) throws IOException {
        InputStream inputStream = getContentResolver().openInputStream(uri);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder stringBuilder = new StringBuilder();

        String line;
        int ukupanBrojLinija = dajUkupanBrojLinijaDatoteke(uri);
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

                for (int i = 0; i < kvizovi.size(); i++) {
                    if (kvizovi.get(i).getNaziv().equals(nazivKviza)) {
                        dajAlert("Kviz kojeg importujete već postoji!");
                    } else {
                        etNaziv.setText(nazivKviza);
                    }
                }


                for (int i = 0; i < kategorije.size(); i++) {
                    if (kategorije.get(i).getNaziv().equals(nazivKategorije)) {
                        imaIstaKategorija = true;
                    }
                }

                if (!imaIstaKategorija) {
                    Kategorija kategorija = new Kategorija(nazivKategorije, "920");
                    kategorije.add(kategorije.size() - 1, kategorija);
                    sAdapter.notifyDataSetChanged();
                    spKategorije.setSelection(spKategorije.getCount() - 2);

                    FBWrite fb = new FBWrite(DodajKvizAkt.this);
                    String poljeNaziv = fb.napraviPolje("naziv", kategorija.getNaziv());
                    String poljeId = fb.napraviPolje("idIkonice", Integer.parseInt(kategorija.getId()));
                    String dokument = fb.napraviDokument(poljeNaziv, poljeId);
                    new FBWrite(DodajKvizAkt.this).execute("Kategorije", kategorija.getNaziv(), dokument);
                } else {
                    for (int i = 0; i < kategorije.size(); i++) {
                        if (kategorije.get(i).getNaziv().equals(nazivKategorije)) {
                            spKategorije.setSelection(i);
                            break;
                        }
                    }
                }

                if ((brojPitanja != ukupanBrojLinija - 1) || brojPitanja < 0) {
                    dajAlert("Kviz kojeg imporujete ima neispravan broj pitanja!");
                    resetujPolja();
                }

                prvaLinija = false;

            } else {
                String nazivPitanja = values[0];
                int brojOdgovora = Integer.valueOf(values[1]);
                String indeksTacnogOdgovora = values[2];
                ArrayList<String> odgovori = new ArrayList<>();

                if (brojOdgovora != brojElemenataLinije - 3) {
                    dajAlert("Kviz kojeg importujete ima neispravan broj odgovora!");
                    resetujPolja();
                }


                for (int i = 3; i < brojElemenataLinije; i++) {
                    if (values[i].contains(",")) {
                        dajAlert("Odgovor sadrži zarez");
                        resetujPolja();
                    } else if (odgovori.contains(values[i])) {
                        dajAlert("Kviz kojeg importujete nije ispravan postoji ponavljanje odgovora!");
                        resetujPolja();
                    } else
                        odgovori.add(values[i]);
                }


                if (!naziviSvihPitanja.contains(nazivPitanja)) {
                    naziviSvihPitanja.add(nazivPitanja);

                    String tacanOdgovor = odgovori.get(Integer.parseInt(indeksTacnogOdgovora));

                    dodana.add(new Pitanje(nazivPitanja, nazivPitanja, odgovori, tacanOdgovor));
                    adapterDodana.notifyDataSetChanged();

                    FBWrite fb = new FBWrite(DodajKvizAkt.this);
                    String poljeNaziv = fb.napraviPolje("naziv", nazivPitanja);
                    String poljeOdgovori = fb.napraviPolje("odgovori", odgovori);
                    String poljeIndex = fb.napraviPolje("indexTacnog", Integer.parseInt(indeksTacnogOdgovora));
                    String dokument = fb.napraviDokument(poljeNaziv, poljeIndex, poljeOdgovori);
                    new FBWrite(DodajKvizAkt.this).execute("Pitanja", nazivPitanja, dokument);
                } else {
                    dajAlert("Kviz nije ispravan postoje dva pitanja sa istim nazivom!");
                    resetujPolja();
                }
            }

        }

        adapterDodana.notifyDataSetChanged();
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

    public void resetujPolja() {
        etNaziv.setText("");

        dodana.clear();
        adapterDodana.notifyDataSetChanged();

        spKategorije.setSelection(0);
        sAdapter.notifyDataSetChanged();
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


}
