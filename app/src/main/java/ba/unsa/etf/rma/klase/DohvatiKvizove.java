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
import ba.unsa.etf.rma.dto.Kategorija;
import ba.unsa.etf.rma.dto.Kviz;
import ba.unsa.etf.rma.dto.Pitanje;

public class DohvatiKvizove extends AsyncTask<String, Void, Void> {
    private ArrayList<Kviz> kvizovi = new ArrayList<>();
    private ArrayList<Pitanje> pitanja = new ArrayList<>();
    private ArrayList<Kategorija> kategorije = new ArrayList<>();

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


            String url1 = "https://firestore.googleapis.com/v1/projects/rma19kadusickerim68/databases/(default)/documents:runQuery?access_token=";
            String query = queryKategorije();

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

            parsirajKategorije(rezultat);
        }
        catch(IOException e){
            e.printStackTrace();
        }


        try {

            InputStream tajnaStream = context.getResources().openRawResource(R.raw.secret);
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

            parsirajPitanja(rezultat);
        }
        catch(IOException e){
            e.printStackTrace();
        }


        try {
            InputStream tajnaStream = context.getResources().openRawResource(R.raw.secret);
            credentials = GoogleCredential.fromStream(tajnaStream).createScoped(Lists.newArrayList("https://www.googleapis.com/auth/datastore"));

            credentials.refreshToken();
            String TOKEN = credentials.getAccessToken();


            String url1 = "https://firestore.googleapis.com/v1/projects/rma19kadusickerim68/databases/(default)/documents:runQuery?access_token=";
            String query = queryKvizovi();

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

            parsirajKvizove(rezultat);

            Log.d("TOKEN", TOKEN);

        } catch(IOException e){
            e.printStackTrace();
        }

        return null;
    }


    public interface IDohvatiKvizoveDone {
        void onDohvatiDone(ArrayList<Kviz> lista, ArrayList<Kategorija> listaKategorije);
    }

    private IDohvatiKvizoveDone poziv;

    public DohvatiKvizove(IDohvatiKvizoveDone p, Context c){
        poziv = p;
        context = c;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        if (poziv != null) {
            poziv.onDohvatiDone(kvizovi, kategorije);
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


    public void parsirajKvizove(String rezultat){
        try {
            JSONObject obj = new JSONObject(rezultat);
            JSONArray documents = obj.getJSONArray("documents");

            for (int i = 0; i < documents.length(); i++) {
                JSONObject doc = documents.getJSONObject(i);

                JSONObject doc2 = new JSONObject(doc.getString("document"));

                JSONObject fields = new JSONObject(doc2.getString("fields"));

                JSONObject naziv = new JSONObject(fields.getString("naziv"));



                JSONObject idKategorije = new JSONObject(fields.getString("idKategorije"));

                JSONObject odgovori = new JSONObject(fields.getString("pitanja"));
                JSONObject arrayValue = new JSONObject(odgovori.getString("arrayValue"));
                JSONArray values = arrayValue.getJSONArray("values");

                Kviz kviz = new Kviz();
                kviz.setNaziv(naziv.getString("stringValue"));

                for (Kategorija k : kategorije){
                    if (k.getNaziv().equals(idKategorije.getString("stringValue"))) {
                        kviz.setKategorija(k);
                        break;
                    }
                }

                for (Pitanje p : pitanja){
                    for (int j=0; j<values.length(); j++){
                        JSONObject pitanje = values.getJSONObject(j);
                        if (p.getNaziv().equals(pitanje.getString("stringValue")))
                            kviz.getPitanja().add(p);
                    }
                }

                kvizovi.add(kviz);
            }

        } catch (JSONException e){
            e.printStackTrace();
        }
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
                pitanja.add(pitanje);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }



    public void parsirajKategorije(String rezultat){
        try {
            JSONObject obj = new JSONObject(rezultat);
            JSONArray documents = obj.getJSONArray("documents");

            for (int i = 0; i < documents.length(); i++) {
                JSONObject doc = documents.getJSONObject(i);

                JSONObject doc2 = new JSONObject(doc.getString("document"));

                JSONObject fields = new JSONObject(doc2.getString("fields"));

                JSONObject naziv = new JSONObject(fields.getString("naziv"));

                JSONObject idIkonice = new JSONObject(fields.getString("idIkonice"));
                int indeks = idIkonice.getInt("integerValue");

                Kategorija kategorija = new Kategorija();
                kategorija.setId(Integer.toString(indeks));
                kategorija.setNaziv(naziv.getString("stringValue"));
                kategorije.add(kategorija);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private String queryKvizovi(){
        return "{" +
                "    \"structuredQuery\": {" +
                "        \"select\": { \"fields\": [ {\"fieldPath\": \"idKategorije\"}, {\"fieldPath\": \"naziv\"}, {\"fieldPath\": \"pitanja\"}] }," +
                "        \"from\": [{\"collectionId\": \"Kvizovi\"}]," +
                "       \"limit\": 1000 " +
                "    }" +
                "}";
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

    private String queryKategorije(){
        return "{" +
                "    \"structuredQuery\": {" +
                "        \"select\": { \"fields\": [ {\"fieldPath\": \"idIkonice\"}, {\"fieldPath\": \"naziv\"}] }," +
                "        \"from\": [{\"collectionId\": \"Kategorije\"}]," +
                "       \"limit\": 1000 " +
                "    }" +
                "}";
    }

}
