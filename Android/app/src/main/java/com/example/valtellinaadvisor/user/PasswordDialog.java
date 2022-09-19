package com.example.valtellinaadvisor.user;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.core.content.ContextCompat;

import com.example.valtellinaadvisor.R;
import com.example.valtellinaadvisor.http.HttpGetRequest;
import com.example.valtellinaadvisor.http.HttpPostRequest;

public class PasswordDialog extends AppCompatDialogFragment implements View.OnClickListener{
    private HttpPostRequest.OnPasswordChangedListener onPasswordChangedListener;
    private ProgressBar progressBar;
    private EditText passwordOld, passwordNew, passwordConf;
    private HttpPostRequest changePassword;
    private DialogInterface.OnDismissListener onDismissListener;

    public PasswordDialog(){
    }

    public void setOnPasswordChangedListener(HttpPostRequest.OnPasswordChangedListener onPasswordChangedListener) {
        this.onPasswordChangedListener = onPasswordChangedListener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.layout_password_dialog, null);

        builder.setView(view)
                .setNegativeButton("annulla", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dismiss();
                    }
                })
                .setPositiveButton("ok", null);

        progressBar = view.findViewById(R.id.progress);
        passwordOld = view.findViewById(R.id.password_old);
        passwordNew = view.findViewById(R.id.password_new);
        passwordConf = view.findViewById(R.id.password_conf);

        return builder.create();
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        if(changePassword != null)
            changePassword.cancel(true);
        if(onDismissListener != null)
            onDismissListener.onDismiss(dialog);
        super.onDismiss(dialog);
    }

    public void setOnDismissListener(DialogInterface.OnDismissListener onDismissListener) {
        this.onDismissListener = onDismissListener;
    }

    @Override
    public void onStart() {
        super.onStart();
        Button positive = ((AlertDialog) getDialog()).getButton(AlertDialog.BUTTON_POSITIVE);
        Button negative = ((AlertDialog) getDialog()).getButton(AlertDialog.BUTTON_NEGATIVE);
        positive.setBackgroundColor(Color.TRANSPARENT);
        positive.setTextColor(ContextCompat.getColor(getActivity(), R.color.green_darker));
        negative.setBackgroundColor(Color.TRANSPARENT);
        negative.setTextColor(ContextCompat.getColor(getActivity(), R.color.green_darker));

        positive.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        String strPasswordOld = passwordOld.getText().toString();
        String strPasswordNew = passwordNew.getText().toString();
        String strPasswordConf = passwordConf.getText().toString();

        if(strPasswordOld.trim().isEmpty()) {
            passwordOld.requestFocus();
            Toast.makeText(getActivity(), "Tutti i campi sono obbligatori", Toast.LENGTH_SHORT).show();
        }
        else if(strPasswordNew.trim().isEmpty()) {
            passwordNew.requestFocus();
            Toast.makeText(getActivity(), "Tutti i campi sono obbligatori", Toast.LENGTH_SHORT).show();
        }
        else if(strPasswordConf.trim().isEmpty()) {
            passwordConf.requestFocus();
            Toast.makeText(getActivity(), "Tutti i campi sono obbligatori", Toast.LENGTH_SHORT).show();
        }
        else if(!Utente.md5(strPasswordOld).equals(Utente.getPassword())){
            passwordOld.requestFocus();
            Toast.makeText(getActivity(), "Vecchia password errata", Toast.LENGTH_SHORT).show();
        }
        else if(strPasswordNew.length()<8) {
            passwordNew.requestFocus();
            Toast.makeText(getActivity(), "La password deve essere lunga almeno 8 caratteri", Toast.LENGTH_SHORT).show();
        }
        else if(strPasswordNew.equals(strPasswordOld)){
            passwordNew.requestFocus();
            Toast.makeText(getActivity(), "La nuova password deve essere diversa da quella vecchia", Toast.LENGTH_SHORT).show();
        }
        else if(!strPasswordNew.equals(strPasswordConf)){
            passwordConf.requestFocus();
            Toast.makeText(getActivity(), "Le password devono corrispondere", Toast.LENGTH_SHORT).show();
        }
        else {
            progressBar.setVisibility(View.VISIBLE);

            changePassword = new HttpPostRequest(getActivity(), false);
            changePassword.setOnPasswordChangedListener(onPasswordChangedListener);
            changePassword.execute("http://dellamateralorenzo.altervista.org/valtellina_advisor/changePassword.php?",
                    String.valueOf(Utente.getId()),
                    Utente.md5(strPasswordNew)
            );
        }

    }
}
