package com.martasim.models;

import java.util.Objects;

public class Event implements Cloneable {
    String busId;
    String stopId;
    int arrivalTime;
    int departureTime;

    public Event(String busId, String stopId, int arrivalTime, int departureTime) {
        this.busId = busId;
        this.stopId = stopId;
        this.arrivalTime = arrivalTime;
        this.departureTime = departureTime;
    }

    @Override
    public String toString() {
        return "('" +
                busId + "', '" +
                stopId + "', " +
                arrivalTime + ", " +
                departureTime +
                ')';
    }

    public String getBusId() {
        return busId;
    }

    public void setBusId(String busId) {
        this.busId = busId;
    }

    public String getStopId() {
        return stopId;
    }

    public void setStopId(String stopId) {
        this.stopId = stopId;
    }

    public int getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(int arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public int getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(int departureTime) {
        this.departureTime = departureTime;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Event)) return false;
        Event event = (Event) o;
        return arrivalTime == event.arrivalTime &&
                departureTime == event.departureTime &&
                Objects.equals(busId, event.busId) &&
                Objects.equals(stopId, event.stopId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(busId, stopId, arrivalTime, departureTime);
    }
}
