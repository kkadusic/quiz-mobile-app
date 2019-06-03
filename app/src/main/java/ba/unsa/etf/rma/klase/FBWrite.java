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
import java.util.Map;

import ba.unsa.etf.rma.R;

public class FBWrite extends AsyncTask<String, Void, Void> {
    @SuppressLint("StaticFieldLeak")
    private Context context;

    public FBWrite(Context context) {
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

            String url = "https://firestore.googleapis.com/v1/projects/rma19kadusickerim68/databases/(default)/documents/" + strings[0] + "/" + strings[1] + "?access_token=";

            URL urlObj = new URL (url + URLEncoder.encode(TOKEN, "UTF-8"));
            HttpURLConnection conn = (HttpURLConnection) urlObj.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("PATCH"); // GET, POST
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");

            String dokument = strings[2];



            try (OutputStream os = conn.getOutputStream()){
                byte[] input = dokument.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            int code = conn.getResponseCode(); // response kod
            InputStream odgovor = conn.getInputStream();

            try (BufferedReader br = new BufferedReader(new InputStreamReader(odgovor, StandardCharsets.UTF_8))){
                StringBuilder response = new StringBuilder();
                String responseLine = null;
                while ((responseLine = br.readLine()) != null){
                    response.append(responseLine.trim());
                }
            }

            Log.d("TOKEN", TOKEN);
        }
        catch(IOException e){
            e.printStackTrace();
        }
        return null;
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


    public String dodajRangListu(String nazivIgraca, String procenatTacnih, String pozicija, String nazivKviza){
        String dokument = "{ \"fields\": {\n" +
                "        \"lista\": {\n" +
                "          \"mapValue\": {\n" +
                "            \"fields\": {\n" +
                "              \"igrac\": {\n" +
                "                \"mapValue\": {\n" +
                "                  \"fields\": {\n" +
                "                    \"nazivIgraca\": {\n" +
                "                      \"stringValue\": \"" + nazivIgraca + "\"\n" +
                "                    },\n" +
                "                    \"procenatTacnih\": {\n" +
                "                      \"stringValue\": \"" + procenatTacnih + "\"\n" +
                "                    }\n" +
                "                  }\n" +
                "                }\n" +
                "              },\n" +
                "              \"pozicija\": {\n" +
                "                \"stringValue\": \"" + pozicija + "\"\n" +
                "              }\n" +
                "            }\n" +
                "          }\n" +
                "        },\n" +
                "        \"" + "nazivKviza" + "\": {\n" +
                "          \"stringValue\": \"" + nazivKviza + "\"\n" +
                "        }\n" +
                "      }}";
        return dokument;
    }

}
