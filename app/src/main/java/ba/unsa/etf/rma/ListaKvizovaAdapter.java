package ba.unsa.etf.rma;

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

import ba.unsa.etf.rma.klase.Kviz;

public class ListaKvizovaAdapter extends BaseAdapter {
    private Activity activity;
    private ArrayList lista; //todo staviti ArrayList<Kviz>?
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
    public View getView(int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        ViewHolder holder;
        if (convertView == null) {
            vi = inflater.inflate(R.layout.element_liste, null);
            holder = new ViewHolder();
            holder.icon = (ImageView) vi.findViewById(R.id.icon);
            //holder.nazivMuzicara = (TextView) vi.findViewById(R.id.nazivMuzicara);
            //holder.zanr = (TextView) vi.findViewById(R.id.zanr);
            vi.setTag(holder);
        }
        else {
            holder = (ViewHolder) vi.getTag();
        }
        if (lista.size() <= 0) {
            //holder.nazivKviza.setText(R.string.no_info);
            //holder.zanr.setText(R.string.no_info);
        }
        else {
            kviz = null;
            kviz = (Kviz) lista.get(position);
            holder.nazivKviza.setText(kviz.getNaziv());
            //holder.icon.setImageResource(resources.getIdentifier
            //     ("ba.unsa.etf.rma:drawable/" + muzicar.getSlikaZanra(),
            //  null, null));
        }
        return vi;
    }

    public static class ViewHolder {
        public TextView nazivKviza;
        public ImageView icon;
    }

}
