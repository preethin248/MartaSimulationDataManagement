package com.martasim.models;

import java.util.Objects;

public class Stop {
    final String id;
    String name;
    int riders;
    int previousRiders;
    double latitude;
    double longitude;

    public Stop(String id, String name, int riders, double latitude, double longitude) {
        this(id, name, riders, -1, latitude, longitude);
    }

    public Stop(String id, String name, int riders, int previousRiders, double latitude, double longitude) {
        this.id = id;
        this.name = name;
        this.riders = riders;
        this.previousRiders = previousRiders;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    @Override
    public String toString() {
        return "('" +
                id + "', " +
                '\'' + name + "', " +
                riders + ", " +
                previousRiders + ", " +
                latitude + ", " +
                longitude +
                ")";
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getRiders() {
        return riders;
    }

    public void setRiders(int riders) {
        if (riders != this.riders) {
            this.previousRiders = this.riders;
            this.riders = riders;
        }
    }

    public int getPreviousRiders() {
        return this.previousRiders;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Stop)) return false;
        Stop stop = (Stop) o;
        return id.equals(stop.id) &&
                riders == stop.riders &&
                Double.compare(stop.latitude, latitude) == 0 &&
                Double.compare(stop.longitude, longitude) == 0 &&
                Objects.equals(name, stop.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, riders, latitude, longitude);
    }
}
