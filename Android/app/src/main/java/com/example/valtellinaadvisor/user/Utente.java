package com.example.valtellinaadvisor.user;

import android.graphics.Color;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Utente {
    private static int id;
    private static String username;
    private static String password;
    private static String nome;
    private static String cognome;
    private static String mail;
    private static int colore;
    private static int numRecensioni;
    private static double mediaVoti;

    public static void setUtente(int id, String username, String password, String nome, String cognome, String mail, String colore, int numRecensioni, double mediaVoti) {
        Utente.id = id;
        Utente.username = username;
        Utente.password = password;
        Utente.nome = nome;
        Utente.cognome = cognome;
        Utente.mail = mail;
        Utente.colore = Color.parseColor(colore);
        Utente.numRecensioni = numRecensioni;
        Utente.mediaVoti = mediaVoti;
    }

    public static int getId() {
        return id;
    }

    public static String getUsername() {
        return username;
    }

    public static String getPassword() {
        return password;
    }

    public static void setPassword(String password) { Utente.password = password; }

    public static String getNome() {
        return nome;
    }

    public static String getCognome() {
        return cognome;
    }

    public static String getMail() {
        return mail;
    }

    public static int getColore() {
        return colore;
    }

    public static int getNumRecensioni() {
        return numRecensioni;
    }

    public static double getMediaVoti() {
        return mediaVoti;
    }

    public static String md5(final String s) {
        final String MD5 = "MD5";
        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest
                    .getInstance(MD5);
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuilder hexString = new StringBuilder();
            for (byte aMessageDigest : messageDigest) {
                String h = Integer.toHexString(0xFF & aMessageDigest);
                while (h.length() < 2)
                    h = "0" + h;
                hexString.append(h);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static void aggiungiRecensione(double nuovoVoto) {
        double somma = mediaVoti * numRecensioni;
        Utente.numRecensioni++;
        mediaVoti = (somma+nuovoVoto) / numRecensioni;
    }

    public static void rimuoviRecensione(double vecchioVoto) {
        double somma = mediaVoti * numRecensioni;
        Utente.numRecensioni--;
        if(numRecensioni == 0)
            mediaVoti = 0;
        else
            mediaVoti = (somma-vecchioVoto) / numRecensioni;
    }
}
