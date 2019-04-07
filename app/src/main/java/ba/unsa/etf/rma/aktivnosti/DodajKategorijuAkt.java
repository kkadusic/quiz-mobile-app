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

import com.maltaisn.icondialog.Icon;
import com.maltaisn.icondialog.IconDialog;

import ba.unsa.etf.rma.R;
import ba.unsa.etf.rma.klase.Kategorija;

public class DodajKategorijuAkt extends AppCompatActivity implements IconDialog.Callback {
    private Icon[] selectedIcons;
    private EditText etNaziv;
    private EditText etIkona;
    private Button btnDodajIkonu;
    private Button btnDodajKategoriju;


    @Override
    protected void onCreate(Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.dodaj_kategoriju_akt);

        final IconDialog iconDialog = new IconDialog();
        etNaziv = (EditText) findViewById(R.id.etNaziv);
        etIkona = (EditText) findViewById(R.id.etIkona);
        btnDodajIkonu = (Button) findViewById(R.id.btnDodajIkonu);
        btnDodajKategoriju = (Button) findViewById(R.id.btnDodajKategoriju);

        btnDodajIkonu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iconDialog.setSelectedIcons(selectedIcons);
                iconDialog.show(getSupportFragmentManager(), "icon_dialog");
                etIkona.setText(Integer.toString(iconDialog.getId()));
            }
        });


        btnDodajKategoriju.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!etNaziv.getText().toString().isEmpty() && !etIkona.getText().toString().isEmpty()) {
                    Kategorija kategorija = new Kategorija();
                    kategorija.setNaziv(etNaziv.getText().toString());
                    kategorija.setId(etIkona.getText().toString());

                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("kategorije", (Parcelable) new Kategorija(kategorija.getNaziv(), kategorija.getId()));
                    setResult(RESULT_OK, returnIntent);
                    finish();
                } else if (etNaziv.getText().toString().isEmpty() && !etIkona.getText().toString().isEmpty()) {
                    etNaziv.getBackground().setColorFilter(Color.parseColor("#6AFF0000"), PorterDuff.Mode.SRC_ATOP);
                    etIkona.getBackground().clearColorFilter();
                } else if (etIkona.getText().toString().isEmpty() && !etNaziv.getText().toString().isEmpty()) {
                    etIkona.getBackground().setColorFilter(Color.parseColor("#6AFF0000"), PorterDuff.Mode.SRC_ATOP);
                    etNaziv.getBackground().clearColorFilter();
                } else {
                    etNaziv.getBackground().setColorFilter(Color.parseColor("#6AFF0000"), PorterDuff.Mode.SRC_ATOP);
                    etIkona.getBackground().setColorFilter(Color.parseColor("#6AFF0000"), PorterDuff.Mode.SRC_ATOP);
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
}
