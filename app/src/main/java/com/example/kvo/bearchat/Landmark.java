package com.example.kvo.bearchat;

/**
 * Created by kvo on 4/11/18.
 */

public class Landmark {
    public String name;
    public double latitude;
    public double longitude;
    public int filename;
    public double distance;

    Landmark(String name, double lat, double lon, double distance, int filename) {
        this.name = name;
        this.latitude = lat;
        this.longitude = lon;
        this.filename = filename;
        this.distance = distance;
    }

    public String getName() {
        return name;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public int getFilename() {
        return filename;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public double getDistance() {
        return distance;
    }

    protected boolean within10m(Landmark lm) {
        return this.distanceTo(lm) < 10;
    }

    protected double distanceTo(Landmark lm) {
        double x1 = this.latitude;
        double x2 = lm.getLatitude();
        double y1 = this.longitude;
        double y2 = lm.getLongitude();
        return Math.round(
                Math.sqrt(
                        Math.pow(x2 - x1, 2) - Math.pow(y2 - y1, 2)));
    }

}
