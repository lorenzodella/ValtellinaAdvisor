package com.example.valtellinaadvisor.launcher;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.valtellinaadvisor.R;
import com.example.valtellinaadvisor.data.Ristorante;
import com.example.valtellinaadvisor.main.MainActivity;
import com.example.valtellinaadvisor.http.HttpGetRequest;
import com.example.valtellinaadvisor.http.HttpPostRequest;
import com.example.valtellinaadvisor.user.Utente;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

public class LoginActivity extends AppCompatActivity implements
        HttpPostRequest.OnUtenteReadyListener,
        HttpGetRequest.OnRistorantiReadyListener {

    EditText username, password;
    TextView registrati;
    Button accedi;
    ProgressBar progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        progress = findViewById(R.id.progress);
        progress.setVisibility(View.INVISIBLE);

        ImageView icon = findViewById(R.id.icon);
        icon.setClipToOutline(true);

        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        accedi = findViewById(R.id.accedi);
        accedi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String strUsername = username.getText().toString();
                String strPassword = password.getText().toString();
                checkUtente(strUsername, Utente.md5(strPassword));
            }
        });

        registrati = findViewById(R.id.registrati);
        registrati.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registrati.setTypeface(registrati.getTypeface(), Typeface.BOLD);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        registrati.setTypeface(Typeface.DEFAULT);
                    }
                }, 150);
                Intent i = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(i);
            }
        });
    }

    private void checkUtente(String strUsername, String strPassword) {
        accedi.setText("");
        progress.setVisibility(View.VISIBLE);
        HttpPostRequest checkUtente = new HttpPostRequest(this, false);
        checkUtente.setOnUtenteReadyListener(this);
        checkUtente.execute(
                "http://dellamateralorenzo.altervista.org/valtellina_advisor/getUtente.php",
                strUsername,
                strPassword
            );
    }

    public void onUtenteNotValid(){
        accedi.setText("Accedi");
        progress.setVisibility(View.INVISIBLE);
        Toast.makeText(getApplicationContext(), "username o password errati", Toast.LENGTH_SHORT).show();
    }

    public void onUtenteReady(int id, String strUsername, String strPassword, String nome, String cognome, String mail, String colore, int numRecensioni, double mediaVoti){
        Utente.setUtente(id, strUsername, strPassword, nome, cognome, mail, colore, numRecensioni, mediaVoti);
        try {
            OutputStreamWriter osw = new OutputStreamWriter(this.openFileOutput("user.txt", Context.MODE_PRIVATE));
            osw.write(id+";"+strUsername+";"+strPassword);
            osw.flush();
            osw.close();

            requestPreferiti();
        } catch (IOException e2) {
            e2.printStackTrace();
        }
    }

    private void requestPreferiti() {
        HttpGetRequest requestRistoranti = new HttpGetRequest(this, false);
        requestRistoranti.setOnRistorantiReadyListener(this);
        requestRistoranti.execute("http://dellamateralorenzo.altervista.org/valtellina_advisor/getPreferiti.php?idUtente="+Utente.getId());
    }

    @Override
    public void onRistorantiReady(ArrayList<Ristorante> elencoRistoranti, String strurl) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                accedi.setText("Accedi");
                progress.setVisibility(View.INVISIBLE);
                Toast.makeText(getApplicationContext(), "Benvenuto "+Utente.getUsername(), Toast.LENGTH_SHORT).show();
                Intent i = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(i);
                finish();
            }
        }, 1000);
    }
}