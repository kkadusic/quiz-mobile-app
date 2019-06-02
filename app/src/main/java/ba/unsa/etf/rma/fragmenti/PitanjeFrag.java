package ba.unsa.etf.rma.fragmenti;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
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

import static android.graphics.Color.BLUE;
import static android.graphics.Color.GREEN;
import static android.graphics.Color.RED;


public class PitanjeFrag extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private TextView tekstPitanja;
    private ListView odgovoriPitanja;

    private int BROJAC_PITANJA = 0;
    private int BROJAC_TACNIH = 0;

    ArrayList<String> odgovori = new ArrayList<String>();

    private ArrayAdapter<String> adapterOdgovori;

    private OnFragmentInteractionListener mListener;


    public PitanjeFrag() {
        // Required empty public constructor
    }


    public interface TextClicked{
        void sendText(String text);
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
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pitanje, container, false);

        odgovoriPitanja = view.findViewById(R.id.odgovoriPitanja);
        tekstPitanja = view.findViewById(R.id.tekstPitanja);

        Intent intent = getActivity().getIntent();
        final Kviz k = intent.getParcelableExtra("kvizIgraj");

        tekstPitanja.setText(k.getPitanja().get(0).getNaziv());

        adapterOdgovori = new ArrayAdapter<>(view.getContext(), android.R.layout.simple_list_item_1, odgovori);
        odgovoriPitanja.setAdapter(adapterOdgovori);


        for (int i = 0; i < k.getPitanja().get(BROJAC_PITANJA).getOdgovori().size(); i++){
            odgovori.add(k.getPitanja().get(BROJAC_PITANJA).getOdgovori().get(i));
        }

        odgovoriPitanja.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
                final Handler handler = new Handler();

                if (odgovori.get(position).equals(k.getPitanja().get(BROJAC_PITANJA).getTacan())) {
                    view.setBackgroundColor(GREEN);
                    BROJAC_TACNIH++;
                }
                else {
                    view.setBackgroundColor(RED);
                    // todo dodati da stavi zeleno na tacan
                }

                double procenat = ((double) BROJAC_TACNIH / (BROJAC_PITANJA + 1)) * 100;
                String procenatDvijeDecimale = String.format("%.2f", procenat);

                ArrayList<String> informacije = new ArrayList<>();
                informacije.add(Integer.toString(BROJAC_TACNIH));
                informacije.add(Integer.toString(k.getPitanja().size() - 1 - BROJAC_PITANJA));
                informacije.add(procenatDvijeDecimale + "%");

                FragmentTransaction transection=getFragmentManager().beginTransaction();
                InformacijeFrag mfragment = new InformacijeFrag();

                Bundle bundle = new Bundle();
                bundle.putStringArrayList("email", informacije);
                mfragment.setArguments(bundle); //data being send to SecondFragment
                transection.replace(R.id.informacijePlace, mfragment);
                transection.commit();

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        BROJAC_PITANJA++;

                        if (BROJAC_PITANJA != k.getPitanja().size()) {
                            view.setBackgroundColor(0);
                            odgovori.clear();


                            tekstPitanja.setText(k.getPitanja().get(BROJAC_PITANJA).getNaziv());
                            for (int i = 0; i < k.getPitanja().get(BROJAC_PITANJA).getOdgovori().size(); i++){
                                odgovori.add(k.getPitanja().get(BROJAC_PITANJA).getOdgovori().get(i));

                            }
                            adapterOdgovori.notifyDataSetChanged();
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


        /*
        FragmentTransaction transection=getFragmentManager().beginTransaction();
        InformacijeFrag mfragment = new InformacijeFrag();

        Bundle bundle = new Bundle();
        bundle.putString("email","ovoSamPoslao");
        mfragment.setArguments(bundle); //data being send to SecondFragment
        transection.replace(R.id.informacijePlace, mfragment);
        transection.commit();
        */


       // odgovori = k.getPitanja().get(0).getOdgovori();
        return view;
    }


    public View getViewByPosition(int pos, ListView listView) {
        final int firstListItemPosition = listView.getFirstVisiblePosition();
        final int lastListItemPosition = firstListItemPosition + listView.getChildCount() - 1;

        if (pos < firstListItemPosition || pos > lastListItemPosition ) {
            return listView.getAdapter().getView(pos, null, listView);
        } else {
            final int childIndex = pos - firstListItemPosition;
            return listView.getChildAt(childIndex);
        }
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
