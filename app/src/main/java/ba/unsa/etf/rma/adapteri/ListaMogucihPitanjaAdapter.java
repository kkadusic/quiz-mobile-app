package ba.unsa.etf.rma.adapteri;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import ba.unsa.etf.rma.R;
import ba.unsa.etf.rma.klase.Pitanje;

public class ListaMogucihPitanjaAdapter extends BaseAdapter implements View.OnClickListener {
    private Activity activity;
    private ArrayList lista;
    private static LayoutInflater inflater = null;
    public Resources resources;
    Pitanje pitanje = null;
    int i = 0;

    public ListaMogucihPitanjaAdapter(Activity activity, ArrayList arrayList, Resources resources) {
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
        ListaKvizovaAdapter.ViewHolder holder;

        if (convertView == null) {
            vi = inflater.inflate(R.layout.element_liste, null);
            holder = new ListaKvizovaAdapter.ViewHolder();
            holder.slikaKviza = (ImageView) vi.findViewById(R.id.slikaKviza);
            holder.nazivKviza = (TextView) vi.findViewById(R.id.nazivKviza);
            vi.setTag(holder);
        } else {
            holder = (ListaKvizovaAdapter.ViewHolder) vi.getTag();
        }

        if (lista.size() <= 0) {
            holder.nazivKviza.setText("No Data");
        } else {
            pitanje = null;
            pitanje = (Pitanje) lista.get(position);
            holder.nazivKviza.setText(pitanje.getNaziv());
            holder.slikaKviza.setImageResource(R.drawable.plus);
        }
        return vi;
    }
}
