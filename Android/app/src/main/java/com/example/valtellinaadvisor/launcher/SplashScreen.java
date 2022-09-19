package com.example.valtellinaadvisor.launcher;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import com.example.valtellinaadvisor.R;
import com.example.valtellinaadvisor.data.Citta;
import com.example.valtellinaadvisor.data.Ristorante;
import com.example.valtellinaadvisor.main.MainActivity;
import com.example.valtellinaadvisor.http.HttpGetRequest;
import com.example.valtellinaadvisor.http.HttpPostRequest;
import com.example.valtellinaadvisor.user.Utente;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class SplashScreen extends AppCompatActivity implements
        HttpPostRequest.OnUtenteReadyListener,
        HttpGetRequest.OnCittaReadyListener,
        HttpGetRequest.OnRistorantiReadyListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        //deleteFile("user.txt");

        requestCitta();
    }

    private void requestCitta(){
        HttpGetRequest requestCitta = new HttpGetRequest(this, false);
        requestCitta.setOnCittaReadyListener(this);
        requestCitta.execute("http://dellamateralorenzo.altervista.org/valtellina_advisor/getCitta.php");
    }

    private boolean existUserFile(){
        String[] files = fileList();
        for (String name : files) {
            if(name.equals("user.txt")) {
                return true;
            }
        }
        return false;
    }

    private void checkUtente(String strUsername, String strPassword) {
        HttpPostRequest checkUtente = new HttpPostRequest(this, false);
        checkUtente.setOnUtenteReadyListener(this);
        checkUtente.execute(
                "http://dellamateralorenzo.altervista.org/valtellina_advisor/getUtente.php",
                strUsername,
                strPassword
            );
    }

    private void loadUtente() {
        try {
            InputStreamReader reader = new InputStreamReader(openFileInput("user.txt"));
            BufferedReader in = new BufferedReader(reader);
            String[] str = in.readLine().split(";");
            in.close();

            checkUtente(str[1], str[2]);

        } catch (IOException e){
            e.printStackTrace();
        }
    }

    private void requestPreferiti() {
        HttpGetRequest requestRistoranti = new HttpGetRequest(this, false);
        requestRistoranti.setOnRistorantiReadyListener(this);
        requestRistoranti.execute("http://dellamateralorenzo.altervista.org/valtellina_advisor/getPreferiti.php?idUtente="+ Utente.getId());
    }

    @Override
    public void onUtenteReady(int id, String username, String password, String nome, String cognome, String mail, String colore, int numRecensioni, double mediaVoti) {
        Utente.setUtente(id, username, password, nome, cognome, mail, colore, numRecensioni, mediaVoti);
        requestPreferiti();
    }

    @Override
    public void onUtenteNotValid() {
        deleteFile("user.txt");
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                login();
            }
        }, 1000);
    }

    @Override
    public void onCittaReady(ArrayList<Citta> elencoCitta) {
        if(existUserFile())
            loadUtente();
        else
            login();
    }

    private void login(){
        //Toast.makeText(getApplicationContext(), "Login non valido", Toast.LENGTH_SHORT).show();
        Intent i = new Intent(SplashScreen.this, LoginActivity.class);
        startActivity(i);
        finish();
    }

    @Override
    public void onRistorantiReady(ArrayList<Ristorante> elencoRistoranti, String strurl) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), "Benvenuto "+Utente.getUsername(), Toast.LENGTH_SHORT).show();
                Intent i = new Intent(SplashScreen.this, MainActivity.class);
                startActivity(i);
                finish();
            }
        }, 1000);
    }
}