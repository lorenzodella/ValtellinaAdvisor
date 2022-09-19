package com.example.valtellinaadvisor.main;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.core.content.ContextCompat;

import com.example.valtellinaadvisor.http.HttpGetRequest;
import com.example.valtellinaadvisor.R;
import com.example.valtellinaadvisor.data.Citta;
import com.example.valtellinaadvisor.data.ElencoCitta;
import com.example.valtellinaadvisor.user.Utente;

public class CittaDialog extends AppCompatDialogFragment {
    private Spinner cittaSpinner;
    private MySpinnerAdapter spinnerAdapter;
    private Citta[] elencoCitta;
    private HttpGetRequest.OnRistorantiReadyListener onRistorantiReadyListener;

    public CittaDialog(HttpGetRequest.OnRistorantiReadyListener onRistorantiReadyListener){
        this.onRistorantiReadyListener = onRistorantiReadyListener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.layout_citta_dialog, null);

        builder.setView(view)
                .setTitle("Seleziona la città")
                .setNegativeButton("annulla", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Toast.makeText(getActivity(), "Ecco i ristoranti di "+elencoCitta[cittaSpinner.getSelectedItemPosition()].getNome(), Toast.LENGTH_SHORT).show();
                        int idCitta = elencoCitta[cittaSpinner.getSelectedItemPosition()].getIdCitta();
                        HttpGetRequest requestRistoranti = new HttpGetRequest((AppCompatActivity)getActivity(), true);
                        requestRistoranti.setOnRistorantiReadyListener(onRistorantiReadyListener);
                        requestRistoranti.execute("http://dellamateralorenzo.altervista.org/valtellina_advisor/getRistoranti.php?idUtente="+ Utente.getId()+"&idCitta="+idCitta);
                    }
                });

        elencoCitta = ElencoCitta.getElencoCittaArray();
        cittaSpinner = view.findViewById(R.id.citta_spinner);
        spinnerAdapter = new MySpinnerAdapter(getActivity(), android.R.layout.simple_spinner_item, elencoCitta);
        cittaSpinner.setAdapter(spinnerAdapter);

        return builder.create();
    }

    @Override
    public void onStart() {
        super.onStart();
        Button positive = ((AlertDialog) getDialog()).getButton(AlertDialog.BUTTON_POSITIVE);
        Button negative = ((AlertDialog) getDialog()).getButton(AlertDialog.BUTTON_NEGATIVE);
        positive.setTextColor(ContextCompat.getColor(getActivity(), R.color.green_darker));
        negative.setBackgroundColor(Color.TRANSPARENT);
        negative.setTextColor(ContextCompat.getColor(getActivity(), R.color.green_darker));
    }
}
