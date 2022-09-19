package com.example.valtellinaadvisor.user;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.valtellinaadvisor.data.ElencoRistoranti;
import com.example.valtellinaadvisor.http.HttpGetRequest;
import com.example.valtellinaadvisor.http.HttpPostRequest;
import com.example.valtellinaadvisor.launcher.LoginActivity;
import com.example.valtellinaadvisor.R;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Locale;

import tyrantgit.explosionfield.ExplosionField;

public class FragmentDati extends Fragment {

    private Button exit, changePassword;
    private ImageView icon;
    private EditText username, nome, cognome, mail, numRecensioni, mediaVoti;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_dati, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        username = view.findViewById(R.id.username);
        username.setText(Utente.getUsername());
        nome = view.findViewById(R.id.nome);
        nome.setText(Utente.getNome());
        cognome = view.findViewById(R.id.cognome);
        cognome.setText(Utente.getCognome());
        mail = view.findViewById(R.id.mail);
        mail.setText(Utente.getMail());
        mediaVoti = getView().findViewById(R.id.mediaVoti);
        numRecensioni = getView().findViewById(R.id.numRecensioni);

        numRecensioni.setText(String.valueOf(Utente.getNumRecensioni()));
        mediaVoti.setText(String.format(Locale.US, "%.1f", Utente.getMediaVoti()));

        icon = view.findViewById(R.id.icon);
        icon.setColorFilter(Utente.getColore(), PorterDuff.Mode.MULTIPLY);

        exit = view.findViewById(R.id.exit);
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                esci(getActivity());
            }
        });

        changePassword = view.findViewById(R.id.changePassword);
        changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PasswordDialog passwordDialog = new PasswordDialog();
                passwordDialog.setOnPasswordChangedListener(new HttpPostRequest.OnPasswordChangedListener() {
                    @Override
                    public void onPasswordChanged(String password) {
                        passwordDialog.dismiss();
                        Utente.setPassword(password);
                        try {
                            OutputStreamWriter osw = new OutputStreamWriter(getActivity().openFileOutput("user.txt", Context.MODE_PRIVATE));
                            osw.write(Utente.getId()+";"+Utente.getUsername()+";"+Utente.getPassword());
                            osw.flush();
                            osw.close();
                            Toast.makeText(getContext(), "Password cambiata con successo", Toast.LENGTH_SHORT).show();
                        } catch (IOException e2) {
                            e2.printStackTrace();
                        }
                    }
                });
                passwordDialog.show(getActivity().getSupportFragmentManager(), "password_dialog");
            }
        });
    }

    @Override
    public void onResume() {
        numRecensioni.setText(String.valueOf(Utente.getNumRecensioni()));
        mediaVoti.setText(String.format(Locale.US, "%.1f", Utente.getMediaVoti()));
        super.onResume();
    }

    private void esci(Activity activity){
        new AlertDialog.Builder(activity)
                .setMessage("Sei sicuro di voler uscire?")
                .setNegativeButton("no", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton("si", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        activity.deleteFile("user.txt");
                        ElencoRistoranti.clear();
                        Intent intent = new Intent(activity, LoginActivity.class);
                        activity.finishAffinity();
                        startActivity(intent);
                    }
                }).show();
    }
}