package com.martasim.datamgmt;

import com.martasim.models.Bus;
import com.martasim.models.Event;
import com.martasim.models.Route;
import com.martasim.models.Stop;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

class SQLiteDatabaseTest {

    SQLiteDatabase db;

    @BeforeEach
    void setup() throws SQLException {
        db = new SQLiteDatabase();
        db.clear();
    }

    @AfterEach
    void tearDown() throws SQLException {
        db.close();
    }

    @Test
    void add_route() throws SQLException {
        Collection<Route> routes = db.getAllRoutes();
        assertEquals(0, routes.size());

        Route route = new Route("0", "0", "route 0");
        db.addRoute(route);

        routes = db.getAllRoutes();

        assertEquals(1, routes.size());
        assertTrue(routes.contains(route));
    }

    @Test
    void add_bus() throws Exception {
        Collection<Bus> buses = db.getAllBuses();
        assertEquals(0, buses.size());

        Bus bus1 = new Bus("0", null, true, 0, 0, 0, 10, 0, 10, 0);
        db.addBus(bus1);

        buses = db.getAllBuses();

        assertEquals(1, buses.size());
        assertTrue(buses.contains(bus1));

        db.removeBus(bus1);
        buses = db.getAllBuses();
        assertEquals(0, buses.size());


        Route route = new Route("0", "0", "route 0");
        Bus bus2 = new Bus("1", route, true, 1, 1, 1, 11, 1, 11, 1);
        db.addRoute(route);
        db.addBus(bus2);
        buses = db.getAllBuses();

        assertEquals(1, buses.size());
        assertTrue(buses.contains(bus2));
    }

    @Test
    void add_stop() throws SQLException {
        Collection<Stop> stops = db.getAllStops();
        assertEquals(0, stops.size());

        Stop stop = new Stop ("0", "stop 0", 0, 0, 0);

        db.addStop(stop);

        stops = db.getAllStops();

        assertEquals(1, stops.size());
        assertTrue(stops.contains(stop));
    }

    @Test
    void add_event() throws SQLException {
        Collection<Event> events = db.getAllEvents();
        assertEquals(0, events.size());

        Event event = new Event("0", "0", 0, 0);

        db.addEvent(event);

        events = db.getAllEvents();

        assertEquals(1, events.size());
        assertTrue(events.contains(event));
    }

    @Test
    void get_route() throws SQLException {
        Route route = new Route("1", "0", "route 0");
        db.addRoute(route);

        assertEquals(route, db.getRoute("1"));
    }

    @Test
    void update_bus() throws Exception {
        Route routeA = new Route("1", "1", "Route 1");
        db.addRoute(routeA);
        Bus busA = new Bus("0", routeA, true, 1, 1, 2, 10, 10, 20, 40);

        db.addBus(busA);
        assertEquals(busA, db.getBus(busA.getId()));

        busA.setFuel(5);
        busA.setPassengers(5);
        assertNotEquals(busA, db.getBus(busA.getId()));

        db.updateBus(busA);
        assertEquals(busA, db.getBus(busA.getId()));
    }

    @Test
    void update_route() throws SQLException {
        Route routeA = new Route("1", "1", "Route 1");
        db.addRoute(routeA);
        assertEquals(routeA, db.getRoute(routeA.getId()));

        routeA.setShortName("2");
        routeA.setName("Route 2");
        assertNotEquals(routeA, db.getRoute(routeA.getId()));

        db.updateRoute(routeA);
        assertEquals(routeA, db.getRoute(routeA.getId()));
    }

    @Test
    void extend_route() throws SQLException {
        Route routeA = new Route("1", "1","Route 1");
        db.addRoute(routeA);
        assertEquals(0, db.getRoute(routeA.getId()).getStops().size());

        Stop stopA = new Stop("1", "Stop 1", 2, 10, 10);
        db.addStop(stopA);

        db.extendRoute(routeA, stopA);
        int numStopsInRoute = db.getRoute(routeA.getId()).getStops().size();
        assertEquals(1, numStopsInRoute);
        assertEquals(stopA, db.getRoute(routeA.getId()).getStops().get(numStopsInRoute-1));
    }

    @Test
    void update_stop() throws SQLException {
        Stop stopA = new Stop("1", "Stop 1", 3, 10, 10);
        db.addStop(stopA);
        assertEquals(stopA, db.getStop(stopA.getId()));

        stopA.setRiders(5);
        assertNotEquals(stopA, db.getStop(stopA.getId()));

        db.updateStop(stopA);
        assertEquals(stopA, db.getStop(stopA.getId()));

    }

    @Test
    void update_event() throws SQLException, CloneNotSupportedException {
        Event eventA = new Event("1", "1", 1, 3);
        db.addEvent(eventA);

        Event eventB = (Event)eventA.clone();
        eventB.setArrivalTime(10);


        db.updateEvent(eventA, eventB);
        assertEquals(1, db.getAllEvents().size());
        assertTrue(db.getAllEvents().contains(eventB));
    }


    @Test
    void remove_bus() throws Exception {
        Route R = new Route("0", "0", "0");
        Bus A = new Bus("0", R, true, 0, 0, 0, 0, 0, 0, 0);
        Bus B = new Bus("1", R, true, 1, 1, 1, 1, 1, 1, 1);

        db.addRoute(R);
        db.addBus(A);
        db.addBus(B);
        assertEquals(2, db.getAllBuses().size());

        db.removeBus(A);
        assertEquals(1, db.getAllBuses().size());
        assertFalse(db.getAllBuses().contains(A));
    }

    @Test
    void remove_route() throws SQLException {
        Route A = new Route("0", "0", "0");
        Route B = new Route("1", "1", "1");

        db.addRoute(A);
        db.addRoute(B);
        assertEquals(2, db.getAllRoutes().size());

        db.removeRoute(A);
        assertEquals(1, db.getAllRoutes().size());
        assertFalse(db.getAllRoutes().contains(A));
    }

    @Test
    void remove_stop() throws SQLException {
        Stop A = new Stop("0", "0", 0, 0, 0);
        Stop B = new Stop("1", "1", 1, 1, 1);
        Stop C = new Stop("2", "2", 2, 2, 2);

        db.addStop(A);
        db.addStop(B);
        db.addStop(C);

        Route R = new Route("0", "0", "0");
        db.addRoute(R);
        db.extendRoute(R, A);
        db.extendRoute(R, B);
        assertEquals(3, db.getAllStops().size());
        assertEquals(2, db.getAllStops("0").size());

        db.removeStop(A);
        assertEquals(2, db.getAllStops().size());
        assertFalse(db.getAllStops().contains(A));

        assertEquals(1, db.getAllStops("0").size());
        assertTrue(db.getAllStops("0").contains(B));


        db.extendRoute(R, C);
        assertEquals(2, db.getAllStops("0").size());
        assertEquals(B, db.getAllStops("0").get(0));
        assertEquals(C, db.getAllStops("0").get(1));
    }

    @Test
    void remove_event() throws SQLException {
        Event A = new Event("0", "0", 0, 0);
        Event B = new Event("1", "1", 1, 1);

        db.addEvent(A);
        db.addEvent(B);
        assertEquals(2, db.getAllEvents().size());

        db.removeEvent(A);
        assertEquals(1, db.getAllEvents().size());
        assertFalse(db.getAllEvents().contains(A));
    }

    @Test
    void remove_stop_from_route() throws SQLException {
        Stop[] stops = {
                new Stop("0", "0", 0, 0, 0),
                new Stop("1", "1", 1, 1, 1)
        };
        Route route = new Route("0", "0", "0");

        for (Stop stop : stops) {
            db.addStop(stop);
        }
        db.addRoute(route);
        db.extendRoute(route, stops[0]);
        db.extendRoute(route, stops[1]);
        assertEquals(2, db.getAllStops(route.getId()).size());

        db.removeFromRoute(route, stops[0]);
        assertEquals(1, db.getAllStops(route.getId()).size());
        assertTrue(db.getAllStops(route.getId()).contains(stops[1]));
        assertEquals(1, route.getStops().size());

        db.extendRoute(route, stops[0]);
        assertEquals(2, db.getAllStops(route.getId()).size());
        assertEquals(2, route.getStops().size());
    }

    @Test
    void read_route() throws SQLException {
        Route A = new Route("0", "0", "0");

        db.addRoute(A);

        assertEquals(A, db.getRoute(A.getId()));
    }

    @Test
    void read_bus() throws Exception {
        Route A = new Route("0", "0", "0");
        Bus B = new Bus("0", A, true, 0, 0, 0, 0, 0, 0, 0);

        db.addRoute(A);
        db.addBus(B);

        assertEquals(B, db.getBus(B.getId()));
    }

    @Test
    void read_stop() throws SQLException {
        Stop S = new Stop("0", "Stop 0", 0, 0, 0);

        db.addStop(S);

        assertEquals(S, db.getStop(S.getId()));
    }

    @Test
    void read_all_buses() throws Exception {
        Route A = new Route("0", "0", "0");
        Route B = new Route("1", "1", "1");
        Route C = new Route("2", "2", "2");
        Bus X = new Bus("0", A, true, 0, 0, 0, 0, 0, 0, 0);
        Bus Y = new Bus("1", B, true, 1, 1, 5, 5, 0, 0, 0);
        Bus Z = new Bus("2", C, true, 2, 2, 10, 10, 0, 0, 0);
        Collection<Bus> buses = new HashSet<>(Arrays.asList(X, Y, Z));

        db.addRoute(A);
        db.addRoute(B);
        db.addRoute(C);
        db.addBus(X);
        db.addBus(Y);
        db.addBus(Z);
        assertEquals(3, db.getAllBuses().size());

        assertEquals(buses, new HashSet<>(db.getAllBuses()));
    }

    @Test
    void read_all_buses_routeid() throws Exception {
        Route A = new Route("0", "0", "0");
        Route B = new Route("1", "1", "1");
        Bus X = new Bus("0", A, true, 0, 0, 0, 0, 0, 0, 0);
        Bus Y = new Bus("1", B, true, 1, 1, 5, 5, 0, 0, 0);
        Bus Z = new Bus("2", B, true, 2, 2, 10, 10, 0, 0, 0);
        Collection<Bus> buses = new HashSet<>(Arrays.asList(Y, Z));

        db.addRoute(A);
        db.addRoute(B);
        db.addBus(X);
        db.addBus(Y);
        db.addBus(Z);
        assertEquals(3, db.getAllBuses().size());

        assertEquals(buses, new HashSet<>(db.getAllBuses("1")));
    }

    @Test
    void read_all_routes() throws SQLException {
        Route A = new Route("0", "0", "0");
        Route B = new Route("1", "1", "1");
        Route C = new Route("2", "2", "2");
        Collection<Route> routes = new HashSet<>(Arrays.asList(A, B, C));

        db.addRoute(A);
        db.addRoute(B);
        db.addRoute(C);
        assertEquals(3, db.getAllRoutes().size());

        assertEquals(routes, new HashSet<>(db.getAllRoutes()));
    }

    @Test
    void read_all_stops() throws SQLException {
        Stop A = new Stop("0", "Stop 0", 0, 0, 0);
        Stop B = new Stop("1", "Stop 1", 5, 0, 0);
        Stop C = new Stop("2", "Stop 2", 10, 0, 0);
        Collection<Stop> stops = new HashSet<>(Arrays.asList(A, B, C));

        db.addStop(A);
        db.addStop(B);
        db.addStop(C);
        assertEquals(3, db.getAllStops().size());

        assertEquals(stops, new HashSet<>(db.getAllStops()));
    }

    @Test
    void read_all_events() throws SQLException {
        Event A = new Event("0", "0", 0, 0);
        Event B = new Event("1", "2", 3, 4);
        Event C = new Event("2", "5", 5, 5);
        Collection<Event> events = new HashSet<>(Arrays.asList(A, B, C));


        db.addEvent(A);
        db.addEvent(B);
        db.addEvent(C);
        assertEquals(3, db.getAllEvents().size());

        assertEquals(events, new HashSet<>(db.getAllEvents()));
    }

    @Test
    void read_all_events_arrival_time() throws SQLException {
        Event A = new Event("0", "0", 0, 0);
        Event B = new Event("1", "2", 3, 4);
        Event C = new Event("2", "5", 3, 5);


        db.addEvent(A);
        db.addEvent(B);
        db.addEvent(C);
        assertEquals(3, db.getAllEvents().size());

        assertEquals(new HashSet<>(Arrays.asList(B, C)), new HashSet<>(db.getAllEventsWithArrivalTime(3)));
    }
}
