package com.example.valtellinaadvisor.data;

import java.io.Serializable;

public class Ristorante implements Serializable {
    private int idRistorante;
    private String nome;
    private String indirizzo;
    private Citta citta;
    private String telefono;
    private Coordinate coordinate;
    private String categoria;
    private String pathImg;
    private double rating;
    private boolean favorite;

    public Ristorante(int idRistorante, String nome, String indirizzo, int idCitta, String telefono, double lat, double lng, String categoria, String pathImg, double rating, boolean favorite) {
        this.idRistorante = idRistorante;
        this.nome = nome;
        this.indirizzo = indirizzo;
        this.citta = ElencoCitta.getCitta(idCitta);
        this.telefono = telefono;
        this.coordinate = new Coordinate(lat, lng);
        this.categoria = categoria;
        this.pathImg = pathImg;
        this.rating = rating;
        this.favorite = favorite;
    }

    public int getIdRistorante() {
        return idRistorante;
    }

    public String getNome() {
        return nome;
    }

    public String getIndirizzo() {
        return indirizzo;
    }

    public Citta getCitta() {
        return citta;
    }

    public String getTelefono() {
        return telefono;
    }

    public Coordinate getCoordinate() { return  coordinate; }

    public String getCategoria() {
        return categoria;
    }

    public String getPathImg() {
        return pathImg;
    }

    public double getRating(){ return rating; }

    public void setRating(double rating) { this.rating = rating; }

    public boolean isFavorite() { return favorite; }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

}
