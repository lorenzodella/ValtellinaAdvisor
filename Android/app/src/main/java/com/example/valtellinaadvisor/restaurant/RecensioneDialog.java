package com.example.valtellinaadvisor.restaurant;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.core.content.ContextCompat;

import com.example.valtellinaadvisor.R;
import com.example.valtellinaadvisor.http.HttpPostRequest;

import java.util.Calendar;

public class RecensioneDialog extends AppCompatDialogFragment {
    private HttpPostRequest.OnRecensioneUploadedListener onRecensioneUploadedListener;
    private int idRistorante;
    private int idUtente;
    private EditText commento;
    private EditText editDate;
    private RatingBar voto;

    public RecensioneDialog(HttpPostRequest.OnRecensioneUploadedListener onRecensioneUploadedListener, int idRistorante, int idUtente){
        this.onRecensioneUploadedListener = onRecensioneUploadedListener;
        this.idRistorante = idRistorante;
        this.idUtente = idUtente;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.layout_recensione_dialog, null);

        builder.setView(view)
                .setTitle("Nuova recensione")
                .setNegativeButton("annulla", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

        commento = view.findViewById(R.id.comment);
        voto = view.findViewById(R.id.voto);
        editDate = view.findViewById(R.id.date);
        editDate.setInputType(InputType.TYPE_NULL);
        editDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar cldr = Calendar.getInstance();
                int day = cldr.get(Calendar.DAY_OF_MONTH);
                int month = cldr.get(Calendar.MONTH);
                int year = cldr.get(Calendar.YEAR);
                // date picker dialog
                DatePickerDialog picker = new DatePickerDialog(getActivity(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                editDate.setText(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
                            }
                        }, year, month, day);
                picker.show();
            }
        });

        return builder.create();
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
                String data = editDate.getText().toString();
                String comment = commento.getText().toString();

                if(data.isEmpty()){
                    Toast.makeText(getActivity(), "Inserisci una data", Toast.LENGTH_SHORT).show();
                    return;
                }

                HttpPostRequest uploadRecensione = new HttpPostRequest((AppCompatActivity)getActivity(), true);
                uploadRecensione.setOnRecensioneUploadedListener(onRecensioneUploadedListener);
                uploadRecensione.execute(
                        "http://dellamateralorenzo.altervista.org/valtellina_advisor/uploadRecensione.php?",
                        String.valueOf(idRistorante),
                        String.valueOf(idUtente),
                        data,
                        String.valueOf(voto.getRating()),
                        comment
                );
                dismiss();
            }
        });
    }
}
