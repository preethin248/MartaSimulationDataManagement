package com.martasim.datamgmt;

import com.martasim.models.DayOfTheWeek;

import java.io.File;
import java.sql.SQLException;
import java.util.zip.ZipFile;

public class DatabaseFactory {

    /**
     * @return a new Database which is located in the MartaDatabase.db file
     * @throws SQLException
     */
    public static Database createEmptyDatabase() throws SQLException {
        Database database = new SQLiteDatabase();
        database.clear();
        return database;
    }

    /**
     * @param file in which to create a new empty Database
     * @return a new empty Database using the given file
     * @throws SQLException
     */
    public static Database createEmptyDatabase(File file) throws SQLException {
        Database database = new SQLiteDatabase(file);
        database.clear();
        return database;
    }

    /**
     * @param file that contains the Database
     * @return the Database based on the given file
     * @throws SQLException
     */
    public static Database createDatabaseFromDb(File file) throws SQLException {
        return new SQLiteDatabase(file);
    }

    /**
     * @param zipFile containing GTFS data
     * @return a Database located in the MartaDatabase.db file that is populated with the GTFS data
     * @throws SQLException
     */
    public static Database createDatabaseFromGtfs(ZipFile zipFile, DayOfTheWeek dayOfTheWeek) throws SQLException {
        Database database = createEmptyDatabase();
        (new GtfsParser(database, zipFile)).parse(dayOfTheWeek);
        return database;
    }

    public static Database createDatabaseFromGtfs(ZipFile zipFile, String dayOfTheWeek) throws SQLException {
        String editedDayOfTheWeek = dayOfTheWeek.trim().toLowerCase();
        switch (editedDayOfTheWeek) {
            case "monday":
                return createDatabaseFromGtfs(zipFile, DayOfTheWeek.MONDAY);
            case "tuesday":
                return createDatabaseFromGtfs(zipFile, DayOfTheWeek.TUESDAY);
            case "wednesday":
                return createDatabaseFromGtfs(zipFile, DayOfTheWeek.WEDNESDAY);
            case "thursday":
                return createDatabaseFromGtfs(zipFile, DayOfTheWeek.THURSDAY);
            case "friday":
                return createDatabaseFromGtfs(zipFile, DayOfTheWeek.FRIDAY);
            case "saturday":
                return createDatabaseFromGtfs(zipFile, DayOfTheWeek.SATURDAY);
            case "sunday":
                return createDatabaseFromGtfs(zipFile, DayOfTheWeek.SUNDAY);
        }
        throw new RuntimeException(dayOfTheWeek + " is not a valid day of the week.");
    }
}
