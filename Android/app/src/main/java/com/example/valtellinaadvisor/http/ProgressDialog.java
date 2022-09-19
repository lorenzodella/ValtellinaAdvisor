package com.example.valtellinaadvisor.http;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.example.valtellinaadvisor.R;

public class ProgressDialog extends AppCompatDialogFragment {
    private Activity activity;
    private AlertDialog dialog;
    private DialogInterface.OnCancelListener onCancelListener;
    private boolean cancelable;
    private String text;

    public ProgressDialog(Activity activity, boolean cancelable){
        this.activity = activity;
        this.cancelable = cancelable;
    }

    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();

        View view = inflater.inflate(R.layout.layout_progress_dialog, null);

        builder.setView(view);
        builder.setCancelable(cancelable);

        dialog = builder.create();
        if(cancelable)
            dialog.setCanceledOnTouchOutside(true);
        Window dialogWindow = dialog.getWindow();
        dialogWindow.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        return dialog;
    }

    @Override
    public void onCancel(@NonNull DialogInterface dialog) {
        if(onCancelListener != null) onCancelListener.onCancel(dialog);
        super.onCancel(dialog);
    }

    public void setOnCancelListener(DialogInterface.OnCancelListener onCancelListener){
        this.onCancelListener = onCancelListener;
    }

    public boolean isShowing(){
        return dialog.isShowing();
    }

    public void setText(String text){
        this.text = text;
    }

    @Override
    public void onStart() {
        super.onStart();
        if(text != null) {
            TextView testo = dialog.findViewById(R.id.testo);
            testo.setText(text);
        }
    }
}
