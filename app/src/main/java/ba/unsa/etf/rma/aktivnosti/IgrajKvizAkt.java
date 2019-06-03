package ba.unsa.etf.rma.aktivnosti;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.text.DateFormat;
import java.util.Date;

import ba.unsa.etf.rma.R;
import ba.unsa.etf.rma.fragmenti.InformacijeFrag;
import ba.unsa.etf.rma.fragmenti.PitanjeFrag;
import ba.unsa.etf.rma.fragmenti.RangLista;
import ba.unsa.etf.rma.klase.FBWrite;
import ba.unsa.etf.rma.dto.Kviz;

public class IgrajKvizAkt extends AppCompatActivity implements PitanjeFrag.OnFragmentInteractionListener,
        InformacijeFrag.OnFragmentInteractionListener, PitanjeFrag.OnCompleteListener, RangLista.OnFragmentInteractionListener,
        PitanjeFrag.OnZamijenaListener {

    private Kviz kviz;

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

        FBWrite fb = new FBWrite(IgrajKvizAkt.this);
        String dokument = fb.dodajRangListu(imeIgraca, procenatTacnih, "0", kviz.getNaziv());
        new FBWrite(IgrajKvizAkt.this).execute("Rangliste", kviz.getNaziv() + " " + currentDateTimeString, dokument);

    }


    @Override
    public void onCompleteZamjena() {
        RangLista rangListaFragment = new RangLista();
        FragmentTransaction transaction2 = getSupportFragmentManager().beginTransaction();

        transaction2.replace(R.id.pitanjePlace, rangListaFragment);
        transaction2.addToBackStack(null);
        transaction2.commit();
    }
}
