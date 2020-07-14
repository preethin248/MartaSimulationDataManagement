package com.martasim.datamgmt;

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
        ZipFile zip = new ZipFile("/Users/lingtham/Desktop/CS3300/MartaSimDataMgmt/src/com/martasim/data/gtfs022118.zip");
        db = DatabaseFactory.createDatabaseFromGtfs(zip);
    }

    @Test
    void parse_route() throws SQLException {
        Route route = new Route("7634", "1", "Centennial Oly. Park/Coronet Way");
        Route last_route = new Route("8747", "RED", "RED-North South North Springs Line");

        assertEquals(route, db.getRoute("7634"));
        assertEquals(last_route, db.getRoute("8747"));
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
        assertEquals(routeC.getStops().size(), 6);
    }
}
