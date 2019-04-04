package ba.unsa.etf.rma.adapteri;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import ba.unsa.etf.rma.R;
import ba.unsa.etf.rma.klase.Kviz;

public class ListaKvizovaAdapter extends BaseAdapter implements View.OnClickListener {
    private Activity activity;
    private ArrayList lista;
    private static LayoutInflater inflater = null;
    public Resources resources;
    Kviz kviz = null;
    int i = 0;

    public ListaKvizovaAdapter(Activity activity, ArrayList arrayList, Resources resources) {
        this.activity = activity;
        this.lista = arrayList;
        this.resources = resources;
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        if (lista.size() <= 0) return 1;
        return lista.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public void onClick(View v) {
    }

    public static class ViewHolder {
        public ImageView slikaKviza;
        public TextView nazivKviza;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        ViewHolder holder;

        if (convertView == null) {
            vi = inflater.inflate(R.layout.el_liste_kviz, null);
            holder = new ViewHolder();
            holder.slikaKviza = (ImageView) vi.findViewById(R.id.slikaKviza);
            holder.nazivKviza = (TextView) vi.findViewById(R.id.nazivKviza);
            vi.setTag(holder);
        } else {
            holder = (ViewHolder) vi.getTag();
        }

        if (lista.size() <= 0) {
            holder.nazivKviza.setText("No Data");
        } else {
            kviz = null;
            kviz = (Kviz) lista.get(position);

            if (position != lista.size() - 1) {
                holder.nazivKviza.setText(kviz.getNaziv());
                holder.slikaKviza.setImageResource(R.drawable.blue_dot);
                //holder.slikaKviza.setImageResource(resources.getIdentifier("ba.unsa.etf.rma:drawable/blue_dot", null, null));
            } else {
                holder.nazivKviza.setText("Dodaj kviz");
                holder.slikaKviza.setImageResource(R.drawable.plus);
                //holder.slikaKviza.setImageResource(resources.getIdentifier("ba.unsa.etf.rma:drawable/plus", null, null));
            }
        }
        return vi;
    }

}
