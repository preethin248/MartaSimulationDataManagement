package com.martasim.datamgmt;

import com.martasim.models.Route;
import com.martasim.models.DayOfTheWeek;
import com.sun.xml.internal.ws.api.ha.StickyFeature;
import javafx.util.Pair;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

class GtfsParser extends Parser {

    Map<String, ZipEntry> zipEntries = new HashMap<>();

    GtfsParser(Database database, ZipFile zipFile) {
        super(database, zipFile);

        Enumeration<? extends ZipEntry> entries = zipFile.entries();
        while (entries.hasMoreElements()) {
            ZipEntry entry = entries.nextElement();
            String name = entry.getName();
            if (name.startsWith("__MACOSX"))
                continue;

            zipEntries.put(name.substring(name.lastIndexOf('/') + 1), entry);
        }
    }

    private InputStream getInputStream(String fileName) throws IOException {
        return zipFile.getInputStream(zipEntries.get(fileName));
    }

    @Override
    public void parse(DayOfTheWeek dayOfTheWeek) {
        try {

            System.out.println(zipFile.getName());
            HashSet<String> serviceIds = getServiceIdsFromDay(dayOfTheWeek, getInputStream("calendar.txt"));
            System.out.println("Parsing Route");

            HashSet<String> routeIds = getRouteIdsByServiceIds(getInputStream("trips.txt"), serviceIds);
            addRoutes(getInputStream("routes.txt"), routeIds);

            System.out.println("Finished Routes, Parsing Stops");
            addStops(getInputStream("stops.txt"));
            System.out.println("Finished Stops, Parsing Buses");
            addBuses(zipFile.getInputStream(zipFile.getEntry("gtfs022118/trips.txt")), serviceIds);
            System.out.println("Finished Buses, Parsing StopsToRoute");

            HashSet<String> busIds = getBusIdsByServiceIds(getInputStream("trips.txt"), serviceIds);
            addStopsToRoutes(getInputStream("stop_times.txt"), busIds);

            System.out.println("Finished StopsToRoutes, Parsing Events");
            addEvents(getInputStream("stop_times.txt"));
            System.out.println("Finished Events");
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
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

    private HashSet<String> getRouteIdsByServiceIds(InputStream inputStream, HashSet<String> serviceIds) throws IOException{
        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));

        //initialize hash map with the first row of the file
        HashMap<String, Integer> map = new HashMap<>();
        String[] labels = br.readLine().split(",");
        for (int i = 0; i < labels.length; i++) {
            map.put(labels[i], i);
        }

        HashSet<String> routeIds = new HashSet<>();

        String line;
        while ((line = br.readLine()) != null) {
            String[] st = (line + ", ").split(",");

            String serviceId = st[map.get("service_id")];
            if (serviceIds.contains(serviceId)) {
                routeIds.add(st[map.get("route_id")]);
            }
        }

        br.close();

        return routeIds;
    }

    private HashSet<String> getBusIdsByServiceIds(InputStream inputStream, HashSet<String> serviceIds) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));

        //initialize hash map with the first row of the file
        HashMap<String, Integer> map = new HashMap<>();
        String[] labels = br.readLine().split(",");
        for (int i = 0; i < labels.length; i++) {
            map.put(labels[i], i);
        }

        HashSet<String> busIds = new HashSet<>();

        String line;
        while ((line = br.readLine()) != null) {
            String[] st = (line + ", ").split(",");

            String serviceId = st[map.get("service_id")];
            if (serviceIds.contains(serviceId)) {
                busIds.add(st[map.get("trip_id")]);
            }
        }

        br.close();

        return busIds;
    }

    private void addRoutes(InputStream inputStream, HashSet<String> routeIds) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));

        //initialize hash map with the first row of the file
        HashMap<String, String> map = new HashMap<>();
        String[] labels = br.readLine().split(",");
        for (String label : labels) {
            map.put(label, "");
        }

        String line;
        while ((line = br.readLine()) != null) {
            String[] st = (line + ", ").split(",");

            try {
                for (int i = 0; i < labels.length; i++) {
                    map.replace(labels[i], st[i]);
                }

                String routeId = map.get("route_id");
                if (routeIds.contains(routeId)) {
                    database.addRoute(new Route(routeId, map.get("route_short_name"), map.get("route_long_name")));
                }
            } catch (SQLException sqlException) {
                sqlException.printStackTrace();
            }
        }

        br.close();
    }

    private void addStops(InputStream inputStream) throws IOException{
        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));

        //initialize hash map with the first row of the file
        HashMap<String, String> map = new HashMap<>();
        String[] labels = br.readLine().split(",");
        for (String label: labels) {
            map.put(label, "");
        }

        StringBuilder sb = null;
        int counter = 0;
        String line;
        while ((line = br.readLine()) != null && !line.isEmpty()) {
            String[] st = (line + ", ").split(",");
            int st_index = 0;
            //update strings including apostrophes to work with SQL INSERT command
            for (String label: labels) {
                if (st[st_index].startsWith("\"")) {
                    String str = st[st_index].concat("," + st[st_index + 1]);
                    str = str.replace("\"", "");
                    map.replace(label, str);
                    st_index++;
                } else {
                    map.replace(label, st[st_index]);
                }
                st_index++;
            }
            if (counter % 10000 == 0) {
                if (counter > 0) {
                    try {
                        ((SQLiteDatabase) database).executeUpdate(sb.toString());
                    } catch (SQLException sqlException) {
                        sqlException.printStackTrace();
                    }
                }
                sb = new StringBuilder("INSERT INTO stop values ");
            } else {
                sb.append(',');
            }
            counter++;

            String stopId = map.get("stop_id");
            String stopName = map.get("stop_name").replace("'", "''");
            double lat = Double.parseDouble(map.get("stop_lat"));
            double lon = Double.parseDouble(map.get("stop_lon"));
            sb.append(String.format(
                    "('%s', '%s', %d, %d, %f, %f)",
                    stopId,
                    stopName,
                    0,
                    0,
                    lat,
                    lon
            ));
        }

        if (counter % 10000 > 0) {
            try {
                ((SQLiteDatabase) database).executeUpdate(sb.toString());
            } catch (SQLException sqlException) {
                sqlException.printStackTrace();
            }
        }

        br.close();
    }

    private void addBuses(InputStream inputStream, HashSet<String> serviceIds) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));

        Map<String, Pair<Double, Double>> shapeStartingLocations =
                getShapeStartingLocations(getInputStream("shapes.txt"));

        HashMap<String, Integer> map = new HashMap<>();
        String[] labels = br.readLine().split(",");
        for (int i = 0; i < labels.length; i++) {
            map.put(labels[i], i);
        }

        StringBuilder sb = null;
        int counter = 0;
        String line;
        while ((line = br.readLine()) != null && !line.isEmpty()) {
            String[] st = (line + ", ").split(",");
            String routeId = st[map.get("route_id")];
            String serviceId = st[map.get("service_id")];
            String busId = st[map.get("trip_id")];
            boolean outbound = st[map.get("direction_id")].trim().charAt(0) == '0';
            String shapeId = st[map.get("shape_id")];

            if (!serviceIds.contains(serviceId)) {
                continue;
            }

            if (counter % 10000 == 0) {
                if (counter > 0) {
                    try {
                        ((SQLiteDatabase) database).executeUpdate(sb.toString());
                    } catch (SQLException sqlException) {
                        sqlException.printStackTrace();
                    }
                }
                sb = new StringBuilder("INSERT INTO bus values ");
            } else {
                sb.append(',');
            }
            counter++;

            sb.append(String.format(
                    "('%s', '%s', %d, %d, %f, %f, %d, %d, %f, %f, %f)",
                    busId,
                    routeId,
                    outbound ? 0 : 1,
                    -1,
                    shapeStartingLocations.get(shapeId).getKey(),
                    shapeStartingLocations.get(shapeId).getValue(),
                    0,
                    50,
                    100.0,
                    100.0,
                    0.0
            ));
        }
        br.close();

        if (counter % 10000 > 0) {
            try {
                ((SQLiteDatabase) database).executeUpdate(sb.toString());
            } catch (SQLException sqlException) {
                sqlException.printStackTrace();
            }
        }
    }

    public Map<String, Pair<Double, Double>> getShapeStartingLocations(InputStream inputStream) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));

        Map<String, Pair<Double, Double>> shapeStartingLocations = new HashMap<>();
        Map<String, Integer> earliestPointForShape = new HashMap<>();

        HashMap<String, Integer> map = new HashMap<>();
        String[] labels = br.readLine().split(",");
        for (int i = 0; i < labels.length; i++) {
            map.put(labels[i], i);
        }

        String line;
        while ((line = br.readLine()) != null && !line.isEmpty()) {
            String[] st = (line + ", ").split(",");
            String shapeId = st[map.get("shape_id")];
            double lat = Double.parseDouble(st[map.get("shape_pt_lat")]);
            double lon = Double.parseDouble(st[map.get("shape_pt_lon")]);
            int seq = Integer.parseInt(st[map.get("shape_pt_sequence")]);

            if (seq <= earliestPointForShape.getOrDefault(shapeId, Integer.MAX_VALUE)) {
                shapeStartingLocations.put(shapeId, new Pair<>(lat, lon));
                earliestPointForShape.put(shapeId, seq);
            }
        }

        br.close();

        return shapeStartingLocations;
    }

    /*
    You can delete this method after addBuses is done and move the bus hashmap to addBuses.
    I needed to loop through trips.txt to grab the routeId and directionId of the trip's connected to the stop
    but the addBuses function wasn't done yet so I used this function.
     */
    private HashMap<String, String[]> createBusMap(InputStream inputStream, HashSet<String> busIds) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));

        HashMap<String, String[]> busMap = new HashMap<>();

        HashMap<String, Integer> map = new HashMap<>();
        String[] labels = br.readLine().split(",");
        for (int i = 0; i < labels.length; i++) {
            map.put(labels[i], i);
        }

        String line;
        String routeId;
        String busId;
        String outbound; // 0 = outbound, 1 = inbound

        while ((line = br.readLine()) != null && !line.isEmpty()) {
            String[] st = (line + ", ").split(",");
            routeId = st[map.get("route_id")];
            busId = st[map.get("trip_id")];

            if (!busIds.contains(busId)) {
                continue;
            }

            outbound = st[map.get("direction_id")].trim();

            busMap.put(busId, new String[] {routeId, outbound});
        }

        br.close();

        return busMap;
    }

    private void addStopsToRoutes(InputStream inputStream, HashSet<String> busIds) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
        HashMap<String, Integer> map = new HashMap<>();
        String[] labels = br.readLine().split(",");
        for (int i = 0; i < labels.length; i++) {
            map.put(labels[i], i);
        }

        String currentBusId = null;
        Map<Integer, String> stopSequenceToId = new TreeMap<>();
        HashMap<String, String[]> busMap = createBusMap(getInputStream("trips.txt"), busIds);
        HashSet<String> completedRoutes = new HashSet<>();

        String line;
        while ((line = br.readLine()) != null && !line.isEmpty()) {

            String[] st = (line + ", ").split(",");
            String busId = st[map.get("trip_id")];

            if (!busIds.contains(busId)) {
                continue;
            }

            String stopId = st[map.get("stop_id")];
            int stopIndex = Integer.parseInt(st[map.get("stop_sequence")]);


            if (currentBusId == null) { //first line
                currentBusId = busId;
            }

            if (!busId.equals(currentBusId)) { //new trip
                //finished going through current trip's stops, time to add to database
                String routeId = busMap.get(currentBusId)[0];
                String outbound = busMap.get(currentBusId)[1];
                if (!completedRoutes.contains(routeId)) {
                    addStopsToRoutesInDatabase(new ArrayList<>(stopSequenceToId.values()), routeId, outbound);
                    completedRoutes.add(routeId);
                }

                //for new trip
                currentBusId = busId;
                stopSequenceToId = new TreeMap<>();
            }

            stopSequenceToId.put(stopIndex, stopId);

        }

        br.close();

        if (!stopSequenceToId.isEmpty()) {
            String routeId = busMap.get(currentBusId)[0];
            String outbound = busMap.get(currentBusId)[1];
            if (!completedRoutes.contains(routeId)) {
                addStopsToRoutesInDatabase(new ArrayList<>(stopSequenceToId.values()), routeId, outbound);
            }
        }
    }

    private void addStopsToRoutesInDatabase(ArrayList<String> stopIdList, String routeId, String outbound) {

        StringBuilder sb = new StringBuilder("INSERT INTO routeToStop values ");
        String currentStopId;
        int stopSequence;
        if (outbound.equals("0")) { //0 = outbound, retain original stop sequence
            for (int i = 0; i < stopIdList.size(); i++) {

                currentStopId = stopIdList.get(i);
                stopSequence = i;
                sb.append(String.format(
                        "('%s', '%s', %d)",
                        routeId,
                        currentStopId,
                        stopSequence
                ));

                if (i < stopIdList.size() - 1) {
                    sb.append(',');
                }
            }
        } else { //1 = inbound, want to reverse the stop sequence
            for (int i = stopIdList.size() - 1; i >= 0; i--) {
                currentStopId = stopIdList.get(i);
                stopSequence = stopIdList.size() - i;
                sb.append(String.format(
                        "('%s', '%s', %d)",
                        routeId,
                        currentStopId,
                        stopSequence
                ));

                if (i > 0) {
                    sb.append(',');
                }

            }
        }

        //insert all the rows for a bus/trip into routeToStop
        try {
            ((SQLiteDatabase) database).executeUpdate(sb.toString());
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
    }

    private void addEvents(InputStream inputStream) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));

        HashMap<String, Integer> map = new HashMap<>();
        String[] labels = br.readLine().split(",");
        for (int i = 0; i < labels.length; i++) {
            map.put(labels[i], i);
        }

        StringBuilder sb = null;
        int counter = 0;
        String line;
        while ((line = br.readLine()) != null && !line.isEmpty()) {
            if (counter % 10000 == 0) {
                if (counter > 0) {
                    try {
                        ((SQLiteDatabase) database).executeUpdate(sb.toString());
                    } catch (SQLException sqlException) {
                        sqlException.printStackTrace();
                    }
                }
                sb = new StringBuilder("INSERT INTO event values ");
            } else {
                sb.append(',');
            }
            counter++;

            String[] st = (line + ", ").split(",");
            String busId = st[map.get("trip_id")];
            String stopId = st[map.get("stop_id")];
            int arrivalTime = getLogicalTimeFromTimeString(st[map.get("arrival_time")]);
            int departureTime = getLogicalTimeFromTimeString(st[map.get("departure_time")]);
            sb.append(String.format(
                    "('%s', '%s', %d, %d)",
                    busId,
                    stopId,
                    arrivalTime,
                    departureTime
            ));
        }
        br.close();

        if (counter % 10000 > 0) {
            try {
                ((SQLiteDatabase) database).executeUpdate(sb.toString());
            } catch (SQLException sqlException) {
                sqlException.printStackTrace();
            }
        }
    }

    /**
     * @param timeString in the format HH:MM:SS
     * @return the number of seconds since 00:00:00
     */
    int getLogicalTimeFromTimeString(String timeString) {
        String[] time = timeString.split(":");
        int hours = Integer.parseInt(time[0]);
        int min = Integer.parseInt(time[0]);
        int sec = Integer.parseInt(time[0]);

        return sec + (60 * min) + (60 * 60 * hours);
    }
}
