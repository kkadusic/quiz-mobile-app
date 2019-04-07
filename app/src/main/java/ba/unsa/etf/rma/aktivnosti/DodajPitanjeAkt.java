package ba.unsa.etf.rma.aktivnosti;

import android.content.Intent;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;

import ba.unsa.etf.rma.R;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dodaj_pitanje_akt);

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
                odgovori.add(0, etOdgovor.getText().toString());
                adapterOdgovori.notifyDataSetChanged();
                etOdgovor.setText("");
            }
        });

        btnDodajTacan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                odgovori.add(0, etOdgovor.getText().toString());
                adapterOdgovori.notifyDataSetChanged();
                etOdgovor.setText("");
            }
        });

        /*
        btnDodajPitanje.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(DodajPitanjeAkt.this, DodajKvizAkt.class);
                Pitanje p = new Pitanje(etNaziv.getText().toString(), etNaziv.getText().toString(), odgovori, "tacan");
                myIntent.putExtra("novoPitanje", (Parcelable) p);
                startActivity(myIntent);
            }
        });
        */

        /*
        btnDodajPitanje.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(DodajPitanjeAkt.this, DodajKategorijuAkt.class);
                startActivity(myIntent);
            }
        });
        */

    }
}
