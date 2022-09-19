package com.example.valtellinaadvisor.data;

import java.io.Serializable;

public class Citta implements Serializable {
    private int idCitta;
    private String nome;
    private int CAP;
    private String provincia;
    private String regione;

    public Citta(int idCitta, String nome, int CAP, String provincia, String regione) {
        this.idCitta = idCitta;
        this.nome = nome;
        this.CAP = CAP;
        this.provincia = provincia;
        this.regione = regione;
    }

    public int getIdCitta() {
        return idCitta;
    }

    public String getNome() {
        return nome;
    }

    public int getCAP() {
        return CAP;
    }

    public String getProvincia() {
        return provincia;
    }

    public String getRegione() {
        return regione;
    }
}
