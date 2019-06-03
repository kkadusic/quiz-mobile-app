package ba.unsa.etf.rma.klase;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.common.collect.Lists;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import ba.unsa.etf.rma.R;
import ba.unsa.etf.rma.dto.Ranglista;

public class DohvatiRangListu extends AsyncTask<String, Void, Void> {

    private ArrayList<Ranglista> rangliste = new ArrayList<>();

    @SuppressLint("StaticFieldLeak")
    private Context context;

    public DohvatiRangListu(Context context){
        this.context = context;
    }

    @Override
    protected Void doInBackground(String... strings) {
        GoogleCredential credentials;

        try {
            InputStream tajnaStream = context.getResources().openRawResource(R.raw.secret);
            credentials = GoogleCredential.fromStream(tajnaStream).createScoped(Lists.newArrayList("https://www.googleapis.com/auth/datastore"));

            credentials.refreshToken();
            String TOKEN = credentials.getAccessToken();


            String url1 = "https://firestore.googleapis.com/v1/projects/rma19kadusickerim68/databases/(default)/documents:runQuery?access_token=";
            String query = queryRangliste();

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
            Log.d("TAG-RANGLISTE", rezultat);

            parsirajRangliste(rezultat);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }



    public interface IDohvatiRangListeDone {
        void onDohvatiRanglisteDone(ArrayList<Ranglista> rangliste);
    }

    private IDohvatiRangListeDone poziv;

    public DohvatiRangListu(IDohvatiRangListeDone p, Context c){
        poziv = p;
        context = c;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        if (poziv != null) {
            poziv.onDohvatiRanglisteDone(rangliste);
        }
    }


    private String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
        } catch (IOException ignored) {

        } finally {
            try {
                is.close();
            } catch (IOException ignored) {
            }
        }
        return sb.toString();
    }

    private void parsirajRangliste(String rezultat){
        try {
            JSONObject obj = new JSONObject(rezultat);
            JSONArray documents = obj.getJSONArray("documents");

            for (int i = 0; i < documents.length(); i++) {
                Ranglista ranglista = new Ranglista();

                JSONObject doc = documents.getJSONObject(i);

                JSONObject doc2 = new JSONObject(doc.getString("document"));

                JSONObject fields = new JSONObject(doc2.getString("fields"));

                JSONObject nazivKviza = new JSONObject(fields.getString("nazivKviza"));
                String NAZIV_KVIZA = nazivKviza.getString("stringValue");
                Log.d("NAZIV KVIZA", NAZIV_KVIZA);
                ranglista.setNazivKviza(NAZIV_KVIZA);

                JSONObject lista = new JSONObject(fields.getString("lista"));

                JSONObject mapvalue = new JSONObject(lista.getString("mapValue"));

                JSONObject fields2 = new JSONObject(mapvalue.getString("fields"));

                JSONObject pozicija = new JSONObject(fields2.getString("pozicija"));
                String POZICIJA = pozicija.getString("stringValue");
                Log.d("POZICIJA", POZICIJA);
                ranglista.setPozicija(POZICIJA);

                JSONObject igrac = new JSONObject(fields2.getString("igrac"));

                JSONObject mapValue2 = new JSONObject(igrac.getString("mapValue"));

                JSONObject fields3 = new JSONObject(mapValue2.getString("fields"));

                JSONObject procenatTacnih = new JSONObject(fields3.getString("procenatTacnih"));
                String PROCENAT_TACNIH = procenatTacnih.getString("stringValue");
                Log.d("PROCENAT TACNIH", PROCENAT_TACNIH);
                ranglista.setProcenatTacnih(PROCENAT_TACNIH);

                JSONObject nazivIgraca = new JSONObject(fields3.getString("nazivIgraca"));
                String NAZIV_IGRACA = nazivIgraca.getString("stringValue");
                Log.d("NAZIV IGRACA", NAZIV_IGRACA);
                ranglista.setNazivIgraca(NAZIV_IGRACA);

                rangliste.add(ranglista);
            }
            for (int i=0; i<rangliste.size(); i++){
                Log.d("TAG-RANGLISTE-SVE", rangliste.get(i).toString());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }





    private String queryRangliste(){
        return "{" +
                "    \"structuredQuery\": {" +
                "        \"select\": { \"fields\": [ {\"fieldPath\": \"lista\"}, {\"fieldPath\": \"nazivKviza\"}] }," +
                "        \"from\": [{\"collectionId\": \"Rangliste\"}]," +
                "       \"limit\": 1000 " +
                "    }" +
                "}";
    }
}
