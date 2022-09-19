package com.example.valtellinaadvisor.user;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.valtellinaadvisor.http.HttpGetRequest;
import com.example.valtellinaadvisor.R;
import com.example.valtellinaadvisor.restaurant.RecyclerViewAdapterRecensioni;
import com.example.valtellinaadvisor.restaurant.RestaurantActivity;
import com.example.valtellinaadvisor.data.ElencoRistoranti;
import com.example.valtellinaadvisor.data.Recensione;
import com.example.valtellinaadvisor.data.Ristorante;

import java.io.Serializable;
import java.util.ArrayList;

import tyrantgit.explosionfield.ExplosionField;

public class FragmentRecensioni extends Fragment implements
        HttpGetRequest.OnRecensioniReadyListener,
        HttpGetRequest.OnRistorantiReadyListener,
        RecyclerViewAdapterRecensioni.GoToListener,
        RecyclerViewAdapterRecensioni.DeleteListener{

    RecyclerView recyclerView;
    RecyclerViewAdapterRecensioni adapter;
    ArrayList<Recensione> elencoRecensioni;
    TextView no_recensioni;
    ProgressBar progress;
    boolean refresh_flag = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_recensioni, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        progress = view.findViewById(R.id.progress);
        no_recensioni = view.findViewById(R.id.no_recensioni);
        no_recensioni.setVisibility(View.INVISIBLE);

        elencoRecensioni = new ArrayList<Recensione>();
        recyclerView = view.findViewById(R.id.recyclerView_recensioni);
        setRecyclerView();
    }

    private void setRecyclerView() {
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        adapter = new RecyclerViewAdapterRecensioni(elencoRecensioni, recyclerView);
        adapter.setNomeRistoranteVisible(true);
        adapter.setGoToListener(this);
        adapter.setDeleteListener(this);
        recyclerView.setAdapter(adapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
    }

    @Override
    public void onRecensioniReady(ArrayList<Recensione> elencoRecensioni) {
        this.elencoRecensioni = elencoRecensioni;
        adapter.setElencoRecensioni(elencoRecensioni, true);
        progress.setVisibility(View.INVISIBLE);
        if(elencoRecensioni.size() > 0) {
            no_recensioni.setVisibility(View.INVISIBLE);
        }
        else {
            no_recensioni.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onGoTo(View view, int position) {
        requestRistorante(position, true);
    }

    private void requestRistorante(int position, boolean showDialog){
        Recensione r = elencoRecensioni.get(position);
        HttpGetRequest requestRistoranti = new HttpGetRequest(getActivity(), showDialog);
        requestRistoranti.setOnRistorantiReadyListener(this);
        requestRistoranti.execute("http://dellamateralorenzo.altervista.org/valtellina_advisor/getRistoranti.php?idUtente="+Utente.getId()+"&idRistorante="+r.getIdRistorante());
    }

    @Override
    public void onDelete(View view, int position, double votoRecensione) {
        view.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.shake));

        DeleteDialog deleteDialog = new DeleteDialog(elencoRecensioni.get(position).getIdRecensione());
        deleteDialog.setOnRecensioneDeletedListener(new HttpGetRequest.OnRecensioneDeletedListener() {
            @Override
            public void onRecensioneDeleted() {
                deleteDialog.dismiss();
                ExplosionField explosionField = ExplosionField.attach2Window(getActivity());
                explosionField.explode(view);
                Utente.rimuoviRecensione(votoRecensione);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        refresh_flag = true;
                        requestRistorante(position, false);
                        adapter.deleteItem(position);
                    }
                }, 300);
            }
        });
        deleteDialog.show(getActivity().getSupportFragmentManager(), "delete_dialog");
        deleteDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                view.clearAnimation();
            }
        });
    }

    @Override
    public void onRistorantiReady(ArrayList<Ristorante> elencoRistoranti, String strurl) {
        Ristorante r = elencoRistoranti.get(0);

        if(refresh_flag) {
            if (ElencoRistoranti.getRistorante(r.getIdRistorante()) != null)
                ElencoRistoranti.putRistorante(r);
            refresh_flag = false;
            return;
        }

        Intent intent = new Intent(getActivity(), RestaurantActivity.class);
        intent.putExtra("serializable_ristorante", (Serializable) r);
        startActivityForResult(intent, 1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {
            if(resultCode == Activity.RESULT_OK){
                if(data.getBooleanExtra("refresh", false)){
                    HttpGetRequest requestRecensioni = new HttpGetRequest(getActivity(), false);
                    requestRecensioni.setOnRecensioniReadyListener(this);
                    requestRecensioni.execute("http://dellamateralorenzo.altervista.org/valtellina_advisor/getRecensioni.php?idUtente="+Utente.getId());
                }
            }
        }
    }

}