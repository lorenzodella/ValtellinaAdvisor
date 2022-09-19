package com.example.valtellinaadvisor.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class ElencoRecensioni {
    static private LinkedHashMap<Integer,Recensione> elencoRecensioni = new LinkedHashMap<Integer, Recensione>();

    static public void putRecensione(Recensione recensione){
        elencoRecensioni.put(recensione.getIdRecensione(), recensione);
    }

    static public Recensione getRecensione(int id){
        return elencoRecensioni.get(id);
    }

    static public void clear(){
        elencoRecensioni.clear();
    }

    static public ArrayList<Recensione> getElencoRecensioni(){
        ArrayList<Recensione> list = new ArrayList<Recensione>();
        list.addAll(elencoRecensioni.values());
        return list;
    }

    static public Recensione[] getElencoRecensioniArray(){
        return elencoRecensioni.values().toArray(new Recensione[0]);
    }
}
