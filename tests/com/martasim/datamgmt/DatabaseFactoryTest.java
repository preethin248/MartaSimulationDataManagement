package com.martasim.datamgmt;

import com.martasim.models.Event;
import com.martasim.models.EventType;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

class DatabaseFactoryTest {
    @Test
    void create_empty_database() throws SQLException {
        File file = new File("MartaSimulation.db");
        boolean deleted = true;
        if (file.exists()) {
            deleted = file.delete();
        }

        assertTrue(deleted);
        assertFalse(file.exists());

        Database db = DatabaseFactory.createEmptyDatabase();

        assertTrue(file.exists());
        assertEquals(db.getAllBuses().size(), 0);
        assertEquals(db.getAllRoutes().size(), 0);
        assertEquals(db.getAllEvents().size(), 0);
        assertEquals(db.getAllStops().size(), 0);

        db.close();
    }

    @Test
    void create_empty_database_with_file() throws SQLException {
        File testfile = new File("TestFile.db");
        File file = new File("MartaSimulation.db");
        boolean deleted = true;
        if (file.exists()) {
            deleted = file.delete();
        }

        assertTrue(deleted);
        assertFalse(file.exists());

        deleted = true;
        if (testfile.exists()) {
            deleted = testfile.delete();
        }

        assertTrue(deleted);
        assertFalse(testfile.exists());

        Database db = DatabaseFactory.createEmptyDatabase(testfile);

        assertTrue(testfile.exists());
        assertEquals(db.getAllBuses().size(), 0);
        assertEquals(db.getAllRoutes().size(), 0);
        assertEquals(db.getAllEvents().size(), 0);
        assertEquals(db.getAllStops().size(), 0);

        db.close();
    }

    @Test
    void create_database_with_file() throws SQLException {
        File testfile = new File("TestFile.db");
        boolean deleted = true;
        if (testfile.exists()) {
            deleted = testfile.delete();
        }

        assertTrue(deleted);
        assertFalse(testfile.exists());

        Database db1 = DatabaseFactory.createEmptyDatabase(testfile);

        Event e = new Event("0", "0", 0, 0);
        db1.addEvent(e);

        assertEquals(db1.getAllEvents().size(), 1);

        db1.close();

        Database db2 = DatabaseFactory.createDatabaseFromDb(testfile);

        assertEquals(db2.getAllEvents().size(), 1);
        assertTrue(db2.getAllEvents().contains(e));

        db2.close();
    }
}
