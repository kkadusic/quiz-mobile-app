package ba.unsa.etf.rma.aktivnosti;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
import ba.unsa.etf.rma.firebase.FirebaseWrite;
import ba.unsa.etf.rma.dto.Kategorija;

public class DodajKategorijuAkt extends AppCompatActivity implements IconDialog.Callback {

    private EditText etNaziv;
    private EditText etIkona;
    private Icon[] selectedIcons;
    private ArrayList<Kategorija> kategorije;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dodaj_kategoriju_akt);

        Button btnDodajKategoriju = findViewById(R.id.btnDodajKategoriju);
        Button btnDodajIkonu = findViewById(R.id.btnDodajIkonu);
        etNaziv = findViewById(R.id.etNaziv);
        etIkona = findViewById(R.id.etIkona);
        etIkona.setEnabled(false);

        Intent intent = getIntent();
        final IconDialog iconDialog = new IconDialog();
        kategorije = intent.getParcelableArrayListExtra("kategorije");

        btnDodajIkonu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iconDialog.setSelectedIcons(selectedIcons);
                iconDialog.show(getSupportFragmentManager(), "icon_dialog");
            }
        });

        btnDodajKategoriju.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isNetworkAvailable()) {
                    if (validanUnos()) {
                        if (!postojiKategorija()) {
                            Kategorija novaKategorija = new Kategorija(etNaziv.getText().toString(), etIkona.getText().toString());
                            Intent i = new Intent();
                            i.putExtra("novaKategorija", novaKategorija);
                            setResult(RESULT_OK, i);
                            finish();

                            FirebaseWrite fb = new FirebaseWrite(getResources());
                            String poljeNaziv = fb.napraviPolje("naziv", novaKategorija.getNaziv());
                            String poljeId = fb.napraviPolje("idIkonice", Integer.parseInt(novaKategorija.getId()));
                            String dokument = fb.napraviDokument(poljeNaziv, poljeId);
                            new FirebaseWrite(getResources()).execute("Kategorije", novaKategorija.getNaziv(), dokument);

                        } else {
                            Toast.makeText(DodajKategorijuAkt.this, "Unesena kategorija već postoji!", Toast.LENGTH_SHORT).show();
                            dajAlert("Unesena kategorija već postoji!");
                        }

                    } else {
                        if (etNaziv.getText().toString().length() == 0)
                            etNaziv.setError("Unesite naziv kategorije!");

                        if (etIkona.getText().toString().length() == 0)
                            etIkona.setError("Izaberite ikonu!");
                    }
                }
                else {
                    dajAlert("Uređaj nije konektovan na Internet!");
                }
            }
        });

    }

    private boolean validanUnos() {
        return etNaziv.getText().toString().length() != 0 && etIkona.getText().toString().length() != 0;
    }

    private boolean postojiKategorija() {
        for (Kategorija kategorija : kategorije)
            if (kategorija.getNaziv().equals(etNaziv.getText().toString()))
                return true;
        return false;
    }

    @Override
    public void onIconDialogIconsSelected(Icon[] icons) {
        selectedIcons = icons;
        etIkona.setText(String.valueOf(selectedIcons[0].getId()));
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
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