package com.example.valtellinaadvisor.main;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import com.example.valtellinaadvisor.R;
import com.example.valtellinaadvisor.data.Coordinate;
import com.example.valtellinaadvisor.data.ElencoRistoranti;
import com.example.valtellinaadvisor.data.Ristorante;
import com.example.valtellinaadvisor.restaurant.RestaurantActivity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.HashMap;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private HashMap<Marker, Integer> markerMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        }

        if(getIntent().hasExtra("idRistorante")){
            setSingleRestaurant();
        }
        else {
            setMultipleRestaurante();
            googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                @Override
                public void onInfoWindowClick(Marker marker) {
                    Intent intent = new Intent(MapsActivity.this, RestaurantActivity.class);
                    intent.putExtra("idRistorante", markerMap.get(marker));
                    startActivity(intent);
                }
            });
        }
    }

    private void setMultipleRestaurante() {
        ArrayList<Ristorante> ristoranti = ElencoRistoranti.getElencoRistoranti();
        markerMap = new HashMap<>();
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (Ristorante r : ristoranti) {
            Coordinate coordinate = r.getCoordinate();
            LatLng rist = new LatLng(coordinate.getLat(), coordinate.getLng());
            Marker marker = mMap.addMarker(new MarkerOptions()
                    .position(rist)
                    .title(r.getNome())
                    .snippet(r.getIndirizzo())
            );
            builder.include(marker.getPosition());
            markerMap.put(marker, r.getIdRistorante());
        }
        LatLngBounds bounds = builder.build();
        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 180));

        if(getIntent().hasExtra("nearby_maxDist")){
            double lat = getIntent().getDoubleExtra("nearby_lat",0);
            double lng = getIntent().getDoubleExtra("nearby_lng",0);
            int maxDist = getIntent().getIntExtra("nearby_maxDist",-1);
            if(maxDist!=-1) {
                Circle circle = mMap.addCircle(new CircleOptions()
                        .strokeWidth(5f)
                        .strokeColor(ContextCompat.getColor(this, R.color.green_darker))
                        .center(new LatLng(lat, lng))
                        .radius(maxDist));
            }
        }
    }

    private void setSingleRestaurant() {
        int idRistorante = getIntent().getIntExtra("idRistorante",1);
        Ristorante r = ElencoRistoranti.getRistorante(idRistorante);
        Coordinate coordinate = r.getCoordinate();

        LatLng rist = new LatLng(coordinate.getLat(), coordinate.getLng());
        mMap.addMarker(new MarkerOptions()
                .position(rist)
                .title(r.getNome())
                .snippet(r.getIndirizzo())
        );
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(rist, 16));
    }
}