package ba.unsa.etf.rma.klase;

import android.content.res.Resources;
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
import ba.unsa.etf.rma.dto.Pitanje;

public class DohvatiPitanja extends AsyncTask<String, Void, Void> {

    private ArrayList<Pitanje> moguca = new ArrayList<>();
    private Resources resources;

    public interface IDohvatiPitanjaDone {
        void onDohvatiDone(ArrayList<Pitanje> lista);
    }

    private IDohvatiPitanjaDone poziv;

    public DohvatiPitanja(IDohvatiPitanjaDone p, Resources resources){
        poziv = p;
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


            String url1 = "https://firestore.googleapis.com/v1/projects/rma19kadusickerim68/databases/(default)/documents:runQuery?access_token=";
            String query = queryPitanja();

            URL url = new URL(url1 + URLEncoder.encode(TOKEN, "UTF-8"));
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST"); // GET, POST
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");

            try (OutputStream os = conn.getOutputStream()){
                byte[] input = query.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            int code = conn.getResponseCode(); // response kod
            InputStream in = conn.getInputStream();
            String rezultat = convertStreamToString(in);
            rezultat = "{ \"documents\": " + rezultat + "}";
            Log.d("TAG-sve", rezultat);

            parsirajPitanja(rezultat);

            Log.d("TOKEN", TOKEN);
        }
        catch(IOException e){
            e.printStackTrace();
        }
        return null;
    }


    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        if (poziv != null) {
            poziv.onDohvatiDone(moguca);
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



    public void parsirajPitanja(String rezultat){
        try {
            JSONObject obj = new JSONObject(rezultat);
            JSONArray documents = obj.getJSONArray("documents");

            for (int i = 0; i < documents.length(); i++) {
                JSONObject doc = documents.getJSONObject(i);

                JSONObject doc2 = new JSONObject(doc.getString("document"));

                JSONObject fields = new JSONObject(doc2.getString("fields"));

                JSONObject naziv = new JSONObject(fields.getString("naziv"));

                JSONObject indexTacnog = new JSONObject(fields.getString("indexTacnog"));
                int indeks = indexTacnog.getInt("integerValue");

                JSONObject odgovori = new JSONObject(fields.getString("odgovori"));
                JSONObject arrayValue = new JSONObject(odgovori.getString("arrayValue"));
                JSONArray values = arrayValue.getJSONArray("values");

                Pitanje pitanje = new Pitanje();

                for (int j = 0; j < values.length(); j++){
                    JSONObject odgovor = values.getJSONObject(j);
                    if (indeks == j)
                        pitanje.setTacan(odgovor.getString("stringValue"));
                    pitanje.getOdgovori().add(odgovor.getString("stringValue"));
                }

                pitanje.setNaziv(naziv.getString("stringValue"));
                pitanje.setTekstPitanja(naziv.getString("stringValue"));
                moguca.add(pitanje);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private String queryPitanja(){
        return "{" +
                "    \"structuredQuery\": {" +
                "        \"select\": { \"fields\": [ {\"fieldPath\": \"indexTacnog\"}, {\"fieldPath\": \"odgovori\"}, {\"fieldPath\": \"naziv\"}] }," +
                "        \"from\": [{\"collectionId\": \"Pitanja\"}]," +
                "       \"limit\": 1000 " +
                "    }" +
                "}";
    }

}
