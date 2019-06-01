package ba.unsa.etf.rma.fragmenti;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import ba.unsa.etf.rma.R;
import ba.unsa.etf.rma.klase.Kviz;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PitanjeFrag.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PitanjeFrag#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PitanjeFrag extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private TextView tekstPitanja;
    private ListView odgovoriPitanja;

    private int BROJAC_PITANJA = 1;

    ArrayList<String> odgovori = new ArrayList<String>();

    private ArrayAdapter<String> adapterOdgovori;

    private OnFragmentInteractionListener mListener;


    public PitanjeFrag() {
        // Required empty public constructor
    }


    public interface TextClicked{
        public void sendText(String text);
    }


    public static PitanjeFrag newInstance(String param1, String param2) {
        PitanjeFrag fragment = new PitanjeFrag();
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
        View view = inflater.inflate(R.layout.fragment_pitanje, container, false);

        odgovoriPitanja = view.findViewById(R.id.odgovoriPitanja);
        tekstPitanja = view.findViewById(R.id.tekstPitanja);

        Intent intent = getActivity().getIntent();
        final Kviz k = (Kviz) intent.getSerializableExtra("kviz");

        tekstPitanja.setText(k.getPitanja().get(0).getNaziv());


        /*
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                odgovori.add(k.getPitanja().get(0).getOdgovori().get(0));
            }
        }, 2000);
        */
        adapterOdgovori = new ArrayAdapter<String>(view.getContext(), android.R.layout.simple_list_item_1 , odgovori);
        odgovoriPitanja.setAdapter(adapterOdgovori);


        for (int i=0; i<k.getPitanja().get(0).getOdgovori().size(); i++){
            odgovori.add(k.getPitanja().get(0).getOdgovori().get(i));
        }

        odgovoriPitanja.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (BROJAC_PITANJA != k.getPitanja().size()) {
                            odgovori.clear();
                            tekstPitanja.setText(k.getPitanja().get(BROJAC_PITANJA).getNaziv());
                            for (int i=0; i<k.getPitanja().get(BROJAC_PITANJA).getOdgovori().size(); i++){
                                odgovori.add(k.getPitanja().get(BROJAC_PITANJA).getOdgovori().get(i));
                            }
                            adapterOdgovori.notifyDataSetChanged();
                            BROJAC_PITANJA++;
                        }
                        else {
                            tekstPitanja.setText("Kviz je završen!");
                            odgovori.clear();
                            adapterOdgovori.notifyDataSetChanged();
                        }
                    }
                }, 2000);
            }
        });



        FragmentTransaction transection=getFragmentManager().beginTransaction();
        InformacijeFrag mfragment=new InformacijeFrag();

        Bundle bundle=new Bundle();
        bundle.putString("email","ovoSamPoslao");
        mfragment.setArguments(bundle); //data being send to SecondFragment
        transection.replace(R.id.informacijePlace, mfragment);
        transection.commit();


       // odgovori = k.getPitanja().get(0).getOdgovori();
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

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
