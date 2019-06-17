package ba.unsa.etf.rma.klase;

import android.content.res.Resources;
import android.os.AsyncTask;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.common.collect.Lists;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import ba.unsa.etf.rma.R;

public class ObrisiKviz extends AsyncTask<String, Void, Void> {

    private Resources resources;

    public ObrisiKviz(Resources resources){
        this.resources = resources;
    }

    @Override
    protected Void doInBackground(String... strings) {
        GoogleCredential credentials;

        try {
            InputStream tajnaStream = resources.openRawResource(R.raw.secret);
            credentials = GoogleCredential.fromStream(tajnaStream).createScoped(Lists.newArrayList("https://www.googleapis.com/auth/datastore"));

            credentials.refreshToken();
            String TOKEN = credentials.getAccessToken();

            String url1 = "https://firestore.googleapis.com/v1/projects/rma19kadusickerim68/databases/(default)/documents/Kvizovi/" + strings[0] + "?access_token=";

            URL url = new URL(url1 + URLEncoder.encode(TOKEN, "UTF-8"));
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("DELETE");
            conn.getResponseCode();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
