package ba.unsa.etf.rma.aktivnosti;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import ba.unsa.etf.rma.R;
import ba.unsa.etf.rma.fragmenti.InformacijeFrag;
import ba.unsa.etf.rma.fragmenti.PitanjeFrag;
import ba.unsa.etf.rma.klase.Kviz;

public class IgrajKvizAkt extends AppCompatActivity implements PitanjeFrag.OnFragmentInteractionListener, InformacijeFrag.OnFragmentInteractionListener {
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


    public void sendText(String text){
        InformacijeFrag frag = (InformacijeFrag) getSupportFragmentManager().findFragmentById(R.id.informacijePlace);
        frag.updateText(text);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

}
