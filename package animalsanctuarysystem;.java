package animalsanctuarysystem;

import java.sql.*;
import java.util.InputMismatchException;
import java.util.Scanner;

public class AnimalSanctuarySystem {

    static Scanner sc = new Scanner(System.in);
    static User loggedInUser = null;

    // =========================================================
    // READ INTEGER
    // =========================================================
    static int readInt(String prompt) {

        while (true) {

            System.out.print(prompt);

            try {
                return sc.nextInt();
            }
            catch (InputMismatchException e) {

                sc.next();
                System.out.println("Invalid input.");
            }
        }
    }

    // =========================================================
    // READ LINE
    // =========================================================
    static String readLine(String prompt) {

        while (true) {

            System.out.print(prompt);

            String val = sc.nextLine().trim();

            if (!val.isEmpty()) {
                return val;
            }

            System.out.println("Input cannot be empty.");
        }
    }

    // =========================================================
    // READ TOKEN
    // =========================================================
    static String readToken(String prompt) {

        while (true) {

            System.out.print(prompt);

            String val = sc.next().trim();

            if (!val.isEmpty()) {
                return val;
            }

            System.out.println("Input cannot be empty.");
        }
    }

    // =========================================================
    // MAIN
    // =========================================================
    public static void main(String[] args) {

        Database.setup();

        while (true) {

            System.out.println("\n======================================");
            System.out.println(" ANIMAL SANCTUARY & REHABILITATION ");
            System.out.println("======================================");

            System.out.println("[1] Register");
            System.out.println("[2] Login");
            System.out.println("[3] Exit");

            int choice = readInt("Choice: ");

            if (choice == 1) {
                register();
            }
            else if (choice == 2) {

                login();

                if (loggedInUser != null) {

                    if (loggedInUser.role.equalsIgnoreCase("admin")) {
                        adminDashboard();
                    }
                    else {
                        userDashboard();
                    }
                }
            }
            else if (choice == 3) {

                System.out.println("Thank you!");
                break;
            }
            else {
                System.out.println("Invalid choice.");
            }
        }
    }

    // =========================================================
    // REGISTER
    // =========================================================
    static void register() {

        System.out.println("\n=== REGISTER ===");

        sc.nextLine();

        String name = readLine("Full Name: ");
        String email = readToken("Email: ");
        String password = readToken("Password: ");

        String sql =
                "INSERT INTO users(name,email,password,role) " +
                "VALUES(?,?,?,'user')";

        try (
                Connection conn = Database.connect();
                PreparedStatement pstmt = conn.prepareStatement(sql)
        ) {

            pstmt.setString(1, name);
            pstmt.setString(2, email);
            pstmt.setString(3, password);

            pstmt.executeUpdate();

            System.out.println("Registration successful!");

        }
        catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // =========================================================
    // LOGIN
    // =========================================================
    static void login() {

        System.out.println("\n=== LOGIN ===");

        String email = readToken("Email: ");
        String password = readToken("Password: ");

        String sql =
                "SELECT * FROM users " +
                "WHERE email = ? AND password = ?";

        try (
                Connection conn = Database.connect();
                PreparedStatement pstmt = conn.prepareStatement(sql)
        ) {

            pstmt.setString(1, email);
            pstmt.setString(2, password);

            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {

                loggedInUser = new User(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("role")
                );

                System.out.println("Welcome " + loggedInUser.name);

            }
            else {
                System.out.println("Invalid credentials.");
            }

        }
        catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // =========================================================
    // USER DASHBOARD
    // =========================================================
    static void userDashboard() {

        while (loggedInUser != null) {

            System.out.println("\n======================================");
            System.out.println(" USER DASHBOARD ");
            System.out.println("======================================");

            System.out.println("[1] View Animals");
            System.out.println("[2] My Bookings");
            System.out.println("[3] Membership");
            System.out.println("[4] Payment History");
            System.out.println("[5] Logout");

            int choice = readInt("Choice: ");

            if (choice == 1) {
                animalCatalog();
            }
            else if (choice == 2) {
                myBookings();
            }
            else if (choice == 3) {
                membershipModule();
            }
            else if (choice == 4) {
                paymentHistory();
            }
            else if (choice == 5) {
                loggedInUser = null;
            }
            else {
                System.out.println("Invalid choice.");
            }
        }
    }

    // =========================================================
    // ANIMAL BOOKING
    // =========================================================
    static void animalCatalog() {

        System.out.println("\n=== ANIMAL CATEGORIES ===");

        System.out.println("[1] Mammals");
        System.out.println("[2] Birds");
        System.out.println("[3] Marine Animals");
        System.out.println("[4] Reptiles");
        System.out.println("[5] Amphibians");
        System.out.println("[6] Exotic Animals");

        int c = readInt("Category: ");

        String[] categories = {
                "Mammals",
                "Birds",
                "Marine Animals",
                "Reptiles",
                "Amphibians",
                "Exotic Animals"
        };

        if (c < 1 || c > 6) {
            System.out.println("Invalid category.");
            return;
        }

        sc.nextLine();

        String date = readLine("Date (YYYY-MM-DD): ");
        String start = readLine("Start Time: ");
        String end = readLine("End Time: ");

        int pax = readInt("Pax: ");

        String sql =
                "INSERT INTO bookings(user_id,category,date,start_time,end_time,pax,status) " +
                "VALUES(?,?,?,?,?,?,'PENDING')";

        try (
                Connection conn = Database.connect();
                PreparedStatement pstmt = conn.prepareStatement(sql)
        ) {

            pstmt.setInt(1, loggedInUser.id);
            pstmt.setString(2, categories[c - 1]);
            pstmt.setString(3, date);
            pstmt.setString(4, start);
            pstmt.setString(5, end);
            pstmt.setInt(6, pax);

            pstmt.executeUpdate();

            System.out.println("Booking submitted!");

        }
        catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // =========================================================
    // MY BOOKINGS
    // =========================================================
    static void myBookings() {

        String sql =
                "SELECT * FROM bookings WHERE user_id = ?";

        try (
                Connection conn = Database.connect();
                PreparedStatement pstmt = conn.prepareStatement(sql)
        ) {

            pstmt.setInt(1, loggedInUser.id);

            ResultSet rs = pstmt.executeQuery();

            System.out.println("\n=== MY BOOKINGS ===");

            while (rs.next()) {

                System.out.println("--------------------------------");
                System.out.println("Booking ID : " + rs.getInt("id"));
                System.out.println("Category   : " + rs.getString("category"));
                System.out.println("Date       : " + rs.getString("date"));
                System.out.println("Time       : "
                        + rs.getString("start_time")
                        + " - "
                        + rs.getString("end_time"));
                System.out.println("Pax        : " + rs.getInt("pax"));
                System.out.println("Status     : " + rs.getString("status"));
            }

        }
        catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // =========================================================
    // MEMBERSHIP + PAYMENT
    // =========================================================
    static void membershipModule() {

        System.out.println("\n=== MEMBERSHIP ===");

        System.out.println("[1] Student - PHP 1500");
        System.out.println("[2] Regular - PHP 2000");

        int type = readInt("Selection: ");

        double amount;
        double discountRate;
        String memberType;

        if (type == 1) {

            amount = 1500;
            discountRate = 0.10;
            memberType = "Student";

        }
        else if (type == 2) {

            amount = 2000;
            discountRate = 0;
            memberType = "Regular";

        }
        else {

            System.out.println("Invalid selection.");
            return;
        }

        String gcashNum;

        while (true) {

            gcashNum = readToken("GCash Number: ");

            if (gcashNum.matches("\\d{11}")) {
                break;
            }

            System.out.println("Invalid GCash number.");
        }

        System.out.println("[1] Pay");
        System.out.println("[2] Cancel");

        int confirm = readInt("Choice: ");

        if (confirm != 1) {
            return;
        }

        String txnId =
                "MEM-" +
                loggedInUser.id +
                "-" +
                System.currentTimeMillis();

        GCashPayment payment = new GCashPayment(
                loggedInUser.name,
                txnId,
                amount,
                discountRate,
                gcashNum
        );

        payment.processInvoice();

        String membershipSql =
                "INSERT INTO memberships(user_id,type,gcash_num,status) " +
                "VALUES(?,?,?,'Pending')";

        try (Connection conn = Database.connect()) {

            // MEMBERSHIP INSERT

            PreparedStatement membershipStmt =
                    conn.prepareStatement(membershipSql);

            membershipStmt.setInt(1, loggedInUser.id);
            membershipStmt.setString(2, memberType);
            membershipStmt.setString(3, gcashNum);

            membershipStmt.executeUpdate();

            // PAYMENT INSERT

            double discounted =
                    amount - (amount * discountRate);

            double finalAmount =
                    discounted + (discounted * 0.12);

            String gcashRef =
                    "GCASH-" + System.currentTimeMillis();

            String paymentSql =
                    "INSERT INTO payments " +
                    "(user_id,payment_for,gcash_ref," +
                    "original_amount,total_amount,status," +
                    "gcash_num,final_amount) " +
                    "VALUES(?,?,?,?,?,?,?,?)";

            PreparedStatement payStmt =
                    conn.prepareStatement(paymentSql);

            payStmt.setInt(1, loggedInUser.id);

            payStmt.setString(
                    2,
                    "Membership - " + memberType
            );

            payStmt.setString(3, gcashRef);

            payStmt.setDouble(4, amount);

            payStmt.setDouble(5, finalAmount);

            payStmt.setString(6, "PAID");

            payStmt.setString(7, gcashNum);

            payStmt.setDouble(8, finalAmount);

            payStmt.executeUpdate();

            System.out.println("Membership submitted!");
            System.out.println("Payment saved!");

        }
        catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // =========================================================
    // PAYMENT HISTORY
    // =========================================================
    static void paymentHistory() {

        System.out.println("\n=== PAYMENT HISTORY ===");

        String sql =
                "SELECT * FROM payments WHERE user_id = ?";

        try (
                Connection conn = Database.connect();
                PreparedStatement pstmt = conn.prepareStatement(sql)
        ) {

            pstmt.setInt(1, loggedInUser.id);

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {

                System.out.println("--------------------------------");

                System.out.println("Payment ID      : "
                        + rs.getInt("id"));

                System.out.println("Payment For     : "
                        + rs.getString("payment_for"));

                System.out.println("GCash Ref       : "
                        + rs.getString("gcash_ref"));

                System.out.println("GCash Number    : "
                        + rs.getString("gcash_num"));

                System.out.println("Original Amount : PHP "
                        + rs.getDouble("original_amount"));

                System.out.println("Final Amount    : PHP "
                        + rs.getDouble("final_amount"));

                System.out.println("Status          : "
                        + rs.getString("status"));
            }

        }
        catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // =========================================================
    // ADMIN DASHBOARD
    // =========================================================
    static void adminDashboard() {

        while (loggedInUser != null) {

            System.out.println("\n======================================");
            System.out.println(" ADMIN DASHBOARD ");
            System.out.println("======================================");

            System.out.println("[1] Manage Bookings");
            System.out.println("[2] Manage Memberships");
            System.out.println("[3] View Payments");
            System.out.println("[4] Logout");

            int choice = readInt("Choice: ");

            if (choice == 1) {
                manageBookings();
            }
            else if (choice == 2) {
                manageMemberships();
            }
            else if (choice == 3) {
                viewPayments();
            }
            else if (choice == 4) {
                loggedInUser = null;
            }
        }
    }

    // =========================================================
    // MANAGE BOOKINGS
    // =========================================================
    static void manageBookings() {

        System.out.println("\n=== MANAGE BOOKINGS ===");

        String sql =
                "SELECT b.id, u.name, b.category, b.date, " +
                "b.status " +
                "FROM bookings b " +
                "JOIN users u ON b.user_id = u.id";

        try (
                Connection conn = Database.connect();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)
        ) {

            while (rs.next()) {

                System.out.println("--------------------------------");

                System.out.println("Booking ID : "
                        + rs.getInt("id"));

                System.out.println("User       : "
                        + rs.getString("name"));

                System.out.println("Category   : "
                        + rs.getString("category"));

                System.out.println("Date       : "
                        + rs.getString("date"));

                System.out.println("Status     : "
                        + rs.getString("status"));
            }

        }
        catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // =========================================================
    // MANAGE MEMBERSHIPS
    // =========================================================
    static void manageMemberships() {

        System.out.println("\n=== MEMBERSHIPS ===");

        String sql =
                "SELECT m.id, u.name, m.type, m.status " +
                "FROM memberships m " +
                "JOIN users u ON m.user_id = u.id";

        try (
                Connection conn = Database.connect();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)
        ) {

            while (rs.next()) {

                System.out.println("--------------------------------");

                System.out.println("Membership ID : "
                        + rs.getInt("id"));

                System.out.println("User          : "
                        + rs.getString("name"));

                System.out.println("Type          : "
                        + rs.getString("type"));

                System.out.println("Status        : "
                        + rs.getString("status"));
            }

        }
        catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // =========================================================
    // VIEW PAYMENTS
    // =========================================================
    static void viewPayments() {

        System.out.println("\n=== ALL PAYMENTS ===");

        String sql =
                "SELECT p.*, u.name " +
                "FROM payments p " +
                "JOIN users u ON p.user_id = u.id";

        try (
                Connection conn = Database.connect();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)
        ) {

            while (rs.next()) {

                System.out.println("--------------------------------");

                System.out.println("User        : "
                        + rs.getString("name"));

                System.out.println("Payment For : "
                        + rs.getString("payment_for"));

                System.out.println("GCash Ref   : "
                        + rs.getString("gcash_ref"));

                System.out.println("Amount      : PHP "
                        + rs.getDouble("final_amount"));

                System.out.println("Status      : "
                        + rs.getString("status"));
            }

        }
        catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}