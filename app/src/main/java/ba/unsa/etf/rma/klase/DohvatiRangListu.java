package ba.unsa.etf.rma.klase;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

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

public class DohvatiRangListu extends AsyncTask<String, Void, Void> {

    @SuppressLint("StaticFieldLeak")
    private Context context;

    @Override
    protected Void doInBackground(String... strings) {
        GoogleCredential credentials;

        try {

            InputStream tajnaStream = context.getResources().openRawResource(R.raw.secret);
            credentials = GoogleCredential.fromStream(tajnaStream).createScoped(Lists.newArrayList("https://www.googleapis.com/auth/datastore"));

            credentials.refreshToken();
            String TOKEN = credentials.getAccessToken();


            /*
            String url1 = "https://firestore.googleapis.com/v1/projects/rma19kadusickerim68/databases/(default)/documents:runQuery?access_token=";
            String query = queryKategorije();

            URL url = new URL(url1 + URLEncoder.encode(TOKEN, "UTF-8"));
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST"); // GET, POST
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");


            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = query.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }
            int code = conn.getResponseCode(); // response kod
            InputStream in = conn.getInputStream();
            String rezultat = convertStreamToString(in);
            rezultat = "{ \"documents\": " + rezultat + "}";

            parsirajKategorije(rezultat);
            Log.d("TAG-kategorije", rezultat);
            */
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
