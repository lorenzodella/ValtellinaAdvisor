package com.example.valtellinaadvisor.restaurant;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import com.example.valtellinaadvisor.main.MapsActivity;
import com.example.valtellinaadvisor.R;
import com.example.valtellinaadvisor.data.Citta;
import com.example.valtellinaadvisor.data.ElencoRistoranti;
import com.example.valtellinaadvisor.data.Recensione;
import com.example.valtellinaadvisor.data.Ristorante;
import com.example.valtellinaadvisor.http.HttpGetRequest;
import com.example.valtellinaadvisor.http.HttpPostRequest;
import com.example.valtellinaadvisor.user.Utente;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

public class RestaurantActivity extends AppCompatActivity implements
        HttpGetRequest.OnRecensioniReadyListener,
        HttpPostRequest.OnRecensioneUploadedListener,
        HttpPostRequest.OnPreferitoSetListener{
    Ristorante ristorante;
    ImageView imageRestaurant;
    TextView indirizzo, citta, coordinate, telefono, categoria;
    RatingBar rating;
    FloatingActionButton addComment;
    ArrayList<Recensione> elencoRecensioni;
    RecyclerView recyclerView;
    RecyclerViewAdapterRecensioni adapter;
    TextView no_recensioni;
    ProgressBar progress;
    MenuItem favoriteItem;
    boolean flagRefresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        flagRefresh = false;

        progress = findViewById(R.id.progress);
        no_recensioni = findViewById(R.id.no_recensioni);
        no_recensioni.setVisibility(View.INVISIBLE);

        indirizzo = findViewById(R.id.indirizzo);
        citta = findViewById(R.id.citta);
        coordinate = findViewById(R.id.coordinate);
        telefono = findViewById(R.id.telefono);
        categoria = findViewById(R.id.categoria);
        rating = findViewById(R.id.rating);

        elencoRecensioni = new ArrayList<Recensione>();
        setRecyclerView();

        getRistorante();
        requestRecensioni();

        setTitle(ristorante.getNome());
        net.opacapp.multilinecollapsingtoolbar.CollapsingToolbarLayout toolBarLayout = (net.opacapp.multilinecollapsingtoolbar.CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        toolBarLayout.setTitle(ristorante.getNome());
        toolBarLayout.setCollapsedTitleTextAppearance(R.style.TitleBarTextAppearance);
        toolBarLayout.setExpandedTitleTextAppearance(R.style.TitleRestaurantTextAppearance);

        AppBarLayout appBarLayout = findViewById(R.id.app_bar);
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                System.out.println(verticalOffset);
                if(verticalOffset > -400){
                    addComment.hide();
                }
                else {
                    addComment.show();
                }
            }
        });

        FloatingActionButton show_location = (FloatingActionButton) findViewById(R.id.show_location);
        show_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RestaurantActivity.this, MapsActivity.class);
                intent.putExtra("idRistorante", ristorante.getIdRistorante());
                startActivity(intent);
            }
        });

        addComment = findViewById(R.id.add_comment);
        addComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRecensioneDialog();
            }
        });
        addComment.hide();

        imageRestaurant = findViewById(R.id.image);
        new RequestImage(ristorante.getPathImg()).start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_restaurant, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        favoriteItem = menu.findItem(R.id.action_fav);
        if(ristorante.isFavorite())
            favoriteItem.setIcon(R.drawable.ic_baseline_favorite_24);
        else
            favoriteItem.setIcon(R.drawable.ic_baseline_favorite_border_24);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.action_fav){
            setPreferito();
            return true;
        }

        return false;
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("refresh", flagRefresh);
        setResult(RESULT_OK, intent);
        super.onBackPressed();
    }

    private void setPreferito() {
        HttpPostRequest setPreferito = new HttpPostRequest(this, false);
        setPreferito.setOnPreferitoSetListener(this, null);
        setPreferito.execute("http://dellamateralorenzo.altervista.org/valtellina_advisor/setPreferito.php?mode=" + (ristorante.isFavorite() ? "remove" : "add"),
                String.valueOf(ristorante.getIdRistorante()),
                String.valueOf(Utente.getId()));
    }

    @Override
    public void onPreferitoSet(ImageButton favoriteButton) {
        if(ristorante.isFavorite()) {
            ristorante.setFavorite(false);
            favoriteItem.setIcon(R.drawable.ic_baseline_favorite_border_24);
            Toast.makeText(getApplicationContext(), "Rimosso dai preferiti", Toast.LENGTH_SHORT).show();
        }
        else {
            ristorante.setFavorite(true);
            favoriteItem.setIcon(R.drawable.ic_baseline_favorite_24);
            Toast.makeText(getApplicationContext(), "Aggiunto ai preferiti", Toast.LENGTH_SHORT).show();
        }
        if(ElencoRistoranti.getRistorante(ristorante.getIdRistorante()) != null)
            ElencoRistoranti.putRistorante(ristorante);
    }

    private void getRistorante() {
        if(getIntent().hasExtra("serializable_ristorante")){
            ristorante = (Ristorante) getIntent().getSerializableExtra("serializable_ristorante");
        }
        else{
            int idRistorante = getIntent().getIntExtra("idRistorante",1);
            ristorante = ElencoRistoranti.getRistorante(idRistorante);
        }

        indirizzo.setText(ristorante.getIndirizzo());
        Citta c  = ristorante.getCitta();
        citta.setText(c.getNome() + " - " + c.getProvincia());
        coordinate.setText(ristorante.getCoordinate().toString());
        telefono.setText(ristorante.getTelefono());
        categoria.setText(ristorante.getCategoria());
        rating.setRating((float)ristorante.getRating());
        //System.out.println(rating.getRating());
    }

    private void requestRecensioni(){
        no_recensioni.setVisibility(View.INVISIBLE);
        progress.setVisibility(View.VISIBLE);
        HttpGetRequest requestRecensioni = new HttpGetRequest(this, false);
        requestRecensioni.setOnRecensioniReadyListener(this);
        requestRecensioni.execute("http://dellamateralorenzo.altervista.org/valtellina_advisor/getRecensioni.php?idRistorante="+ristorante.getIdRistorante());
    }

    public void onRecensioniReady(ArrayList<Recensione> elencoRecensioni) {
        this.elencoRecensioni = elencoRecensioni;
        adapter.setElencoRecensioni(elencoRecensioni, true);
        progress.setVisibility(View.INVISIBLE);
        if(elencoRecensioni.size() > 0) {
            no_recensioni.setVisibility(View.INVISIBLE);
            if(flagRefresh)
                refreshRating();
        }
        else {
            no_recensioni.setVisibility(View.VISIBLE);
        }
    }

    private void setRecyclerView() {
        recyclerView = findViewById(R.id.recyclerView_recensioni);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        adapter = new RecyclerViewAdapterRecensioni(elencoRecensioni, recyclerView);
        recyclerView.setAdapter(adapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
    }

    private void showRecensioneDialog(){
        RecensioneDialog recensioneDialog = new RecensioneDialog(this, ristorante.getIdRistorante(), Utente.getId());
        recensioneDialog.show(getSupportFragmentManager(), "recensione_dialog");
    }

    @Override
    public void onRecensioneUploaded(double votoRecensione) {
        Toast.makeText(getApplicationContext(), "recensione salvata", Toast.LENGTH_SHORT).show();
        Utente.aggiungiRecensione(votoRecensione);
        flagRefresh = true;
        requestRecensioni();
    }

    @Override
    public void onRecensioneNotValid() {
        Toast.makeText(getApplicationContext(), "recensione non valida", Toast.LENGTH_SHORT).show();
    }

    private void refreshRating(){
        double rating = 0.0;
        for (Recensione r : elencoRecensioni) {
            rating += r.getVoto();
        }
        rating /= elencoRecensioni.size();
        ristorante.setRating(rating);
        if(ElencoRistoranti.getRistorante(ristorante.getIdRistorante()) != null)
            ElencoRistoranti.putRistorante(ristorante);
        this.rating.setRating((float)rating);
    }

    public class RequestImage extends Thread
    {
        String sURL = "http://dellamateralorenzo.altervista.org/valtellina_advisor/images/";
        public RequestImage(String image) {
            this.sURL += image;
        }
        public void run()
        {
            try {
                URL url = new URL(sURL);
                Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        imageRestaurant.setImageBitmap(bmp);
                        ProgressBar bar = findViewById(R.id.progress_bar);
                        bar.setVisibility(View.GONE);
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}