package ba.unsa.etf.rma.fragmenti;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

import ba.unsa.etf.rma.R;
import ba.unsa.etf.rma.dto.Kviz;
import ba.unsa.etf.rma.dto.Ranglista;
import ba.unsa.etf.rma.klase.DohvatiRangListu;


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



        new DohvatiRangListu(RangLista.this, getContext()).execute();


        return view;
    }


    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
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
        rangliste.addAll(sveRangListe);

        for (int i = 0; i < rangliste.size(); i++){
            if (rangliste.get(i).getNazivKviza().equals(kviz.getNaziv())) {

                podaci.add(rangliste.get(i).getPozicija() + ".       " + rangliste.get(i).getNazivIgraca() +
                        "        " + rangliste.get(i).getProcenatTacnih());
            }
        }

        adapterPodaci.notifyDataSetChanged();
    }




    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
