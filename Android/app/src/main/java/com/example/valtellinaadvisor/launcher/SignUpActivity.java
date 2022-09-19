package com.example.valtellinaadvisor.launcher;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.valtellinaadvisor.R;
import com.example.valtellinaadvisor.http.HttpPostRequest;
import com.example.valtellinaadvisor.user.Utente;
import com.skydoves.colorpickerview.ColorEnvelope;
import com.skydoves.colorpickerview.ColorPickerDialog;
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener;

public class SignUpActivity extends AppCompatActivity implements
        View.OnClickListener,
        HttpPostRequest.OnSignUpListener {

    EditText nome, cognome, mail, username, password, password_conf;
    String color = "#ffffff";
    Button registrati;
    ProgressBar progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        progress = findViewById(R.id.progress);
        progress.setVisibility(View.INVISIBLE);

        nome = findViewById(R.id.nome);
        nome.requestFocus();
        cognome = findViewById(R.id.cognome);
        mail = findViewById(R.id.mail);
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        password_conf = findViewById(R.id.password_conf);
        ImageView icon = findViewById(R.id.icon);

        icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ColorPickerDialog.Builder(SignUpActivity.this)
                        .setTitle("Choose your color")
                        .setPreferenceName("MyColorPickerDialog")
                        .setPositiveButton("selziona",
                                new ColorEnvelopeListener() {
                                    @Override
                                    public void onColorSelected(ColorEnvelope envelope, boolean fromUser) {
                                        color = "#"+envelope.getHexCode().substring(2);
                                        icon.setColorFilter(envelope.getColor(), PorterDuff.Mode.MULTIPLY);
                                    }
                                })
                        .setNegativeButton("annulla",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                    }
                                })
                        .attachAlphaSlideBar(false) // default is true. If false, do not show the AlphaSlideBar.
                        .attachBrightnessSlideBar(true)  // default is true. If false, do not show the BrightnessSlideBar.
                        .show();
            }
        });

        registrati = findViewById(R.id.registrati);
        registrati.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        String strNome = nome.getText().toString();
        String strCognome = cognome.getText().toString();
        String strMail = mail.getText().toString();
        String strUsername = username.getText().toString();
        String strPassword = password.getText().toString();
        String strPasswordConf = password_conf.getText().toString();

        if(strNome.trim().isEmpty()) {
            nome.requestFocus();
            Toast.makeText(getApplicationContext(), "Tutti i campi sono obbligatori", Toast.LENGTH_SHORT).show();
        }
        else if(strCognome.trim().isEmpty()) {
            cognome.requestFocus();
            Toast.makeText(getApplicationContext(), "Tutti i campi sono obbligatori", Toast.LENGTH_SHORT).show();
        }
        else if(strMail.trim().isEmpty()) {
            mail.requestFocus();
            Toast.makeText(getApplicationContext(), "Tutti i campi sono obbligatori", Toast.LENGTH_SHORT).show();
        }
        else if(strUsername.trim().isEmpty()) {
            username.requestFocus();
            Toast.makeText(getApplicationContext(), "Tutti i campi sono obbligatori", Toast.LENGTH_SHORT).show();
        }
        else if(strPassword.trim().isEmpty()) {
            password.requestFocus();
            Toast.makeText(getApplicationContext(), "Tutti i campi sono obbligatori", Toast.LENGTH_SHORT).show();
        }
        else if(strPassword.length()<8) {
            password.requestFocus();
            Toast.makeText(getApplicationContext(), "La password deve essere lunga almeno 8 caratteri", Toast.LENGTH_SHORT).show();
        }
        else if(!strPassword.equals(strPasswordConf)){
            password_conf.requestFocus();
            Toast.makeText(getApplicationContext(), "Le password devono corrispondere", Toast.LENGTH_SHORT).show();
        }
        else {
            registrati.setText("");
            progress.setVisibility(View.VISIBLE);

            HttpPostRequest nuovoUtente = new HttpPostRequest(this, false);
            nuovoUtente.setOnSignUpListener(this);
            nuovoUtente.execute(
                    "http://dellamateralorenzo.altervista.org/valtellina_advisor/nuovoUtente.php",
                    strNome,
                    strCognome,
                    strMail,
                    strUsername,
                    Utente.md5(strPassword),
                    color
            );
        }
    }

    @Override
    public void onSignUpValid() {
        registrati.setText("Registrati");
        progress.setVisibility(View.INVISIBLE);
        Toast.makeText(getApplicationContext(), "Registrazione avvenuta con successo", Toast.LENGTH_SHORT).show();
        Intent i = new Intent(SignUpActivity.this, LoginActivity.class);
        startActivity(i);
        finish();
    }

    @Override
    public void onSignUpInvalid(String error) {
        registrati.setText("Registrati");
        progress.setVisibility(View.INVISIBLE);
        Toast.makeText(getApplicationContext(), error, Toast.LENGTH_SHORT).show();
    }
}