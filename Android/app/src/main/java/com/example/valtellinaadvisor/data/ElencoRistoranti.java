package com.example.valtellinaadvisor.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class ElencoRistoranti {
    static LinkedHashMap<Integer,Ristorante> elencoRistoranti = new LinkedHashMap<Integer, Ristorante>();

    static public void putRistorante(Ristorante ristorante){
        elencoRistoranti.put(ristorante.getIdRistorante(), ristorante);
    }

    static public Ristorante getRistorante(int id){
        return elencoRistoranti.get(id);
    }

    static public void clear(){
        elencoRistoranti.clear();
    }

    static public ArrayList<Ristorante> getElencoRistoranti(){
        ArrayList<Ristorante> list = new ArrayList<Ristorante>();
        list.addAll(elencoRistoranti.values());
        return list;
    }
}
