<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <title>Experiment 10 — JSP JDBC Student Login</title>
    <style>
        body { font-family: sans-serif; background: #f8fafc; padding: 2rem; display: flex; justify-content: center; }
        .card { background: #fff; padding: 2rem; border-radius: 8px; border: 1px solid #e2e8f0; width: 340px; box-shadow: 0 4px 6px rgba(0,0,0,0.05); }
        .form-group { margin-bottom: 1rem; }
        label { display: block; margin-bottom: 0.25rem; font-weight: bold; font-size: 0.85rem; }
        input { width: 100%; padding: 0.5rem; border: 1px solid #cbd5e1; border-radius: 4px; box-sizing: border-box; }
        button { width: 100%; background: #4f46e5; color: white; padding: 0.6rem; border: none; border-radius: 4px; font-weight: bold; cursor: pointer; }
    </style>
</head>
<body>
    <div class="card">
        <h2>JSP Student Login</h2>
        <p style="color: #64748b; font-size: 0.85rem;">Experiment 10: JDBC Authentication with Connection, PreparedStatement & ResultSet</p>
        
        <form action="validateLogin.jsp" method="POST">
            <div class="form-group">
                <label for="studentId">Student ID:</label>
                <input type="text" id="studentId" name="studentId" placeholder="e.g. STU101" required />
            </div>
            <div class="form-group">
                <label for="password">Password:</label>
                <input type="password" id="password" name="password" placeholder="Enter password" required />
            </div>
            <button type="submit">Authenticate via JDBC</button>
        </form>
    </div>
</body>
</html>
