package experiments.exp10_jsp_jdbc_login;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Experiment 10 Demonstration: Standalone Java verification of the exact
 * JDBC Connection, PreparedStatement, and ResultSet logic utilized in validateLogin.jsp.
 */
public class Exp10JdbcDemo {

    public static void main(String[] args) {
        System.out.println("==============================================================");
        System.out.println("   EXPERIMENT 10: JSP & JDBC LOGIN VALIDATION DEMO            ");
        System.out.println("==============================================================");

        String jdbcUrl = "jdbc:h2:mem:exp10_db;DB_CLOSE_DELAY=-1";
        String user = "sa";
        String pass = "";

        try {
            // 1. Load Driver
            System.out.println("[Step 1] Loading JDBC Driver...");
            Class.forName("org.h2.Driver");

            // 2. Establish Connection
            System.out.println("[Step 2] Establishing JDBC Connection...");
            try (Connection conn = DriverManager.getConnection(jdbcUrl, user, pass)) {
                System.out.println("Connected to: " + conn.getMetaData().getDatabaseProductName());

                // Setup demo table and student
                try (Statement stmt = conn.createStatement()) {
                    stmt.execute("CREATE TABLE users (student_id VARCHAR(50), name VARCHAR(100), branch VARCHAR(50), password VARCHAR(50))");
                    stmt.execute("INSERT INTO users VALUES ('STU101', 'John Doe', 'Computer Science', 'StudentPass123')");
                    stmt.execute("INSERT INTO users VALUES ('STU102', 'Jane Smith', 'Information Technology', 'StudentPass123')");
                }

                // 3. Authenticate with PreparedStatement
                System.out.println("\n[Step 3] Testing PreparedStatement Authentication:");
                String testStudentId = "STU101";
                String testPassword = "StudentPass123";

                String sql = "SELECT name, branch FROM users WHERE student_id = ? AND password = ?";
                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setString(1, testStudentId);
                    ps.setString(2, testPassword);

                    // 4. Inspect ResultSet
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            String name = rs.getString("name");
                            String branch = rs.getString("branch");
                            System.out.println(">> JDBC AUTHENTICATION SUCCESSFUL!");
                            System.out.println(">> Student ID : " + testStudentId);
                            System.out.println(">> Full Name  : " + name);
                            System.out.println(">> Branch     : " + branch);
                        } else {
                            System.out.println(">> JDBC AUTHENTICATION FAILED: Invalid credentials.");
                        }
                    }
                }

                // Testing Invalid Credentials
                System.out.println("\n[Step 4] Testing Invalid Credentials Handling:");
                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setString(1, "STU999");
                    ps.setString(2, "WrongPassword");
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            System.out.println("Unexpected success!");
                        } else {
                            System.out.println(">> Correctly rejected non-existent student record.");
                        }
                    }
                }
            }

            System.out.println("\nExperiment 10 JDBC demonstration completed successfully!");
        } catch (ClassNotFoundException e) {
            System.err.println("JDBC Driver class not found: " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("SQL Execution Exception: " + e.getMessage());
        }
    }
}
