package com.example.valtellinaadvisor.user;

import android.content.Context;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter {

    private final Context mContext;
    private FragmentDati fragmentDati;
    private FragmentRecensioni fragmentRecensioni;

    public SectionsPagerAdapter(Context context, FragmentManager fm) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        mContext = context;
        fragmentDati = new FragmentDati();
        fragmentRecensioni = new FragmentRecensioni();
    }

    @Override
    public Fragment getItem(int position) {
        if(position==0)
            return fragmentDati;
        else
            return fragmentRecensioni;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        if(position==0)
            return "I miei dati";
        else
            return "Le mie recensioni";
    }

    @Override
    public int getCount() {
        // Show 2 total pages.
        return 2;
    }

    public FragmentDati getFragmentDati() {
        return fragmentDati;
    }

    public FragmentRecensioni getFragmentRecensioni() {
        return fragmentRecensioni;
    }
}