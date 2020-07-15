package com.martasim.datamgmt;

import com.martasim.models.Bus;
import com.martasim.models.Route;
import com.martasim.models.Stop;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.sql.SQLException;
import java.util.zip.ZipFile;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ParserTest {
    static private Database db;

    @BeforeAll
    static void parse_data() throws SQLException, IOException {
        ZipFile zip = new ZipFile("./test-resources/gtfs022118.zip");
        db = DatabaseFactory.createDatabaseFromGtfs(zip);
    }

    @Test
    void parse_route() throws SQLException {
        Route route = new Route("7634", "1", "Centennial Oly. Park/Coronet Way");
        Route last_route = new Route("8747", "RED", "RED-North South North Springs Line");

        assertEquals(route.getId(), db.getRoute("7634").getId());
        assertEquals(route.getName(), db.getRoute("7634").getName());
        assertEquals(route.getShortName(), db.getRoute("7634").getShortName());
        assertEquals(52, db.getRoute("7634").getStops().size());
        assertEquals(110, db.getAllRoutes().size());
    }

    @Test
    void parse_stop() throws SQLException {
        Stop stop = new Stop("100004", "JOSEPH E LOWERY BLVD@BECKWITH ST SW", 0, 33.752636, -84.417759);
        Stop mid_stop = new Stop("900788", "FULTON INDUSTRIAL BLVD @ SELIG DR", 0, 33.747613, -84.554351);
        Stop quote_stop = new Stop("213316", "PEACHTREE ST SW @ MARTIN L KING,JR DR", 0, 33.751957, -84.392124);

        assertEquals(stop, db.getStop("100004"));
        assertEquals(mid_stop, db.getStop("900788"));
        assertEquals(quote_stop, db.getStop("213316"));
        assertEquals(9172, db.getAllStops().size());
    }

    @Test
    void parse_stopToRoute() throws IOException, SQLException {
        Route routeA = db.getRoute("7687");
        Route routeB = db.getRoute("7682");
        Route routeC = db.getRoute("8766");

        assertEquals(routeA.getStops().size(), 55);
        assertEquals(routeB.getStops().size(), 39);
        assertEquals(routeC.getStops().size(), 15);
    }

    @Test
    void parse_bus() throws SQLException {
        Bus firstBus = new Bus("6062219", db.getRoute("7634"), true, -1, 33.771889,
                -84.386959, 0, 50, 100, 100, 0);
        Bus midBus = new Bus("6045456", db.getRoute("7735"), false, -1, 33.80365,
                -84.414991, 0, 50, 100, 100, 0);
        Bus lastBus = new Bus("2475833", db.getRoute("8766"), false, -1, 33.753612,
                -84.391008, 0, 50, 100, 100, 0);

        assertEquals(firstBus, db.getBus("6062219"));
        assertEquals(midBus, db.getBus("6045456"));
        assertEquals(lastBus, db.getBus("2475833"));
//        assertEquals(24596, db.getAllBuses().size());
    }
}
