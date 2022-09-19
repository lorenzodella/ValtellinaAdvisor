package com.example.valtellinaadvisor.user;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.core.content.ContextCompat;

import com.example.valtellinaadvisor.http.HttpGetRequest;
import com.example.valtellinaadvisor.R;

public class DeleteDialog extends AppCompatDialogFragment {
    private HttpGetRequest.OnRecensioneDeletedListener onRecensioneDeletedListener;
    private int idRecensione;
    private ProgressBar progressBar;
    private HttpGetRequest deleteRecensione;
    private DialogInterface.OnDismissListener onDismissListener;

    public DeleteDialog(int idRecensione){
        this.idRecensione = idRecensione;
    }

    public void setOnRecensioneDeletedListener(HttpGetRequest.OnRecensioneDeletedListener onRecensioneDeletedListener) {
        this.onRecensioneDeletedListener = onRecensioneDeletedListener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.layout_delete_dialog, null);

        builder.setView(view)
                .setNegativeButton("annulla", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dismiss();
                    }
                })
                .setPositiveButton("ok", null);

        progressBar = view.findViewById(R.id.progress);

        return builder.create();
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        if(deleteRecensione != null)
            deleteRecensione.cancel(true);
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

        positive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                deleteRecensione = new HttpGetRequest(getActivity(), false);
                deleteRecensione.setOnRecensioneDeletedListener(onRecensioneDeletedListener);
                deleteRecensione.execute("http://dellamateralorenzo.altervista.org/valtellina_advisor/deleteRecensione.php?idRecensione="+idRecensione);
            }
        });
    }
}
