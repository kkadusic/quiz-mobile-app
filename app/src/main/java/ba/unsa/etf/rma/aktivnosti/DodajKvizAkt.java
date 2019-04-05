package ba.unsa.etf.rma.aktivnosti;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;

import ba.unsa.etf.rma.R;

public class DodajKvizAkt extends AppCompatActivity {

    private ListView prvi;
    private ListView drugi;
    private EditText tekst;
    private Spinner spiner;
    private Button dugme;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dodaj_kviz_akt);
    }
}
