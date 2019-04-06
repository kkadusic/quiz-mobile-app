package ba.unsa.etf.rma.aktivnosti;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;

import java.io.Serializable;
import java.util.ArrayList;

import ba.unsa.etf.rma.R;
import ba.unsa.etf.rma.adapteri.ListaPitanjaAdapter;
import ba.unsa.etf.rma.klase.Kviz;
import ba.unsa.etf.rma.klase.Pitanje;

public class DodajKvizAkt extends AppCompatActivity {

    private ListView lvDodanaPitanja;
    private ListView lvMogucaPitanja;
    private EditText etNaziv;
    private Spinner spKategorije;
    private Button btnDodajKviz;

    private ListaPitanjaAdapter adapter;
    private ArrayList<Pitanje> pitanja = new ArrayList<Pitanje>(){
        {
            add(new Pitanje());
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dodaj_kviz_akt);

        Kviz k = (Kviz) getIntent().getSerializableExtra("nekiKviz");

        lvDodanaPitanja = (ListView) findViewById(R.id.lvDodanaPitanja);
        lvMogucaPitanja = (ListView) findViewById(R.id.lvMogucaPitanja);
        spKategorije = (Spinner) findViewById(R.id.spKategorije);

        etNaziv = (EditText) findViewById(R.id.etNaziv);
        etNaziv.setText(k.getNaziv());
        btnDodajKviz = (Button) findViewById(R.id.btnDodajKviz);

        adapter = new ListaPitanjaAdapter(this, pitanja, getResources());
        lvDodanaPitanja.setAdapter(adapter);



        for (Pitanje p : k.getPitanja()){
            pitanja.add(pitanja.size()-1, p);
        }


        btnDodajKviz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Pitanje p = new Pitanje (etNaziv.getText().toString(), "", new ArrayList<String>(), "");
                pitanja.add(pitanja.size()-1, p);
                adapter.notifyDataSetChanged();
            }
        });

    }


}
