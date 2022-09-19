package com.example.valtellinaadvisor.data;

public class Recensione  {
    private int idRecensione;
    private int idRistorante;
    private String nomeRistorante;
    private String data;
    private double voto;
    private String commento;
    private String username;
    private String colore;

    public Recensione(int idRecensione, int idRistorante, String nomeRistorante, String data, double voto, String commento, String username, String colore) {
        this.idRecensione = idRecensione;
        this.idRistorante = idRistorante;
        this.nomeRistorante = nomeRistorante;
        this.data = data;
        this.voto = voto;
        this.commento = commento;
        this.username = username;
        this.colore = colore;
    }

    public int getIdRecensione() {
        return idRecensione;
    }

    public int getIdRistorante() {
        return idRistorante;
    }

    public String getNomeRistorante() {
        return nomeRistorante;
    }

    public String getData() {
        return data;
    }

    public double getVoto() {
        return voto;
    }

    public String getCommento() {
        return commento;
    }

    public String getUsername() {
        return username;
    }

    public String getColore() {
        return colore;
    }
}
