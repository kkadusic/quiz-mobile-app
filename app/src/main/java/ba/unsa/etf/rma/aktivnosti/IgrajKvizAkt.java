package ba.unsa.etf.rma.aktivnosti;

import android.content.Context;
import android.content.Intent;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

import ba.unsa.etf.rma.R;
import ba.unsa.etf.rma.dto.Ranglista;
import ba.unsa.etf.rma.fragmenti.InformacijeFrag;
import ba.unsa.etf.rma.fragmenti.PitanjeFrag;
import ba.unsa.etf.rma.fragmenti.RangLista;
import ba.unsa.etf.rma.klase.DohvatiRangListu;
import ba.unsa.etf.rma.klase.FBWrite;
import ba.unsa.etf.rma.dto.Kviz;

public class IgrajKvizAkt extends AppCompatActivity implements PitanjeFrag.OnFragmentInteractionListener,
        InformacijeFrag.OnFragmentInteractionListener, PitanjeFrag.OnCompleteListener, RangLista.OnFragmentInteractionListener,
        PitanjeFrag.OnZamijenaListener, DohvatiRangListu.IDohvatiRangListeDone {

    private Kviz kviz;
    private ArrayList<Ranglista> rangliste = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.igraj_kviz_akt);

        final Intent intent = getIntent();
        kviz = intent.getParcelableExtra("kviz");

        getIntent().putExtra("kvizIgraj", kviz);

        InformacijeFrag informacijeFrag = new InformacijeFrag();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        PitanjeFrag pitanjeFrag = new PitanjeFrag();
        FragmentTransaction transaction2 = getSupportFragmentManager().beginTransaction();

        transaction.replace(R.id.informacijePlace, informacijeFrag);
        transaction.addToBackStack(null);
        transaction.commit();

        transaction2.replace(R.id.pitanjePlace, pitanjeFrag);
        transaction2.addToBackStack(null);
        transaction2.commit();

        new DohvatiRangListu(IgrajKvizAkt.this, getResources()).execute();
    }


    public void zamijeniFragmente() {
        RangLista rangListaFragment = new RangLista();
        FragmentTransaction transaction2 = getSupportFragmentManager().beginTransaction();

        transaction2.replace(R.id.pitanjePlace, rangListaFragment);
        transaction2.addToBackStack(null);
        transaction2.commit();
    }


    @Override
    public void onFragmentInteraction(Uri uri) {

    }


    @Override
    public void onComplete(String procenatTacnih, String imeIgraca) {
        String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());

        int pozicija;
        if (rangliste.size() == 0)
            pozicija = 1;
        else {
            rangliste = sortirajRangListu(rangliste);
            pozicija = rangliste.size() + 1;
            for (int i = 0; i < rangliste.size(); i++) {
                if (Double.valueOf(procenatTacnih.replaceAll("[^0-9]", "")) >=
                        Double.valueOf(rangliste.get(i).getProcenatTacnih().replaceAll("[^0-9]", ""))) {
                    pozicija--;
                } else {
                    break;
                }
            }
        }

        // if (isNetworkAvailable()) {
            FBWrite fb = new FBWrite(getResources());
            String dokument = fb.dodajRangListu(imeIgraca, procenatTacnih, Integer.toString(pozicija), kviz.getNaziv());
            new FBWrite(getResources()).execute("Rangliste", kviz.getNaziv() + " " + currentDateTimeString, dokument);

           // bazaOpenHelper.dodajRanglistu(new Ranglista(imeIgraca, procenatTacnih, Integer.toString(pozicija), kviz.getNaziv()), db);
        // }
        // else {
           //  bazaOpenHelper.dodajRanglistu(new Ranglista(imeIgraca, procenatTacnih, Integer.toString(pozicija), kviz.getNaziv()), db);
        // }
    }


    @Override
    public void onCompleteZamjena() {
        RangLista rangListaFragment = new RangLista();
        FragmentTransaction transaction2 = getSupportFragmentManager().beginTransaction();

        transaction2.replace(R.id.pitanjePlace, rangListaFragment);
        transaction2.addToBackStack(null);
        transaction2.commit();
    }

    @Override
    public void onDohvatiRanglisteDone(ArrayList<Ranglista> sveRangListe) {
        for (int i = 0; i < sveRangListe.size(); i++) {
            if (sveRangListe.get(i).getNazivKviza().equals(kviz.getNaziv())) {
                rangliste.add(sveRangListe.get(i));
            }
        }
    }

    private ArrayList<Ranglista> sortirajRangListu(ArrayList<Ranglista> r) {
        ArrayList<Ranglista> ranglistas = r;

        Collections.sort(ranglistas, new Comparator<Ranglista>() {
            @Override
            public int compare(Ranglista s1, Ranglista s2) {
                String a = s1.getProcenatTacnih().replaceAll("[^0-9]", "");
                String b = s2.getProcenatTacnih().replaceAll("[^0-9]", "");
                return Double.valueOf(a).compareTo(Double.valueOf(b));
            }
        });

        return ranglistas;
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
