package com.martasim.datamgmt;

import com.martasim.models.Bus;
import com.martasim.models.Event;
import com.martasim.models.Route;
import com.martasim.models.Stop;

import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

public interface Database {

    /**
     * Clears the database of all data.
     * @throws SQLException
     */
    void clear() throws SQLException;

    /**
     * Disconnects from the database.
     * Make sure to call this once you are done with the database
     * to make sure all changes and updates have been saved.
     * @throws SQLException
     */
    void close() throws SQLException;

    /**
     * Adds a bus to the database.
     *
     * @param bus that is to be added to the database
     * @throws SQLException
     */
    void addBus(Bus bus) throws SQLException;

    /**
     * Adds an event to the database.
     *
     * @param event that is to be added to the database
     * @throws SQLException
     */
    void addEvent(Event event) throws SQLException;

    /**
     * Adds a route to the database.
     *
     * @param route that is to be added to the database
     * @throws SQLException
     */
    void addRoute(Route route) throws SQLException;

    /**
     * Adds a stop to the database.
     *
     * @param stop that is to be added to the database
     * @throws SQLException
     */
    void addStop(Stop stop) throws SQLException;

    /**
     * Updates the route, location, passengers, passenger capacity, fuel, fuel capacity, and/or speed of a bus.
     *
     * @param bus Bus object that is updated to database
     * @throws SQLException
     */
    void updateBus(Bus bus) throws SQLException;

    /**
     * Updates only the time of event
     *
     * @param oldEvent Event object that is currently in database
     * @param newEvent Event object that is will replace oldEvent
     * @throws SQLException
     */
    void updateEvent(Event oldEvent, Event newEvent) throws SQLException;

    /**
     * Updates the name or number of a route. To add to a route's list of stops, use the extendRoute function instead.
     *
     * @param route Route object that is updated to database
     * @throws SQLException
     */
    void updateRoute(Route route) throws SQLException;

    /** Adds a stop to only the end of a route's list of stops
     *
     * @param route Route object that is getting a stop added to the end of its list of stops
     * @param stop Stop object that is getting added to the end of the route input
     * @throws SQLException
     */
    void extendRoute(Route route, Stop stop) throws SQLException;

    /**
     * Updates name, # of riders, latitude, and/or longitude of a stop
     *
     * @param stop Stop object that is updated to database
     * @throws SQLException
     */
    void updateStop(Stop stop) throws SQLException;

    /**
     * Gets a Bus from the database
     *
     * @param id of the bus being retrieved from the database
     * @return Bus object with corresponding id
     * @throws SQLException
     */
    Bus getBus(String id) throws SQLException;

    /**
     * Gets a Route from the database
     *
     * @param id of the route being retrieved from the database
     * @return Route object with corresponding id
     * @throws SQLException
     */
    Route getRoute(String id) throws SQLException;

    /**
     * Gets a Stop from the database
     *
     * @param id of the stop being retrieved from the database
     * @return Stop object with corresponding id
     * @throws SQLException
     */
    Stop getStop(String id) throws SQLException;

    /**
     * Gets all buses from the database
     *
     * @return Collection of type Bus containing all buses in database
     * @throws SQLException
     */
    Collection<Bus> getAllBuses() throws SQLException;

    /**
     * Gets all buses from the database with the corresponding route ID
     *
     * @param routeId of the bus's routes
     * @return Collection of type Bus containing all buses in database with the corresponding route ID
     * @throws SQLException
     */
    Collection<Bus> getAllBuses(String routeId) throws SQLException;

    /**
     * Gets all events from the database
     *
     * @return Collection of type Event containing all events in database
     * @throws SQLException
     */
    Collection<Event> getAllEvents() throws SQLException;

    /**
     * @param busId related to event
     * @return Collection of Events with corresponding busId
     * @throws SQLException
     */
    Collection<Event> getAllEventsWithBusId(String busId) throws SQLException;

    /**
     * @param stopId related to event
     * @return Collection of Events with corresponding stopId
     * @throws SQLException
     */
    Collection<Event> getAllEventsWithStopId(String stopId) throws SQLException;

    /**
     * @param arrivalTime of Bus
     * @return Collection of Events with buses arriving at a stop at the given arrivalTime
     * @throws SQLException
     */
    Collection<Event> getAllEventsWithArrivalTime(int arrivalTime) throws SQLException;

    /**
     * @param departureTime of Bus
     * @return Collection of Events with buses leaving a stop at the given departureTime
     * @throws SQLException
     */
    Collection<Event> getAllEventsWithDepartureTime(int departureTime) throws SQLException;

    /**
     * Gets all routes from the database
     *
     * @return Collection of type Route containing all routes in database
     * @throws SQLException
     */
    Collection<Route> getAllRoutes() throws SQLException;

    /**
     * Gets all Stops from the database
     *
     * @return Collection of type Stop containing all stops in database
     * @throws SQLException
     */
    Collection<Stop> getAllStops() throws SQLException;

    /**
     * Gets all stops from the database that are on a route with the corresponding route ID.
     * Stops will be ordered based on how they are ordered in the route
     *
     * @param routeId of the route that the stops are included on
     * @return List of type Stop including the ordered stops from the corresponding route
     * @throws SQLException
     */
    List<Stop> getAllStops(String routeId) throws SQLException;

    /**
     * Removes a bus from the database.
     *
     * @param bus that is removed from the database
     * @throws SQLException
     */
    void removeBus(Bus bus) throws SQLException;

    /**
     * Removes an event from the database.
     *
     * @param event that is removed from the database
     * @throws SQLException
     */
    void removeEvent(Event event) throws SQLException;

    /**
     * Removes a route from the database.
     *
     * @param route that is removed from the database
     * @throws SQLException
     */
    void removeRoute(Route route) throws SQLException;

    /**
     * Removes a stop from a given route
     *
     * @param route from which to remove the stop
     * @param stop  to remove
     * @throws SQLException
     */
    void removeFromRoute(Route route, Stop stop) throws SQLException;

    /**
     * Removes a stop from a given route
     *
     * @param routeId of route from which to remove the stop
     * @param stopId  of stop to remove
     * @throws SQLException
     */
    void removeFromRoute(String routeId, String stopId) throws SQLException;

    /**
     * Removes a stop from the database.
     *
     * @param stop that is removed from the database
     * @throws SQLException
     */
    void removeStop(Stop stop) throws SQLException;

    /**
     * Removes a stop from the database.
     *
     * @param stopId of stop to remove from the database
     * @throws SQLException
     */
    void removeStop(String stopId) throws SQLException;
}
