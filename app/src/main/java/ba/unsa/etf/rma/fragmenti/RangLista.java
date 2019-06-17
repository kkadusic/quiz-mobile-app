package ba.unsa.etf.rma.fragmenti;

import android.content.Context;
import android.content.Intent;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import ba.unsa.etf.rma.R;
import ba.unsa.etf.rma.dto.Kviz;
import ba.unsa.etf.rma.dto.Ranglista;
import ba.unsa.etf.rma.firebase.DohvatiRangListu;
import ba.unsa.etf.rma.sqlite.BazaOpenHelper;


public class RangLista extends Fragment implements DohvatiRangListu.IDohvatiRangListeDone {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;
    private OnFragmentInteractionListener mListener;
    private Kviz kviz;

    private ListView lvPodaci;
    private ArrayList<String> podaci = new ArrayList<>();
    private ArrayList<Ranglista> rangliste = new ArrayList<>();
    private ArrayAdapter<String> adapterPodaci;

    private static SQLiteDatabase db = null;
    private static BazaOpenHelper bazaOpenHelper;

    public RangLista() {
    }

    public static RangLista newInstance(String param1, String param2) {
        RangLista fragment = new RangLista();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_rang_lista, container, false);

        lvPodaci = view.findViewById(R.id.lvPodaci);

        Intent intent = getActivity().getIntent();
        kviz = intent.getParcelableExtra("kvizIgraj");

        adapterPodaci = new ArrayAdapter<>(view.getContext(), android.R.layout.simple_list_item_1, podaci);
        lvPodaci.setAdapter(adapterPodaci);

        bazaOpenHelper = new BazaOpenHelper(getContext());
        try {
            db = bazaOpenHelper.getWritableDatabase();
        } catch (SQLException e) {
            db = bazaOpenHelper.getReadableDatabase();
        }


        if (isNetworkAvailable()) {
            System.out.println("RANGLISTE UCITAVANJE IZ FIREBASE");
            new DohvatiRangListu(RangLista.this, getResources()).execute();
        }
        else {
            podaci.clear();
            System.out.println("RANGLISTE UCITAVANJE IZ SQLITE");
            ArrayList<Ranglista> sveRangListe = new ArrayList<>(bazaOpenHelper.dohvatiRangliste(db));

            for (int i = 0; i < sveRangListe.size(); i++) {
                if (sveRangListe.get(i).getNazivKviza().equals(kviz.getNaziv())) {
                    rangliste.add(sveRangListe.get(i));
                }
            }

            Collections.sort(rangliste, new Comparator<Ranglista>() {
                @Override
                public int compare(Ranglista s1, Ranglista s2) {
                    String a = s1.getProcenatTacnih().replaceAll("[^0-9]", "");
                    String b = s2.getProcenatTacnih().replaceAll("[^0-9]", "");
                    return Double.valueOf(a).compareTo(Double.valueOf(b));
                }
            });

            for (int i = rangliste.size() - 1; i >= 0; i--) {
                podaci.add(rangliste.get(i).getPozicija() + ".       " + rangliste.get(i).getNazivIgraca() +
                        "        " + rangliste.get(i).getProcenatTacnih());
            }

            adapterPodaci.notifyDataSetChanged();
        }
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onDohvatiRanglisteDone(ArrayList<Ranglista> sveRangListe) {
        podaci.clear();

        for (int i = 0; i < sveRangListe.size(); i++) {
            if (sveRangListe.get(i).getNazivKviza().equals(kviz.getNaziv())) {
                rangliste.add(sveRangListe.get(i));
            }
        }

        Collections.sort(rangliste, new Comparator<Ranglista>() {
            @Override
            public int compare(Ranglista s1, Ranglista s2) {
                String a = s1.getProcenatTacnih().replaceAll("[^0-9]", "");
                String b = s2.getProcenatTacnih().replaceAll("[^0-9]", "");
                return Double.valueOf(a).compareTo(Double.valueOf(b));
            }
        });

        for (int i = rangliste.size() - 1; i >= 0; i--) {
            podaci.add(rangliste.get(i).getPozicija() + ".       " + rangliste.get(i).getNazivIgraca() +
                    "        " + rangliste.get(i).getProcenatTacnih());
        }

        adapterPodaci.notifyDataSetChanged();
    }


    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
