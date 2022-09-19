package com.example.valtellinaadvisor.main;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.valtellinaadvisor.http.HttpGetRequest;
import com.example.valtellinaadvisor.http.HttpPostRequest;
import com.example.valtellinaadvisor.http.ProgressDialog;
import com.example.valtellinaadvisor.R;
import com.example.valtellinaadvisor.restaurant.RestaurantActivity;
import com.example.valtellinaadvisor.data.ElencoRistoranti;
import com.example.valtellinaadvisor.data.Ristorante;
import com.example.valtellinaadvisor.user.UserActivity;
import com.example.valtellinaadvisor.user.Utente;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements
        HttpGetRequest.OnRistorantiReadyListener,
        RecyclerViewAdapterRistoranti.OnFavoriteClickListener,
        HttpPostRequest.OnPreferitoSetListener {

    NearbyUtils myNearbyUtils;
    LocationManager myLocationManager;
    LocationListener locationListener;
    ProgressDialog dialog;
    RecyclerView recyclerView;
    RecyclerViewAdapterRistoranti adapter;
    ArrayList<Ristorante> elencoRistoranti;
    EditText editRistorante;
    ImageButton dropdown, selectPreferiti, selectCitta, selectNearby, search;
    ExtendedFloatingActionButton mostraMappa;
    TextView no_ristoranti;
    boolean dropdown_flag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        Bitmap icon = BitmapFactory.decodeResource(getResources(), R.drawable.icon_foreground);
        icon = Bitmap.createScaledBitmap(icon, 110, 110, true);
        toolbar.setNavigationIcon(new BitmapDrawable(getResources(), icon));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        no_ristoranti = findViewById(R.id.no_ristoranti);
        no_ristoranti.setVisibility(View.INVISIBLE);

        editRistorante = findViewById(R.id.edit_ristorante);
        editRistorante.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    search();
                    return true;
                }
                return false;
            }
        });

        dropdown = findViewById(R.id.dropdown);
        dropdown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dropDown(!dropdown_flag);
            }
        });

        selectNearby = findViewById(R.id.select_nearby);
        selectNearby.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestLocation();
                dropDown(false);
            }
        });
        selectPreferiti = findViewById(R.id.select_preferiti);
        selectPreferiti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestPreferiti();
                dropDown(false);
            }
        });
        selectCitta = findViewById(R.id.select_city);
        selectCitta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCittaDialog();
                dropDown(false);
            }
        });
        search = findViewById(R.id.search);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search();
            }
        });
        mostraMappa = findViewById(R.id.mostra);
        mostraMappa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MapsActivity.class);
                if(myNearbyUtils != null && myNearbyUtils.getFlag()) {
                    intent.putExtra("nearby_maxDist", myNearbyUtils.getMaxDistance());
                    intent.putExtra("nearby_lat", myNearbyUtils.getLatitude());
                    intent.putExtra("nearby_lng", myNearbyUtils.getLongitude());
                }
                startActivity(intent);
            }
        });
        mostraMappa.hide();

        elencoRistoranti = ElencoRistoranti.getElencoRistoranti();
        setRecyclerView();

        checkLocationPermission();
    }

    private void checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                                Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.INTERNET}
                        , 10);
            }
            return;
        } else {
           setLocationManager();
        }
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 10) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                    PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                showLocationPermissionAlert();
            } else {
                setLocationManager();
            }
        }
    }

    private void showLocationPermissionAlert() {
        new AlertDialog.Builder(this)
                .setTitle("Attenzione!")
                .setMessage("Devi abilitare la localizzazione per poter utilizzare alcune funzioni.")
                .setNegativeButton("non abilitare", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton("abilita", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        checkLocationPermission();
                    }
                }).show();
    }

    private void setLocationManager(){
        myLocationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                myNearbyUtils.setLatitude(location.getLatitude());
                myNearbyUtils.setLongitude(location.getLongitude());
                requestNearby(location.getLatitude(), location.getLongitude());
                myLocationManager.removeUpdates(this);
            }
            public void onStatusChanged(String provider, int status, Bundle extras) {}
            public void onProviderEnabled(String provider) {}
            public void onProviderDisabled(String provider) {}
        };
        myNearbyUtils = new NearbyUtils(2000);
    }

    private void requestNearby(double latitude, double longitude) {
        HttpGetRequest requestRistoranti = new HttpGetRequest(this, false);
        requestRistoranti.setOnRistorantiReadyListener(this);
        requestRistoranti.setDialog(dialog);
        requestRistoranti.execute("http://dellamateralorenzo.altervista.org/valtellina_advisor/getNearby.php?idUtente=" + Utente.getId() +
                "&maxDist=2000" + "&myLAT=" + latitude + "&myLNG=" + longitude);
    }

    private void dropDown(boolean flag) {
        if (flag && !dropdown_flag) {
            dropdown.startAnimation(AnimationUtils.loadAnimation(this, R.anim.rotate_open));
            selectCitta.setVisibility(View.VISIBLE);
            selectPreferiti.setVisibility(View.VISIBLE);
            selectNearby.setVisibility(View.VISIBLE);
        } else if(!flag && dropdown_flag){
            dropdown.startAnimation(AnimationUtils.loadAnimation(this, R.anim.rotate_close));
            selectCitta.setVisibility(View.GONE);
            selectPreferiti.setVisibility(View.GONE);
            selectNearby.setVisibility(View.GONE);
        }
        else return;
        dropdown_flag = !dropdown_flag;
    }

    private void requestLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if ( !myLocationManager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
                Toast.makeText(getApplicationContext(), "GPS disattivato", Toast.LENGTH_SHORT).show();
                return;
            }

            myLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            dialog = new ProgressDialog(this, true);
            dialog.setOnCancelListener( new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    myLocationManager.removeUpdates(locationListener);
                }
            });
            dialog.setText("Ricerca ristoranti vicini...");
            dialog.show(getSupportFragmentManager(), "progress_dialog");
        }
        else {
            showLocationPermissionAlert();
        }
    }

    private void requestPreferiti() {
        HttpGetRequest requestRistoranti = new HttpGetRequest(this, true);
        requestRistoranti.setOnRistorantiReadyListener(this);
        requestRistoranti.execute("http://dellamateralorenzo.altervista.org/valtellina_advisor/getPreferiti.php?idUtente="+Utente.getId());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.profilo){
            Intent intent = new Intent(MainActivity.this, UserActivity.class);
            startActivity(intent);
            //esci();
            return true;
        }
        else if(id == android.R.id.home){
            Toast.makeText(getApplicationContext(),"      © 2021 Della Matera Lorenzo\n" +
                                                        "Valtellina Advisor - all rights reserved",Toast.LENGTH_SHORT).show();
        }

        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        adapter.setElencoRistoranti(ElencoRistoranti.getElencoRistoranti(), false);
    }

    private void search() {
        dropDown(false);
        hideKeyboard(this);
        String nome = editRistorante.getText().toString();
        if(!nome.trim().isEmpty())
            requestRistoranti(nome);
    }

    private void showCittaDialog() {
        //Toast.makeText(getApplicationContext(), "seleziona la citta", Toast.LENGTH_SHORT).show();
        CittaDialog cittaDialog = new CittaDialog(this);
        cittaDialog.show(getSupportFragmentManager(), "citta_dialog");
        editRistorante.setText("");
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public void onRistorantiReady(ArrayList<Ristorante> elencoRistoranti, String strurl) {
        myNearbyUtils.setFlag(strurl.contains("getNearby"));
        this.elencoRistoranti = elencoRistoranti;
        adapter.setElencoRistoranti(elencoRistoranti, true);
        if(elencoRistoranti.size() > 0) {
            mostraMappa.show();
            no_ristoranti.setVisibility(View.INVISIBLE);
        }
        else {
            mostraMappa.hide();
            no_ristoranti.setVisibility(View.VISIBLE);
            Toast.makeText(getApplicationContext(), "nessun ristorante trovato", Toast.LENGTH_SHORT).show();
        }
    }

    private void setRecyclerView() {
        recyclerView = findViewById(R.id.recyclerView_ristoranti);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        adapter = new RecyclerViewAdapterRistoranti(elencoRistoranti, recyclerView);
        adapter.setClickListener(new RecyclerViewAdapterRistoranti.ItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                //Toast.makeText(getApplicationContext(), "hai premuto "+elencoRistoranti.get(position).getNome(), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this, RestaurantActivity.class);
                intent.putExtra("idRistorante", elencoRistoranti.get(position).getIdRistorante());
                startActivity(intent);
            }
        });
        adapter.setOnFavoriteClickListener(this);
        recyclerView.setAdapter(adapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        if(elencoRistoranti.size() > 0)
            mostraMappa.show();
    }

    private void requestRistoranti(String nome) {
        HttpGetRequest requestRistoranti = new HttpGetRequest(this, true);
        requestRistoranti.setOnRistorantiReadyListener(this);
        requestRistoranti.execute("http://dellamateralorenzo.altervista.org/valtellina_advisor/getRistoranti.php?idUtente="+Utente.getId()+"&nome="+nome);
    }

    @Override
    public void onPreferitoSet(ImageButton favoriteButton) {
        int idRistorante = (int)favoriteButton.getTag();
        Ristorante r = ElencoRistoranti.getRistorante(idRistorante);
        if(r.isFavorite()) {
            r.setFavorite(false);
            favoriteButton.setImageResource(R.drawable.ic_baseline_favorite_border_24);
            Toast.makeText(getApplicationContext(), "Rimosso dai preferiti", Toast.LENGTH_SHORT).show();
        }
        else {
            r.setFavorite(true);
            favoriteButton.setImageResource(R.drawable.ic_baseline_favorite_red_24);
            Toast.makeText(getApplicationContext(), "Aggiunto ai preferiti", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onFavoriteClick(ImageButton favoriteButton, int position, int idRistorante) {
        Ristorante r = elencoRistoranti.get(position);
        HttpPostRequest setPreferito = new HttpPostRequest(this, false);
        setPreferito.setOnPreferitoSetListener(this, favoriteButton);
        setPreferito.execute("http://dellamateralorenzo.altervista.org/valtellina_advisor/setPreferito.php?mode=" + (r.isFavorite() ? "remove" : "add"),
                String.valueOf(idRistorante),
                String.valueOf(Utente.getId()));
    }
}