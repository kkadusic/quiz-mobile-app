package ba.unsa.etf.rma.fragmenti;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

import ba.unsa.etf.rma.R;
import ba.unsa.etf.rma.aktivnosti.KvizoviAkt;
import ba.unsa.etf.rma.klase.Kviz;


public class InformacijeFrag extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private TextView infNazivKviza;
    private TextView infBrojTacnihPitanja;
    private TextView infBrojPreostalihPitanja;
    private TextView infProcenatTacni;
    private Button btnKraj;


    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public InformacijeFrag() {
        // Required empty public constructor
    }


    public static InformacijeFrag newInstance(String param1, String param2) {
        InformacijeFrag fragment = new InformacijeFrag();
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

        FragmentManager fm = getFragmentManager();
        PitanjeFrag f = (PitanjeFrag) fm.findFragmentByTag("pitanjeFrag");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
       View view = inflater.inflate(R.layout.fragment_informacije, container, false);

        infNazivKviza = view.findViewById(R.id.infNazivKviza);
        infBrojTacnihPitanja = view.findViewById(R.id.infBrojTacnihPitanja);
        infBrojPreostalihPitanja = view.findViewById(R.id.infBrojPreostalihPitanja);
        infProcenatTacni = view.findViewById(R.id.infProcenatTacni);
        btnKraj = view.findViewById(R.id.btnKraj);

        Intent i = getActivity().getIntent();
        Kviz k = i.getParcelableExtra("kvizIgraj");

        btnKraj.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), KvizoviAkt.class);
                startActivity(intent);
            }
        });

        infNazivKviza.setText(k.getNaziv());
        infBrojPreostalihPitanja.setText(Integer.toString(k.getPitanja().size()));
        infBrojTacnihPitanja.setText("0");
        infProcenatTacni.setText("100.00%");


        Bundle bundle = getArguments();
        if (bundle != null) {
            ArrayList<String> informacije = bundle.getStringArrayList("email");
            infBrojTacnihPitanja.setText(informacije.get(0));
            infBrojPreostalihPitanja.setText(informacije.get(1));
            infProcenatTacni.setText(informacije.get(2));
        }


        return view;
    }




    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    public void updateText(String text){
        infBrojPreostalihPitanja.setText(text);
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


    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
