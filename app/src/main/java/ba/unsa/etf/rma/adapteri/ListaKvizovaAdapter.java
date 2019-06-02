package ba.unsa.etf.rma.adapteri;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import ba.unsa.etf.rma.R;
import ba.unsa.etf.rma.dto.Kviz;

public class ListaKvizovaAdapter extends BaseAdapter implements View.OnClickListener, Filterable {
    private Activity activity;
    private ArrayList<Kviz> lista;
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

    @Override
    public Filter getFilter() {

        Filter filter = new Filter() {

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                lista = (ArrayList) results.values;
                notifyDataSetChanged();
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {

                FilterResults results = new FilterResults();
                ArrayList<String> FilteredArrayNames = new ArrayList<String>();

                // perform your search here using the searchConstraint String.

                constraint = constraint.toString().toLowerCase();
                for (int i = 0; i < lista.size(); i++) {
                    String dataNames = lista.get(i).getClass().getName();
                    if (dataNames.toLowerCase().startsWith(constraint.toString()))  {
                        FilteredArrayNames.add(dataNames);
                    }
                }

                results.count = FilteredArrayNames.size();
                results.values = FilteredArrayNames;

                return results;
            }
        };

        return filter;
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
            vi = inflater.inflate(R.layout.element_liste, null);
            holder = new ViewHolder();
            holder.slikaKviza = (ImageView) vi.findViewById(R.id.ikona);
            holder.nazivKviza = (TextView) vi.findViewById(R.id.naziv);
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
            } else {
                holder.nazivKviza.setText("Dodaj Kviz");
                holder.slikaKviza.setImageResource(R.drawable.plus);
            }
        }
        return vi;
    }

}
