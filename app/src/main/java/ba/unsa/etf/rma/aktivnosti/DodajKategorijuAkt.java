package ba.unsa.etf.rma.aktivnosti;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.maltaisn.icondialog.Icon;
import com.maltaisn.icondialog.IconDialog;

import java.util.ArrayList;

import ba.unsa.etf.rma.R;
import ba.unsa.etf.rma.klase.Kategorija;

public class DodajKategorijuAkt extends AppCompatActivity implements IconDialog.Callback {

    private EditText etNaziv;
    private EditText etIkona;
    private Button btnDodajIkonu;
    private Button btnDodajKategoriju;

    private ArrayList<Kategorija> postojeceKategorije;
    private Icon[] selectedIcons;


    @Override
    protected void onCreate(Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.dodaj_kategoriju_akt);

        final IconDialog iconDialog = new IconDialog();
        etNaziv = (EditText) findViewById(R.id.etNaziv);
        etIkona = (EditText) findViewById(R.id.etIkona);
        btnDodajIkonu = (Button) findViewById(R.id.btnDodajIkonu);
        btnDodajKategoriju = (Button) findViewById(R.id.btnDodajKategoriju);
        etIkona.setEnabled(false);


        btnDodajIkonu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iconDialog.setSelectedIcons(selectedIcons);
                iconDialog.show(getSupportFragmentManager(), "icon_dialog");
                etIkona.setText(Integer.toString(iconDialog.getId())); //?
            }
        });


        btnDodajKategoriju.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validanUnos()) {
                    Kategorija novaKategorija = new Kategorija();
                    novaKategorija.setNaziv(etNaziv.getText().toString());
                    novaKategorija.setId(etIkona.getText().toString());

                    Intent intent = new Intent();
                    intent.putExtra("kategorije", (Parcelable) novaKategorija);
                    setResult(RESULT_OK, intent);
                    finish();
                }
                else if (etNaziv.getText().toString().isEmpty() && !etIkona.getText().toString().isEmpty()) {
                    etNaziv.getBackground().setColorFilter(Color.parseColor("#6AFF0000"), PorterDuff.Mode.SRC_ATOP);
                    etNaziv.setError("Nije unesen naziv kategorije");
                    etIkona.getBackground().clearColorFilter();
                } else if (etIkona.getText().toString().isEmpty() && !etNaziv.getText().toString().isEmpty()) {
                    etIkona.getBackground().setColorFilter(Color.parseColor("#6AFF0000"), PorterDuff.Mode.SRC_ATOP);
                    etIkona.setError("Nije izabrana ikona");
                    etNaziv.getBackground().clearColorFilter();
                } else {
                    etNaziv.getBackground().setColorFilter(Color.parseColor("#6AFF0000"), PorterDuff.Mode.SRC_ATOP);
                    etIkona.getBackground().setColorFilter(Color.parseColor("#6AFF0000"), PorterDuff.Mode.SRC_ATOP);
                    etNaziv.setError("Nije unesen naziv kategorije");
                    etIkona.setError("Nije izabrana ikona");
                }
            }
        });




    }

    @Override
    public void onIconDialogIconsSelected(Icon[] icons) {
        selectedIcons = icons;
        for (int i = 0; i < selectedIcons.length; i++) {
            etIkona.setText("" + selectedIcons[i].getId());
        }
    }

    public boolean postojiKategorija() {
        for (Kategorija k : postojeceKategorije) {
            if (k.getNaziv().equals(etNaziv.getText().toString())){
                return true;
            }
        }
        return false;
    }


    public boolean validanUnos() {
        return etIkona.getText().toString().length() != 0 && etNaziv.getText().toString().length() != 0;
    }
}
