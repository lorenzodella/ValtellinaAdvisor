package com.example.valtellinaadvisor.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class ElencoCitta {
    static private LinkedHashMap<Integer,Citta> elencoCitta = new LinkedHashMap<Integer, Citta>();

    static public void putCitta(Citta citta){
        elencoCitta.put(citta.getIdCitta(), citta);
    }

    static public Citta getCitta(int id){
        return elencoCitta.get(id);
    }

    static public void clear(){
        elencoCitta.clear();
    }

    static public ArrayList<Citta> getElencoCitta(){
        ArrayList<Citta> list = new ArrayList<Citta>();
        list.addAll(elencoCitta.values());
        return list;
    }

    static public Citta[] getElencoCittaArray(){
        return elencoCitta.values().toArray(new Citta[0]);
    }
}
