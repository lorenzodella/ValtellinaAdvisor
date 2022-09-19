package com.example.valtellinaadvisor.main;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.valtellinaadvisor.data.Citta;

public class MySpinnerAdapter extends ArrayAdapter<Citta> {
    private Context context;
    private Citta[] values;

    public MySpinnerAdapter(Context context, int textViewResourceId, Citta[] values) {
        super(context, textViewResourceId, values);
        this.context = context;
        this.values = values;
    }

    @Override
    public int getCount(){
        return values.length;
    }

    @Override
    public Citta getItem(int position){
        return values[position];
    }

    @Override
    public long getItemId(int position){
        return position;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView label = (TextView) super.getView(position, convertView, parent);
        label.setText(values[position].getNome());
        return label;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        TextView label = (TextView) super.getDropDownView(position, convertView, parent);
        label.setText(values[position].getNome());
        return label;
    }
}
