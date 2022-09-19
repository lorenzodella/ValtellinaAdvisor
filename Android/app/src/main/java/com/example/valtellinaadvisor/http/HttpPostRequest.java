package com.example.valtellinaadvisor.http;

import android.os.AsyncTask;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Scanner;

public class HttpPostRequest extends AsyncTask<String , Void ,String> {
    private FragmentActivity activity;
    private ProgressDialog dialog;
    private int timeout;
    private String strurl;
    private double votoRecensione;
    private OnUtenteReadyListener onUtenteReadyListener;
    private OnRecensioneUploadedListener onRecensioneUploadedListener;
    private OnPreferitoSetListener onPreferitoSetListener;
    private OnSignUpListener onSignUpListener;
    private OnPasswordChangedListener onPasswordChangedListener;
    private ImageButton favoriteButton;
    private boolean showDialog;
    private static int DEFAULT_TIMEOUT = 6000;

    public HttpPostRequest(FragmentActivity activity, boolean showDialog){
        this.activity = activity;
        this.showDialog = showDialog;
        this.timeout = DEFAULT_TIMEOUT;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        if(showDialog) {
            dialog = new ProgressDialog(activity, false);
            dialog.setText("Salvataggio...");
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

            urlConnection.setDoOutput(true);

            String content = "";
            if(strurl.contains("getUtente")) {
                String username = strings[1];
                String password = strings[2];
                content = "username=" + URLEncoder.encode(username, "utf-8") +
                            "&password=" + URLEncoder.encode(password, "utf-8");
                urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                urlConnection.setRequestProperty("Content-Length", "" + content.getBytes().length);
            }
            else if(strurl.contains("uploadRecensione")) {
                String idRistorante = strings[1];
                String idUtente = strings[2];
                String data = strings[3];
                String voto = strings[4];
                String commento = strings[5];
                votoRecensione = Double.parseDouble(voto);
                content = "idRistorante=" + URLEncoder.encode(idRistorante, "utf-8") +
                            "&idUtente=" + URLEncoder.encode(idUtente, "utf-8") +
                            "&data=" + URLEncoder.encode(data, "utf-8") +
                            "&voto=" + URLEncoder.encode(voto, "utf-8") +
                            "&commento=" + URLEncoder.encode(commento, "utf-8");
                urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                urlConnection.setRequestProperty("Content-Length", "" + content.getBytes().length);
            }
            else if(strurl.contains("setPreferito")) {
                String idRistorante = strings[1];
                String idUtente = strings[2];
                content = "idRistorante=" + URLEncoder.encode(idRistorante, "utf-8") +
                            "&idUtente=" + URLEncoder.encode(idUtente, "utf-8");
                urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                urlConnection.setRequestProperty("Content-Length", "" + content.getBytes().length);
            }
            else if(strurl.contains("nuovoUtente")) {
                String nome = strings[1];
                String cognome = strings[2];
                String mail = strings[3];
                String username = strings[4];
                String password = strings[5];
                String colore = strings[6];
                content = "nome=" + URLEncoder.encode(nome, "utf-8") +
                        "&cognome=" + URLEncoder.encode(cognome, "utf-8") +
                        "&mail=" + URLEncoder.encode(mail, "utf-8") +
                        "&username=" + URLEncoder.encode(username, "utf-8") +
                        "&password=" + URLEncoder.encode(password, "utf-8") +
                        "&colore=" + URLEncoder.encode(colore, "utf-8");
                urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                urlConnection.setRequestProperty("Content-Length", "" + content.getBytes().length);
            }
            else if(strurl.contains("changePassword")){
                String idUtente = strings[1];
                String password = strings[2];
                content = "idUtente=" + URLEncoder.encode(idUtente, "utf-8") +
                        "&password=" + URLEncoder.encode(password, "utf-8");
                urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                urlConnection.setRequestProperty("Content-Length", "" + content.getBytes().length);
            }

            OutputStream output = urlConnection.getOutputStream();
            output.write(content.getBytes());
            output.close();

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

        if(s!=null && !s.equals("error") && !s.equals("exception") && !s.contains("Errore:")){
            if(strurl.contains("getUtente")) {
                try {
                    processJSON(new JSONObject(s));
                } catch (JSONException e) {
                    if (onUtenteReadyListener != null) onUtenteReadyListener.onUtenteNotValid();
                    e.printStackTrace();
                }
            }
            else if(strurl.contains("uploadRecensione")) {
                if (onRecensioneUploadedListener != null) onRecensioneUploadedListener.onRecensioneUploaded(votoRecensione);
            }
            else if(strurl.contains("setPreferito")) {
                if (onPreferitoSetListener != null) onPreferitoSetListener.onPreferitoSet(favoriteButton);
            }
            else if(strurl.contains("nuovoUtente")) {
                if (onSignUpListener != null) onSignUpListener.onSignUpValid();
            }
            else if(strurl.contains("changePassword")){
                if (onPasswordChangedListener != null) onPasswordChangedListener.onPasswordChanged(s);
            }
        }
        else {
            if(strurl.contains("getUtente")) {
                if (onUtenteReadyListener != null) onUtenteReadyListener.onUtenteNotValid();
            }
            else if(strurl.contains("uploadRecensione")) {
                if (onRecensioneUploadedListener != null) onRecensioneUploadedListener.onRecensioneNotValid();
            }
            else if(strurl.contains("nuovoUtente")) {
                if (onSignUpListener != null) onSignUpListener.onSignUpInvalid(s);
            }
        }

    }

    private void processJSON(JSONObject jobj) throws JSONException{
        if(jobj.isNull("mediaVoti"))
            jobj.put("mediaVoti", 0.0);
        int id = jobj.getInt("idUtente");
        String username = jobj.getString("username");
        String password = jobj.getString("password");
        String nome = jobj.getString("nome");
        String cognome = jobj.getString("cognome");
        String mail = jobj.getString("mail");
        String colore = jobj.getString("colore");
        int numRecensioni = jobj.getInt("numRecensioni");
        double mediaVoti = jobj.getDouble("mediaVoti");

        if (onUtenteReadyListener != null) onUtenteReadyListener.onUtenteReady(id, username, password, nome, cognome, mail, colore, numRecensioni, mediaVoti);
    }

    public interface OnUtenteReadyListener {
        void onUtenteReady(int id, String username, String password, String nome, String cognome, String mail, String colore, int numRecensioni, double mediaVoti);
        void onUtenteNotValid();
    }

    public void setOnUtenteReadyListener(OnUtenteReadyListener onUtenteReadyListener){
        this.onUtenteReadyListener = onUtenteReadyListener;
    }

    public interface OnRecensioneUploadedListener {
        void onRecensioneUploaded(double votoRecensione);
        void onRecensioneNotValid();
    }

    public void setOnRecensioneUploadedListener(OnRecensioneUploadedListener onRecensioneUploadedListener){
        this.onRecensioneUploadedListener = onRecensioneUploadedListener;
    }

    public interface OnPreferitoSetListener {
        void onPreferitoSet(ImageButton favoriteButton);
    }

    public void setOnPreferitoSetListener(OnPreferitoSetListener onPreferitoSetListener, ImageButton favoriteButton){
        this.onPreferitoSetListener = onPreferitoSetListener;
        this.favoriteButton = favoriteButton;
    }

    public interface OnSignUpListener {
        void onSignUpValid();
        void onSignUpInvalid(String error);
    }

    public void setOnSignUpListener(OnSignUpListener onSignUpListener){
        this.onSignUpListener = onSignUpListener;
    }

    public interface OnPasswordChangedListener {
        void onPasswordChanged(String password);
    }

    public void setOnPasswordChangedListener(OnPasswordChangedListener onPasswordChangedListener){
        this.onPasswordChangedListener = onPasswordChangedListener;
    }
}
