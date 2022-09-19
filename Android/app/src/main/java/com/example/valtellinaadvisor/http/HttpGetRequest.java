package com.example.valtellinaadvisor.http;

import android.content.DialogInterface;
import android.os.AsyncTask;

import androidx.fragment.app.FragmentActivity;

import com.example.valtellinaadvisor.data.Citta;
import com.example.valtellinaadvisor.data.ElencoCitta;
import com.example.valtellinaadvisor.data.ElencoRecensioni;
import com.example.valtellinaadvisor.data.ElencoRistoranti;
import com.example.valtellinaadvisor.data.Recensione;
import com.example.valtellinaadvisor.data.Ristorante;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

public class HttpGetRequest extends AsyncTask<String , Void ,String> {
    private FragmentActivity activity;
    private ProgressDialog dialog;
    private int timeout;
    private String strurl;
    private boolean showDialog;
    private OnRistorantiReadyListener onRistorantiReadyListener;
    private OnRecensioniReadyListener onRecensioniReadyListener;
    private OnCittaReadyListener onCittaReadyListener;
    private OnRecensioneDeletedListener onRecensioneDeletedListener;
    private static int DEFAULT_TIMEOUT = 6000;

    public HttpGetRequest(FragmentActivity activity, boolean showDialog){
        this.activity = activity;
        this.showDialog = showDialog;
        this.timeout = DEFAULT_TIMEOUT;
    }

    public void setDialog(ProgressDialog dialog) {
        this.dialog = dialog;
        if(dialog.isCancelable())
            dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    cancel(true);
                }
            });
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        if(showDialog) {
            dialog = new ProgressDialog(activity, true);
            dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    cancel(true);
                }
            });

            dialog.show(activity.getSupportFragmentManager(), "progress_dialog");
        }
    }

    @Override
    protected String doInBackground(String... strings){
        try {
            strurl = strings[0];
            URL url = new URL(strurl);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setConnectTimeout(timeout);
            urlConnection.setReadTimeout(timeout);
            int responseCode = urlConnection.getResponseCode();
            if(responseCode!=HttpURLConnection.HTTP_OK)
                return "error";

            InputStream response = urlConnection.getInputStream();
            Scanner scanner = new Scanner(response);
            String responseBody = scanner.useDelimiter("\\A").next();
            return responseBody;

        } catch (IOException e) {
            e.printStackTrace();
            return "exception";
        }
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        if(dialog != null && dialog.isShowing()){
            dialog.dismiss();
        }

        System.out.println(s);
        if(s!=null && !s.equals("error") && !s.equals("exception")){
            if(strurl.contains("deleteRecensione")) {
                if (onRecensioneDeletedListener != null) onRecensioneDeletedListener.onRecensioneDeleted();
            }
            else {
                try {
                    processJSON(new JSONArray(s));
                    //Toast.makeText(activity, s, Toast.LENGTH_LONG).show();
                } catch (JSONException e) {
                    //Toast.makeText(activity, "Parsing error", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        }
    }

    private void processJSON(JSONArray array) throws JSONException{
        if(strurl.contains("getRistoranti") && strurl.contains("idRistorante")){
            getSingleRestaurant(array.getJSONObject(0));
            return;
        }

        if(strurl.contains("getRistoranti") || strurl.contains("getPreferiti") || strurl.contains("getNearby"))
            ElencoRistoranti.clear();
        else if(strurl.contains("getRecensioni"))
            ElencoRecensioni.clear();

        for (int i = 0; i < array.length(); i++) {
            JSONObject jobj = array.getJSONObject(i);
            if(strurl.contains("getCitta")){
                Citta c = new Citta(
                        jobj.getInt("idCitta"),
                        jobj.getString("nome"),
                        jobj.getInt("CAP"),
                        jobj.getString("provincia"),
                        jobj.getString("regione")
                );
                ElencoCitta.putCitta(c);
            }
            else if(strurl.contains("getRistoranti") || strurl.contains("getPreferiti") || strurl.contains("getNearby")){
                if(jobj.isNull("rating"))
                    jobj.put("rating", 0.0);
                Ristorante r = new Ristorante(
                        jobj.getInt("idRistorante"),
                        jobj.getString("nome"),
                        jobj.getString("indirizzo"),
                        jobj.getInt("idCitta"),
                        jobj.getString("telefono"),
                        jobj.getDouble("LAT"),
                        jobj.getDouble("LNG"),
                        jobj.getString("categoria"),
                        jobj.getString("image"),
                        jobj.getDouble("rating"),
                        jobj.getString("isFavorite").equals("1")
                );
                ElencoRistoranti.putRistorante(r);
            }
            else if(strurl.contains("getRecensioni")){
                if(jobj.isNull("commento"))
                    jobj.put("commento", "");
                Recensione r = new Recensione(
                        jobj.getInt("idRecensione"),
                        jobj.getInt("idRistorante"),
                        jobj.getString("nomeRistorante"),
                        jobj.getString("data"),
                        jobj.getDouble("voto"),
                        jobj.getString("commento"),
                        jobj.getString("username"),
                        jobj.getString("colore")
                );
                ElencoRecensioni.putRecensione(r);
            }
        }
        if(strurl.contains("getRistoranti") || strurl.contains("getPreferiti") || strurl.contains("getNearby")){
            if (onRistorantiReadyListener != null) onRistorantiReadyListener.onRistorantiReady(ElencoRistoranti.getElencoRistoranti(), strurl);
        }
        else if(strurl.contains("getRecensioni")){
            if (onRecensioniReadyListener != null) onRecensioniReadyListener.onRecensioniReady(ElencoRecensioni.getElencoRecensioni());
        }
        else if(strurl.contains("getCitta")){
            if (onCittaReadyListener != null) onCittaReadyListener.onCittaReady(ElencoCitta.getElencoCitta());
        }

    }

    private void getSingleRestaurant(JSONObject jobj) throws JSONException {
        Ristorante r = new Ristorante(
                jobj.getInt("idRistorante"),
                jobj.getString("nome"),
                jobj.getString("indirizzo"),
                jobj.getInt("idCitta"),
                jobj.getString("telefono"),
                jobj.getDouble("LAT"),
                jobj.getDouble("LNG"),
                jobj.getString("categoria"),
                jobj.getString("image"),
                jobj.getDouble("rating"),
                jobj.getString("isFavorite").equals("1")
        );
        ArrayList<Ristorante> ristorante = new ArrayList<Ristorante>();
        ristorante.add(r);
        if (onRistorantiReadyListener != null) onRistorantiReadyListener.onRistorantiReady(ristorante, strurl);
    }

    public interface OnRistorantiReadyListener {
        void onRistorantiReady(ArrayList<Ristorante> elencoRistoranti, String strurl);
    }

    public void setOnRistorantiReadyListener(OnRistorantiReadyListener onRistorantiReadyListener){
        this.onRistorantiReadyListener = onRistorantiReadyListener;
    }

    public interface OnRecensioniReadyListener {
        void onRecensioniReady(ArrayList<Recensione> elencoRecensioni);
    }

    public void setOnRecensioniReadyListener(OnRecensioniReadyListener onRecensioniReadyListener){
        this.onRecensioniReadyListener = onRecensioniReadyListener;
    }

    public interface OnCittaReadyListener {
        void onCittaReady(ArrayList<Citta> elencoCitta);
    }

    public void setOnCittaReadyListener(OnCittaReadyListener onCittaReadyListener){
        this.onCittaReadyListener = onCittaReadyListener;
    }

    public interface OnRecensioneDeletedListener {
        void onRecensioneDeleted();
    }

    public void setOnRecensioneDeletedListener(OnRecensioneDeletedListener onRecensioneDeletedListener){
        this.onRecensioneDeletedListener = onRecensioneDeletedListener;
    }

}