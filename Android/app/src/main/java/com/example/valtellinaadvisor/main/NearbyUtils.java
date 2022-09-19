package com.example.valtellinaadvisor.main;

public class NearbyUtils {
    private boolean flag;
    private double latitude;
    private double longitude;
    private int maxDistance;

    public NearbyUtils(int maxDistance) {
        this.flag = false;
        this.maxDistance = maxDistance;
    }

    public boolean getFlag() {
        return flag;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public int getMaxDistance() {
        return maxDistance;
    }

    public void setMaxDistance(int maxDistance) {
        this.maxDistance = maxDistance;
    }
}
