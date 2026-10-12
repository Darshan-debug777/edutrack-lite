<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.sql.Connection" %>
<%@ page import="java.sql.DriverManager" %>
<%@ page import="java.sql.PreparedStatement" %>
<%@ page import="java.sql.ResultSet" %>
<%@ page import="java.sql.SQLException" %>
<!DOCTYPE html>
<html>
<head>
    <title>JSP Authentication Result</title>
    <style>
        body { font-family: sans-serif; background: #f8fafc; padding: 2rem; display: flex; justify-content: center; }
        .card { background: #fff; padding: 2rem; border-radius: 8px; border: 1px solid #e2e8f0; width: 440px; box-shadow: 0 4px 6px rgba(0,0,0,0.05); }
        .success { color: #065f46; background: #ecfdf5; padding: 1rem; border-radius: 6px; border: 1px solid #a7f3d0; }
        .failure { color: #991b1b; background: #fef2f2; padding: 1rem; border-radius: 6px; border: 1px solid #fecaca; }
        .details { margin-top: 1rem; font-size: 0.9rem; }
    </style>
</head>
<body>
    <div class="card">
        <h2>Authentication Result</h2>

<%
    String studentId = request.getParameter("studentId");
    String password = request.getParameter("password");

    boolean authenticated = false;
    String studentName = "";
    String branch = "";
    String dbError = null;

    // Database Connection Parameters
    String jdbcUrl = "jdbc:h2:mem:edutrack;MODE=MySQL;DB_CLOSE_DELAY=-1";
    String dbUser = "sa";
    String dbPass = "";

    // In production with MySQL:
    // String jdbcUrl = "jdbc:mysql://localhost:3306/edutrack?useSSL=false";
    // String dbUser = "root";
    // String dbPass = "root";

    if (studentId != null && password != null) {
        try {
            // 1. Load Driver
            Class.forName("org.h2.Driver"); // or com.mysql.cj.jdbc.Driver

            // 2. Establish Connection
            try (Connection conn = DriverManager.getConnection(jdbcUrl, dbUser, dbPass)) {
                
                // 3. Prepare Statement
                String sql = "SELECT name, branch FROM users WHERE student_id = ? AND role = 'STUDENT'";
                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setString(1, studentId.trim());

                    // 4. Execute Query and inspect ResultSet
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            authenticated = true;
                            studentName = rs.getString("name");
                            branch = rs.getString("branch");
                        }
                    }
                }
            }
        } catch (ClassNotFoundException e) {
            dbError = "JDBC Driver Error: " + e.getMessage();
        } catch (SQLException e) {
            dbError = "Database SQL Exception: " + e.getMessage();
        }
    }
%>

<%
    if (dbError != null) {
%>
        <div class="failure">
            <strong>Database Error:</strong> <%= dbError %>
        </div>
<%
    } else if (authenticated) {
%>
        <div class="success">
            <strong>✓ Authentication Successful!</strong>
            <p>Welcome, <%= studentName %>.</p>
        </div>
        <div class="details">
            <p><strong>Student ID:</strong> <%= studentId %></p>
            <p><strong>Branch:</strong> <%= branch %></p>
            <p><strong>Mechanism:</strong> Verified using JDBC PreparedStatement and ResultSet.</p>
        </div>
<%
    } else {
%>
        <div class="failure">
            <strong>✕ Authentication Failed</strong>
            <p>Invalid Student ID or credentials.</p>
        </div>
<%
    }
%>
        <div style="margin-top: 1.5rem;">
            <a href="login.jsp" style="color: #4f46e5; text-decoration: none; font-weight: bold;">&larr; Return to Login</a>
        </div>
    </div>
</body>
</html>
