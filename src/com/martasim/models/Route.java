package com.martasim.models;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Route {
    String id;
    String shortName;
    String name;
    List<Stop> stops;

    public Route(String id, String shortName, String name) {
        this(id, shortName, name, new ArrayList<>());
    }

    public Route(String id, String shortName, String name, List<Stop> stops) {
        this.id = id;
        this.shortName = shortName;
        this.name = name;
        this.stops = stops;
    }

    @Override
    public String toString() {
        return "('" +
                id + "', '" +
                shortName + "', '" +
                 name + '\'' +
                ")";
    }

    public String getId() {
        return id;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void extend(Stop stop) {
        this.stops.add(stop);
    }

    public List<Stop> getStops() {
        return stops;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Route)) return false;
        Route route = (Route) o;
        return id.equals(route.id) &&
                shortName.equals(route.shortName) &&
                Objects.equals(name, route.name) &&
                Objects.equals(stops, route.stops);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, shortName, name, stops);
    }
}
