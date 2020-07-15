package com.martasim.datamgmt;

import com.martasim.models.Bus;
import com.martasim.models.DayOfTheWeek;
import com.martasim.models.Route;
import com.martasim.models.Stop;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.zip.ZipFile;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ParserTest {
    static private Database db;

    @BeforeAll
    static void parse_data() throws SQLException, IOException {
        ZipFile zip = new ZipFile("./test-resources/gtfs022118.zip");
        db = DatabaseFactory.createDatabaseFromGtfs(zip, DayOfTheWeek.MONDAY);
    }

    private HashSet<String> getServiceIdsFromDay(DayOfTheWeek dayOfTheWeek, InputStream inputStream) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));

        //initialize hash map with the first row of the file
        HashMap<String, Integer> map = new HashMap<>();
        String[] labels = br.readLine().split(",");
        for (int i = 0; i < labels.length; i++) {
            map.put(labels[i], i);
        }

        String dayOfService = "";

        switch(dayOfTheWeek) {
            case MONDAY:
                dayOfService = "monday";
                break;
            case TUESDAY:
                dayOfService = "tuesday";
                break;
            case WEDNESDAY:
                dayOfService = "wednesday";
                break;
            case THURSDAY:
                dayOfService = "thursday";
                break;

            case FRIDAY:
                dayOfService = "friday";
                break;
            case SATURDAY:
                dayOfService = "saturday";
                break;
            case SUNDAY:
                dayOfService = "sunday";
                break;
        }

        HashSet<String> serviceIds = new HashSet<>();

        String line;
        String serviceId;
        while ((line = br.readLine()) != null && !line.isEmpty()) {
            String st[] = (line + ", ").split(",");

            if (st[map.get(dayOfService)].equals("1")) {
                serviceId = st[map.get("service_id")];
                serviceIds.add(serviceId);
            }

        }

        br.close();

        return serviceIds;
    }

    @Test
    void parse_serviceIds() throws IOException {
        ZipFile zipFile = new ZipFile("./test-resources/gtfs022118.zip");
        HashSet<String> serviceIds1 = getServiceIdsFromDay(DayOfTheWeek.MONDAY, zipFile.getInputStream(zipFile.getEntry("gtfs022118/calendar.txt")));
        HashSet<String> testServiceIds1 = new HashSet<>();
        testServiceIds1.add("5");
        assertEquals(testServiceIds1, serviceIds1);

        HashSet<String> serviceIds2 = getServiceIdsFromDay(DayOfTheWeek.SATURDAY, zipFile.getInputStream(zipFile.getEntry("gtfs022118/calendar.txt")));
        HashSet<String> testServiceIds2 = new HashSet<>();
        testServiceIds2.add("3");
        assertEquals(testServiceIds2, serviceIds2);

        HashSet<String> serviceIds3 = getServiceIdsFromDay(DayOfTheWeek.SUNDAY, zipFile.getInputStream(zipFile.getEntry("gtfs022118/calendar.txt")));
        HashSet<String> testServiceIds3 = new HashSet<>();
        testServiceIds3.add("4");

        assertEquals(testServiceIds3, serviceIds3);
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
        //Route routeB = db.getRoute("7682");
        //Route routeC = db.getRoute("8766");

        assertEquals(routeA.getStops().size(), 58);
       // assertEquals(routeB.getStops().size(), 39);
       // assertEquals(routeC.getStops().size(), 15);
    }

    @Test
    void parse_bus() throws SQLException {
        Bus firstBus = new Bus("6053989", db.getRoute("7638"), false, -1, 33.921202,
                -84.344649, 0, 50, 100, 100, 0);
        Bus midBus = new Bus("6043732", db.getRoute("7688"), false, -1, 33.771889,
                -84.386959, 0, 50, 100, 100, 0);
        Bus lastBus = new Bus("2475833", db.getRoute("8766"), false, -1, 33.753612,
                -84.391008, 0, 50, 100, 100, 0);

        assertEquals(firstBus, db.getBus("6053989"));
        assertEquals(midBus, db.getBus("6043732"));
        assertEquals(lastBus, db.getBus("2475833"));
//        assertEquals(24596, db.getAllBuses().size());
    }
}
