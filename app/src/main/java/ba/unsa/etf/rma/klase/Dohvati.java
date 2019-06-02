package ba.unsa.etf.rma.klase;

import android.os.AsyncTask;
import android.util.Log;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

import ba.unsa.etf.rma.dto.Pitanje;

public class Dohvati extends AsyncTask<String, Integer, Void> {
    ArrayList<Pitanje> moguca = new ArrayList<>();

    @Override
    protected Void doInBackground(String... strings) {
        GoogleCredential credentials;


        String query = null;
        try {
            query = URLEncoder.encode(strings[0], "utf-8");
        } catch (UnsupportedEncodingException exep) {
            exep.printStackTrace();
            return null;
        }

        String noviURL = "https://firestore.googleapis.com/v1/projects/rma19kadusickerim68/databases/(default)/documents/" + "probnaKolekcija" + "?access_token=";

        String rezultat = null;
        URL url = null;
        try {
            url = new URL(noviURL);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            rezultat = convertStreamToString(in);
        } catch (IOException e) {
            Log.d("DOHVATI-TAG", "Greska (uslo u catch)");
            e.printStackTrace();
            return null;
        }

        JSONArray items = null;
        JSONArray listAutors = null;

        try {
            JSONObject jo = new JSONObject(rezultat);
            items = jo.getJSONArray("items");
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
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
}
