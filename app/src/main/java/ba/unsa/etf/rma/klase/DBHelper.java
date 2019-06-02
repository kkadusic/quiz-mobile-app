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

public class DBHelper extends AsyncTask<String, Void, Void> {
    private String opcija = null; // write or read
    private Resources resources;
    private IDohvatiKvizoveDone pozivKvizovi;
    private IDohvatiPitanjaDone pozivPitanja;

    private ArrayList<Kviz> kvizovi = new ArrayList<>();
    private ArrayList<Pitanje> pitanja = new ArrayList<>();
    private ArrayList<Kategorija> kategorije = new ArrayList<>();
    private ArrayList<Pitanje> moguca = new ArrayList<>();

    public DBHelper(){
    }

    public DBHelper(IDohvatiKvizoveDone p, Resources resources, String opcija, String ok){ // Citanje kvizova
        this.pozivKvizovi = p;
        this.resources = resources;
        this.opcija = opcija;
    }


    public DBHelper(IDohvatiPitanjaDone p, Resources resources, String opcija){ // Citanje pitanja
        this.pozivPitanja = p;
        this.resources = resources;
        this.opcija = opcija;
    }


    public DBHelper(Resources resources, String opcija){ // Pisanje u bazu
        this.resources = resources;
        this.opcija = opcija;
    }



    public interface IDohvatiKvizoveDone {
        void onDohvatiDoneKvizovi(ArrayList<Kviz> kvizovi, ArrayList<Kategorija> kategorije);
    }

    public interface IDohvatiPitanjaDone {
        void onDohvatiDonePitanja(ArrayList<Pitanje> pitanja);
    }





    @Override
    protected Void doInBackground(String... strings) {
        GoogleCredential credentials;
        switch (opcija) {
            case "WRITE":
                try {
                    InputStream tajnaStream = resources.openRawResource(R.raw.secret);
                    credentials = GoogleCredential.fromStream(tajnaStream).createScoped(Lists.newArrayList("https://www.googleapis.com/auth/datastore"));

                    credentials.refreshToken();
                    String TOKEN = credentials.getAccessToken();

                    String url = "https://firestore.googleapis.com/v1/projects/rma19kadusickerim68/databases/(default)/documents/" + strings[0] + "/" + strings[1] + "?access_token=";

                    URL urlObj = new URL(url + URLEncoder.encode(TOKEN, "UTF-8"));
                    HttpURLConnection conn = (HttpURLConnection) urlObj.openConnection();
                    conn.setDoOutput(true);
                    conn.setRequestMethod("PATCH"); // GET, POST
                    conn.setRequestProperty("Content-Type", "application/json");
                    conn.setRequestProperty("Accept", "application/json");

                    String dokument = strings[2];

                    try (OutputStream os = conn.getOutputStream()) {
                        byte[] input = dokument.getBytes(StandardCharsets.UTF_8);
                        os.write(input, 0, input.length);
                    }

                    int code = conn.getResponseCode(); // response kod
                    InputStream odgovor = conn.getInputStream();

                    try (BufferedReader br = new BufferedReader(new InputStreamReader(odgovor, StandardCharsets.UTF_8))) {
                        StringBuilder response = new StringBuilder();
                        String responseLine = null;
                        while ((responseLine = br.readLine()) != null) {
                            response.append(responseLine.trim());
                        }
                        Log.d("ODGOVOR", response.toString());
                    }

                    Log.d("TOKEN", TOKEN);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case "READ-KVIZOVI":
                try {
                    Log.d("TAG----->", "USLO");
                    InputStream tajnaStream = resources.openRawResource(R.raw.secret);
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
                } catch (IOException e) {
                    e.printStackTrace();
                }


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


                    try (OutputStream os = conn.getOutputStream()) {
                        byte[] input = query.getBytes(StandardCharsets.UTF_8);
                        os.write(input, 0, input.length);
                    }
                    int code = conn.getResponseCode(); // response kod
                    InputStream in = conn.getInputStream();
                    String rezultat = convertStreamToString(in);
                    rezultat = "{ \"documents\": " + rezultat + "}";

                    parsirajPitanja(rezultat);
                    Log.d("TAG-pitanja", rezultat);
                } catch (IOException e) {
                    e.printStackTrace();
                }


                try {
                    InputStream tajnaStream = resources.openRawResource(R.raw.secret);
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


                    try (OutputStream os = conn.getOutputStream()) {
                        byte[] input = query.getBytes(StandardCharsets.UTF_8);
                        os.write(input, 0, input.length);
                    }
                    int code = conn.getResponseCode(); // response kod
                    InputStream in = conn.getInputStream();
                    String rezultat = convertStreamToString(in);
                    rezultat = "{ \"documents\": " + rezultat + "}";
                    Log.d("TAG-kvizovi", rezultat);


                    parsirajKvizove(rezultat);
                    Log.d("TOKEN", TOKEN);

                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case "READ-PITANJA":
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

                    try (OutputStream os = conn.getOutputStream()) {
                        byte[] input = query.getBytes(StandardCharsets.UTF_8);
                        os.write(input, 0, input.length);
                    }

                    int code = conn.getResponseCode(); // response kod
                    InputStream in = conn.getInputStream();
                    String rezultat = convertStreamToString(in);
                    rezultat = "{ \"documents\": " + rezultat + "}";
                    Log.d("TAG---PITANJA", rezultat);

                    parsirajPitanja(rezultat);

                    Log.d("TOKEN", TOKEN);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
        }

        return null;
    }



    @Override
    protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            pozivKvizovi.onDohvatiDoneKvizovi(kvizovi, kategorije);
    }




    private String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {

        } finally {
            try {
                is.close();
            } catch (IOException e) {
            }
        }
        return sb.toString();
    }


    public String napraviDokument(String... polja){
        StringBuilder dokument = new StringBuilder("{ \"fields\": " + "{ ");
        for (int i = 0; i < polja.length; i++){
            if (i != polja.length - 1)
                dokument.append(polja[i]).append(",");
            else
                dokument.append(polja[i]);
        }
        dokument.append("}" + "}");
        return dokument.toString();
    }


    public String napraviPolje(String naziv, String vrijednost){ // tip = stringValue
        String dokument = "\"" + naziv + "\": ";
        JSONObject json = new JSONObject();

        try {
            json.put("stringValue", vrijednost);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        dokument += json.toString();
        return dokument;
    }

    public String napraviPolje(String naziv, int vrijednost){ // tip = integerValue
        String dokument = "\"" + naziv + "\": ";
        JSONObject json = new JSONObject();

        try {
            json.put("integerValue", vrijednost);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        dokument += json.toString();
        return dokument;
    }


    public String napraviPolje(String nazivNiza, ArrayList<?> vrijednosti){ // tip = arrayValue
        String dokument = "\"" + nazivNiza + "\": {" + "\"" + "arrayValue" + "\": ";
        JSONArray jsonArray = new JSONArray();

        String tipElemenata;
        if (vrijednosti.get(0) instanceof String)
            tipElemenata = "stringValue";
        else
            tipElemenata = "integerValue";

        for (int i = 0; i < vrijednosti.size(); i++){
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put(tipElemenata, vrijednosti.get(i));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            jsonArray.put(jsonObject);
        }

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("values", jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        dokument += jsonObject.toString();
        dokument += "}";

        return dokument;
    }





    public void parsirajKvizove(String rezultat){
        try {
            JSONObject obj = new JSONObject(rezultat);
            JSONArray documents = obj.getJSONArray("documents");

            for (int i = 0; i < documents.length(); i++) {
                JSONObject doc = documents.getJSONObject(i);
                Log.d("TAG-DOC1", doc.toString());

                JSONObject doc2 = new JSONObject(doc.getString("document"));
                Log.d("TAG-DOC2", doc2.toString());

                JSONObject fields = new JSONObject(doc2.getString("fields"));
                Log.d("TAG-FIELDS", fields.toString());

                JSONObject naziv = new JSONObject(fields.getString("naziv"));
                Log.d("TAG-NAZIV", naziv.getString("stringValue"));

                JSONObject idKategorije = new JSONObject(fields.getString("idKategorije"));
                Log.d("TAG-IDKATEGORIJE", idKategorije.getString("stringValue"));

                JSONObject odgovori = new JSONObject(fields.getString("pitanja"));
                JSONObject arrayValue = new JSONObject(odgovori.getString("arrayValue"));
                JSONArray values = arrayValue.getJSONArray("values");
                Log.d("TAG-PITANJA", values.toString());

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
            for (Kviz k : kvizovi){
                Log.d("TAG-LISTAKVIZOVA", k.toString());
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
                // Log.d("TAG-DOC1", doc.toString());

                JSONObject doc2 = new JSONObject(doc.getString("document"));
                // Log.d("TAG-DOC2", doc2.toString());

                JSONObject fields = new JSONObject(doc2.getString("fields"));
                // Log.d("TAG-FIELDS", fields.toString());

                JSONObject naziv = new JSONObject(fields.getString("naziv"));
                // Log.d("TAG-NAZIV", naziv.getString("stringValue"));

                JSONObject indexTacnog = new JSONObject(fields.getString("indexTacnog"));
                // Log.d("TAG-INDEX", indexTacnog.getString("integerValue"));
                int indeks = indexTacnog.getInt("integerValue");

                JSONObject odgovori = new JSONObject(fields.getString("odgovori"));
                JSONObject arrayValue = new JSONObject(odgovori.getString("arrayValue"));
                JSONArray values = arrayValue.getJSONArray("values");
                // Log.d("TAG-ODGOVORI", values.toString());

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



    public void parsirajKategorije(String rezultat){
        try {
            JSONObject obj = new JSONObject(rezultat);
            JSONArray documents = obj.getJSONArray("documents");

            for (int i = 0; i < documents.length(); i++) {
                JSONObject doc = documents.getJSONObject(i);

                JSONObject doc2 = new JSONObject(doc.getString("document"));

                JSONObject fields = new JSONObject(doc2.getString("fields"));

                JSONObject naziv = new JSONObject(fields.getString("naziv"));
                Log.d("TAG-NAZIV", naziv.getString("stringValue"));

                JSONObject idIkonice = new JSONObject(fields.getString("idIkonice"));
                Log.d("TAG-INDEX", idIkonice.getString("integerValue"));
                int indeks = idIkonice.getInt("integerValue");

                Kategorija kategorija = new Kategorija();
                kategorija.setId(Integer.toString(indeks));
                kategorija.setNaziv(naziv.getString("stringValue"));
                kategorije.add(kategorija);
            }
            Log.d("TAG-SVE-KATEGORIJE", kategorije.toString());

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
