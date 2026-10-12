# Experiment 10: JSP & JDBC Login Validation

## Objective
To demonstrate server-side JavaServer Pages (JSP) web development coupled with direct Java Database Connectivity (JDBC) utilizing `Connection`, `PreparedStatement`, and `ResultSet` to authenticate credentials against a relational database.

## Source Files
- [`login.jsp`](login.jsp) — HTML form collecting Student ID and password.
- [`validateLogin.jsp`](validateLogin.jsp) — JSP scriptlet executing parameterized SQL query via `PreparedStatement`.
- [`Exp10JdbcDemo.java`](Exp10JdbcDemo.java) — Standalone runnable Java class demonstrating the exact JDBC connection, statement, and query execution.

## Concepts Demonstrated
1. **JSP Lifecycle & Scriptlets**: Scriptlet tags `<% ... %>` and expression tags `<%= ... %>`.
2. **JDBC Architecture**:
   - `Class.forName(...)`: Loading the database driver.
   - `DriverManager.getConnection(...)`: Establishing the database socket connection.
   - `PreparedStatement`: Parameterized SQL avoiding SQL Injection.
   - `ResultSet`: Navigating rows using `rs.next()`.
   - `try-with-resources`: Automatically closing connections.

## How to Run Standalone Demonstration
Run in Windows PowerShell:
```powershell
$env:JAVA_HOME = "C:\Program Files\Microsoft\jdk-17.0.20.101-hotspot"
$env:Path = "$env:JAVA_HOME\bin;" + $env:Path
$h2Jar = Get-ChildItem -Path "$env:USERPROFILE\.m2\repository\com\h2database\h2" -Filter "*.jar" -Recurse | Select-Object -First 1 -ExpandProperty FullName

cd "c:\Users\Darshan jain\OneDrive\Desktop\EDUTRAC"
javac -cp "$h2Jar" experiments\exp10_jsp_jdbc_login\Exp10JdbcDemo.java
java -cp ".;$h2Jar" experiments.exp10_jsp_jdbc_login.Exp10JdbcDemo
```
