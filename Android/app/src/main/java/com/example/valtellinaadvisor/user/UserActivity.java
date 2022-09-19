package com.example.valtellinaadvisor.user;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.example.valtellinaadvisor.http.HttpGetRequest;
import com.example.valtellinaadvisor.R;
import com.google.android.material.tabs.TabLayout;

public class UserActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);

        HttpGetRequest requestRecensioni = new HttpGetRequest(this, false);
        requestRecensioni.setOnRecensioniReadyListener(sectionsPagerAdapter.getFragmentRecensioni());
        requestRecensioni.execute("http://dellamateralorenzo.altervista.org/valtellina_advisor/getRecensioni.php?idUtente="+Utente.getId());
    }
}