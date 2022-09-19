package com.example.valtellinaadvisor.data;

import java.io.Serializable;

public class Coordinate implements Serializable {
    private double lat;
    private double lng;

    public Coordinate(double lat, double lng) {
        this.lat = lat;
        this.lng = lng;
    }

    public double getLat() {
        return lat;
    }

    public double getLng() {
        return lng;
    }

    public String toString(){
        return lat + ", " + lng;
    }
}
