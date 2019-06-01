package ba.unsa.etf.rma.aktivnosti;

import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import ba.unsa.etf.rma.R;
import ba.unsa.etf.rma.adapteri.MyAdapter;
import ba.unsa.etf.rma.klase.FBWrite;
import ba.unsa.etf.rma.klase.Firebase;
import ba.unsa.etf.rma.klase.Kategorija;
import ba.unsa.etf.rma.klase.Kviz;
import ba.unsa.etf.rma.klase.Pitanje;

import static ba.unsa.etf.rma.aktivnosti.KvizoviAkt.AZURIRAJ_KVIZ;

public class DodajKvizAkt extends AppCompatActivity implements Firebase.IDohvatiPitanjaDone {

    static final int DODAJ_PITANJE = 200;
    static final int DODAJ_KATEGORIJU = 300;

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

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);



        new Firebase(DodajKvizAkt.this, DodajKvizAkt.this).execute("blabla");



        final Intent intent = getIntent();

        kvizovi = intent.getParcelableArrayListExtra("kvizovi");
        kategorije = intent.getParcelableArrayListExtra("kategorije");


        lvDodanaPitanja = findViewById(R.id.lvDodanaPitanja);
        ListView lvMogucaPitanja = findViewById(R.id.lvMogucaPitanja);
        spKategorije = findViewById(R.id.spKategorije);
        Button btnDodajKviz = findViewById(R.id.btnDodajKviz);
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
                        for (Pitanje p : trenutniKviz.getPitanja()){
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
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK) {
            if (data != null) {

                if (requestCode == DODAJ_PITANJE) {
                    Pitanje novoPitanje = data.getParcelableExtra("novoPitanje");
                    dodana.add(novoPitanje);
                    // trenutniKviz.setPitanja(dodana);
                    adapterDodana.notifyDataSetChanged();
                }

                if (requestCode == DODAJ_KATEGORIJU) {
                    Kategorija novaKategorija = data.getParcelableExtra("novaKategorija");
                    kategorije.add(kategorije.size() - 1, novaKategorija);
                    // trenutniKviz.setKategorija(novaKategorija);
                    sAdapter.notifyDataSetChanged();
                    spKategorije.setSelection(kategorije.size() - 2);
                }
            }
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            onBackPressed();
        return super.onOptionsItemSelected(item);
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
        for (Pitanje p : svaPitanja){
            if (!dodana.contains(p))
                moguca.add(p);
        }
        adapterMoguca.notifyDataSetChanged();
    }

}
