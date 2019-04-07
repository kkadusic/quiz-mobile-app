package ba.unsa.etf.rma.aktivnosti;

import android.content.Intent;
import android.graphics.Color;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;

import ba.unsa.etf.rma.R;
import ba.unsa.etf.rma.klase.Kategorija;
import ba.unsa.etf.rma.klase.Kviz;
import ba.unsa.etf.rma.klase.Pitanje;

public class DodajPitanjeAkt extends AppCompatActivity {
    private ListView lvOdgovori;
    private EditText etNaziv;
    private EditText etOdgovor;
    private Button btnDodajOdgovor;
    private Button btnDodajTacan;
    private Button btnDodajPitanje;

    ArrayList<String> odgovori = new ArrayList<String>();
    private ArrayAdapter<String> adapterOdgovori;
    private Pitanje pitanje;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dodaj_pitanje_akt);

        Bundle bundle = getIntent().getExtras();
        pitanje = (Pitanje) getIntent().getParcelableExtra("nekoPitanje");

        lvOdgovori = (ListView) findViewById(R.id.lvOdgovori);
        etNaziv = (EditText) findViewById(R.id.etNaziv);
        etOdgovor = (EditText) findViewById(R.id.etOdgovor);
        btnDodajOdgovor = (Button) findViewById(R.id.btnDodajOdgovor);
        btnDodajTacan = (Button) findViewById(R.id.btnDodajTacan);
        btnDodajPitanje = (Button) findViewById(R.id.btnDodajPitanje);


        adapterOdgovori = new ArrayAdapter<String>(this, R.layout.element_odgovori, R.id.Itemname, odgovori);
        lvOdgovori.setAdapter(adapterOdgovori);

        btnDodajOdgovor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!odgovori.isEmpty()) {
                    odgovori.add(odgovori.size(), etOdgovor.getText().toString());
                }
                else {
                    odgovori.add(0, etOdgovor.getText().toString());
                }
                adapterOdgovori.notifyDataSetChanged();
                etOdgovor.setText("");
            }
        });

        btnDodajTacan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                odgovori.add(odgovori.size()-1, etOdgovor.getText().toString());
                adapterOdgovori.notifyDataSetChanged();
                etOdgovor.setText("");
            }
        });




        btnDodajPitanje.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Pitanje pitanje = new Pitanje();
                // naziv i tekst pitanja je ista stvar
                pitanje.setNaziv(etNaziv.getText().toString());
                pitanje.setTekstPitanja(etNaziv.getText().toString());
                pitanje.setOdgovori(odgovori);

                Intent returnIntent = new Intent();
                returnIntent.putExtra("nekoPitanje", (Parcelable) new Pitanje(pitanje.getNaziv(), pitanje.getTekstPitanja(), pitanje.getOdgovori(), ""));
                setResult(RESULT_OK, returnIntent);
                finish();
            }
        });


        lvOdgovori.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View view, int position, long arg) {
                odgovori.remove(position);
                adapterOdgovori.notifyDataSetChanged();
            }
        });

    }

    public void onListItemClick(ListView parent, View v, int position, long id){
        //Set background of all items to white
        for (int i=0;i<parent.getChildCount();i++){
            parent.getChildAt(i).setBackgroundColor(Color.WHITE);
        }
        v.setBackgroundColor(Color.CYAN);
    }
}
