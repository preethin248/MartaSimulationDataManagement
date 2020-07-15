package com.martasim.models;

import java.util.Objects;

public class Bus {
    final String id;
    Route route;
    boolean outbound;
    int currentStop;
    double latitude;
    double longitude;
    int passengers;
    int passengerCapacity;
    double fuel;
    double fuelCapacity;
    double speed;

    public Bus(String id, Route route, boolean outbound, double latitude, double longitude, int passengers,
               int passengerCapacity, double fuel, double fuelCapacity, double speed) {
        this(id, route, outbound, -1, latitude, longitude, passengers, passengerCapacity, fuel, fuelCapacity, speed);
    }

    public Bus(String id, Route route, boolean outbound, int currentStop, double latitude, double longitude, int passengers,
               int passengerCapacity, double fuel, double fuelCapacity, double speed) {
        this.id = id;
        this.route = route;
        this.outbound = outbound;
        this.setCurrentStopIndex(currentStop);
        this.latitude = latitude;
        this.longitude = longitude;
        this.passengers = passengers;
        this.passengerCapacity = passengerCapacity;
        this.fuel = fuel;
        this.fuelCapacity = fuelCapacity;
        this.speed = speed;
    }

    @Override
    public String toString() {
        return String.format(
                "('%s', '%s', %d, %d, %f, %f, %d, %d, %f, %f, %f)",
                id,
                (route == null ? "null" : route.getId()),
                outbound ? 0 : 1,
                currentStop,
                latitude,
                longitude,
                passengers,
                passengerCapacity,
                fuel,
                fuelCapacity,
                speed
        );
    }

    public String getId() {
        return id;
    }

    public Route getRoute() {
        return route;
    }

    public void setRoute(Route route) {
        this.route = route;
    }

    public boolean isOutbound() {
        return outbound;
    }

    public int getOutboundAsInt() {
        return outbound ? 1 : 0;
    }

    public void setOutbound(boolean outbound) {
        this.outbound = outbound;
    }

    public void setOutbound(int outbound) {
        this.outbound = outbound != 0;
    }

    public int getCurrentStopIndex() {
        return currentStop;
    }

    public void setCurrentStopIndex(int currentStop) {
        if (currentStop < -1) {
            throw new RuntimeException("can't set current stop to any value less than -1");
        } else if (currentStop != -1) {
            if (route == null) {
                throw new RuntimeException("can't set current stop to value other than -1 when the bus's route is null");
            } else if (currentStop >= route.getStops().size()) {
                throw new IndexOutOfBoundsException("can't set current stop to value greater than or equal to length of stops on bus's route");
            }
        }
        this.currentStop = currentStop;
    }

    /**
     * @return the stop the bus is at currently or the last stop the bus visited
     */
    public Stop getCurrentStop() {
        if (currentStop == -1 || route == null) {
            return null;
        }
        return route.getStops().get(currentStop);
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

    public int getPassengers() {
        return passengers;
    }

    public void setPassengers(int passengers) {
        this.passengers = passengers;
    }

    public int getPassengerCapacity() {
        return passengerCapacity;
    }

    public void setPassengerCapacity(int passengerCapacity) {
        this.passengerCapacity = passengerCapacity;
    }

    public double getFuel() {
        return fuel;
    }

    public void setFuel(double fuel) {
        this.fuel = fuel;
    }

    public double getFuelCapacity() {
        return fuelCapacity;
    }

    public void setFuelCapacity(double fuelCapacity) {
        this.fuelCapacity = fuelCapacity;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Bus)) return false;
        Bus bus = (Bus) o;
        return outbound == bus.outbound &&
                currentStop == bus.currentStop &&
                Double.compare(bus.latitude, latitude) == 0 &&
                Double.compare(bus.longitude, longitude) == 0 &&
                passengers == bus.passengers &&
                passengerCapacity == bus.passengerCapacity &&
                Double.compare(bus.fuel, fuel) == 0 &&
                Double.compare(bus.fuelCapacity, fuelCapacity) == 0 &&
                Double.compare(bus.speed, speed) == 0 &&
                Objects.equals(id, bus.id) &&
                Objects.equals(route, bus.route);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, route, outbound, currentStop, latitude, longitude, passengers, passengerCapacity, fuel, fuelCapacity, speed);
    }
}
