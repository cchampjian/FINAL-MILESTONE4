package animalsanctuarysystem;

import java.sql.*;

public class Database {
    private static final String URL = "jdbc:sqlite:animal_sanctuary.db";

    public static Connection connect() {
        try {
            return DriverManager.getConnection(URL);
        } catch (SQLException e) {
            System.out.println("Connection Error: " + e.getMessage());
            return null;
        }
    }

    public static void setup() {
        try (Connection conn = connect(); Statement stmt = conn.createStatement()) {
            stmt.execute("CREATE TABLE IF NOT EXISTS users (" +
                         "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                         "name TEXT, email TEXT, password TEXT, role TEXT)");

            stmt.execute("CREATE TABLE IF NOT EXISTS bookings (" +
                         "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                         "user_id INTEGER, category TEXT, date TEXT, " +
                         "start_time TEXT, end_time TEXT, pax INTEGER, status TEXT)");

            // Migration: add start_time and end_time if old table exists without them
            try { stmt.execute("ALTER TABLE bookings ADD COLUMN start_time TEXT"); } catch (SQLException ignored) {}
            try { stmt.execute("ALTER TABLE bookings ADD COLUMN end_time TEXT"); } catch (SQLException ignored) {}

            stmt.execute("CREATE TABLE IF NOT EXISTS donations (" +
                         "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                         "user_id INTEGER, artifact TEXT, description TEXT, status TEXT)");

            // gcash_num column (consistent across all files)
            stmt.execute("CREATE TABLE IF NOT EXISTS memberships (" +
                         "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                         "user_id INTEGER, type TEXT, gcash_num TEXT, status TEXT)");

            stmt.execute("CREATE TABLE IF NOT EXISTS payments (" +
                         "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                         "user_id INTEGER, payment_for TEXT, gcash_ref TEXT, " +
                         "original_amount REAL, total_amount REAL, status TEXT)");

            // Auto-create admin account
            stmt.execute("INSERT OR IGNORE INTO users (id, name, email, password, role) " +
                         "VALUES (1, 'Admin', 'admin@gmail.com', 'admin123', 'admin')");

        } catch (SQLException e) {
            System.out.println("Setup Error: " + e.getMessage());
        }
    }
}