package ba.unsa.etf.rma.klase;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import ba.unsa.etf.rma.aktivnosti.KvizoviAkt;

public class NetworkChangeReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(final Context context, final Intent intent) {
        /*
        int status = NetworkUtil.getConnectivityStatusString(context);

        if ("android.net.conn.CONNECTIVITY_CHANGE".equals(intent.getAction())) {
            if (status == NetworkUtil.NETWORK_STATUS_NOT_CONNECTED) {
                Log.d("TAG-net", "Nema Interneta");
            } else {
                Log.d("TAG-net", "Ima Interneta");
                new KvizoviAkt().proba();
            }
        }
        */
    }
}
