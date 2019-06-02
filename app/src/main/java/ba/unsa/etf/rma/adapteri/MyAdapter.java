package ba.unsa.etf.rma.adapteri;
import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.maltaisn.icondialog.IconHelper;

import java.util.ArrayList;
import java.util.List;

import ba.unsa.etf.rma.R;
import ba.unsa.etf.rma.dto.Kviz;
import ba.unsa.etf.rma.dto.Pitanje;


public class MyAdapter extends ArrayAdapter<Object> {
    private Context context;
    private Resources res;
    private ArrayList<?> lista;

    @SuppressWarnings("unchecked")
    public MyAdapter(@NonNull Context context, ArrayList<?> lista, Resources res) {
        super(context, 0, (List<Object>) lista);
        this.context = context;
        this.lista = lista;
        this.res = res;
    }

    public void setLista(ArrayList<?> lista) {
        this.lista = lista;
    }

    @Override
    public int getCount() {
        return lista != null ? lista.size() : 0;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;

        if (listItem == null)
            listItem = LayoutInflater.from(context).inflate(R.layout.element_liste, parent, false);

        if (lista.get(position) instanceof Kviz) {
            final Kviz trenutniKviz = (Kviz) lista.get(position);
            final ImageView image = listItem.findViewById(R.id.ikona);
            final IconHelper iconHelper = IconHelper.getInstance(context);

            iconHelper.addLoadCallback(new IconHelper.LoadCallback() {
                @Override
                public void onDataLoaded() {
                    if (trenutniKviz.getKategorija().getId().equals("-1"))
                        image.setImageResource(res.getIdentifier("ba.unsa.etf.rma:drawable/blue_dot", null, null));
                    else
                        image.setImageDrawable(iconHelper.getIcon(Integer.valueOf(trenutniKviz.getKategorija().getId())).getDrawable(context));
                }
            });

            ((TextView) listItem.findViewById(R.id.naziv)).setText(trenutniKviz.getNaziv());
        } else if (lista.get(position) instanceof Pitanje) {
            Pitanje pitanje = (Pitanje) lista.get(position);

            ((ImageView) listItem.findViewById(R.id.ikona)).setImageResource(res.getIdentifier("ba.unsa.etf.rma:drawable/blue_dot", null, null));
            ((TextView) listItem.findViewById(R.id.naziv)).setText(pitanje.getNaziv());
        }

        return listItem;
    }

    public View getFooterView(ViewGroup parent, String text) {
        View footerView = LayoutInflater.from(context).inflate(R.layout.element_liste, parent, false);
        ((TextView) footerView.findViewById(R.id.naziv)).setText(text);
        return footerView;
    }

}