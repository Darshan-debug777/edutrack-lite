# EduTrack Lite — Student Management System

A small, robust, real-time, fully working Student Management System designed for second-year engineering college laboratory demonstrations and viva examinations.

---

## Architecture Overview

```
React Frontend (Vite, Port 5173)
       ↓ HTTP REST Requests (fetch API, JSON)
Spring Boot Backend (Port 8080)
       ↓ Controller Layer (@RestController)
       ↓ Service Layer (@Service)
       ↓ JDBC Repository (PreparedStatement, ResultSet, Connection)
Relational Database (MySQL / H2 in MySQL Compatibility Mode)
```

---

## Prerequisites & Installation

- **Java JDK**: Version 17+ installed (`C:\Program Files\Microsoft\jdk-17.0.20.101-hotspot`)
- **Apache Maven**: Version 3.9+ (`C:\Users\Darshan jain\AppData\Local\Programs\apache-maven-3.9.6\bin`)
- **Node.js & npm**: Node v18+ / v24 (`C:\Program Files\nodejs`)
- **MySQL Database**: Local MySQL Server (port 3306) or built-in zero-config standalone mode.

---

## Quick Start (Two Terminal Windows)

### 1. Start the Backend (Terminal 1)
Open PowerShell and run:
```powershell
$env:JAVA_HOME = "C:\Program Files\Microsoft\jdk-17.0.20.101-hotspot"
$env:Path = "$env:JAVA_HOME\bin;C:\Users\Darshan jain\AppData\Local\Programs\apache-maven-3.9.6\bin;" + $env:Path

cd "c:\Users\Darshan jain\OneDrive\Desktop\EDUTRAC\backend"

# Option A: Standalone / Viva Demo Mode (Zero Setup - H2 in MySQL mode)
mvn spring-boot:run "-Dspring-boot.run.profiles=h2"

# Option B: Standard Local MySQL (Connects to localhost:3306/edutrack)
# mvn spring-boot:run
```
*Backend is live at:* `http://localhost:8080/api`  
*Health Check:* `http://localhost:8080/api/health`

### 2. Start the Frontend (Terminal 2)
Open a second PowerShell window and run:
```powershell
$env:Path = "C:\Program Files\nodejs;" + $env:Path

cd "c:\Users\Darshan jain\OneDrive\Desktop\EDUTRAC\frontend"
npm run dev
```
*Frontend opens at:* `http://localhost:5173`

---

## Core Features Implemented

1. **One Unified Dashboard**: Single-screen student portal for fast, stress-free college demonstrations.
2. **Student CRUD**:
   - **Add Student**: Enrolls new student into the database with validation.
   - **View & Search**: Real-time multi-field search (ID, name, branch, email).
   - **Edit Student**: Updates student name, email, branch, and semester in database.
   - **Delete Student**: Removes student record with cascading integrity.
3. **Marks & Attendance Tracking**:
   - Enter marks obtained and maximum marks &rarr; automatically calculates percentage.
   - Enter attended classes and total conducted classes &rarr; automatically calculates attendance percentage and eligibility badge.
   - Saves directly to database.
4. **Assignment Management**:
   - Add new assignment with title, subject, due date, and instructions.
   - Toggle status between **Pending** and **Completed** with instant database update.
   - Automatically displays **Overdue** if the due date has passed.
5. **Real Database Persistence**:
   - Every operation executes direct SQL `PreparedStatement` and `ResultSet` queries.
   - Data persists across browser refreshes and backend restarts.

---

## Demo Credentials (Viva Testing)

| Role | Student ID / Username | Password |
|---|---|---|
| **Admin / Faculty** | `admin@edutrack.edu` or `ADMIN001` | `AdminPassword123!` |
| **Student 1** | `STU101` or `john.doe@edutrack.edu` | `StudentPassword123!` |
| **Student 2** | `STU102` or `jane.smith@edutrack.edu` | `StudentPassword123!` |

---

## MySQL Configuration Setup

If you want to run directly against an external MySQL Server:
1. Ensure MySQL service is running on port 3306.
2. Create the database:
   ```sql
   CREATE DATABASE edutrack;
   ```
3. Update credentials in `backend/src/main/resources/application.properties`:
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/edutrack?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
   spring.datasource.username=root
   spring.datasource.password=YOUR_PASSWORD
   ```
4. Start backend using `mvn spring-boot:run`. The tables (`schema.sql`) and sample data (`data.sql`) will initialize automatically.

---

## Automated Backend Test Suite

To run all 15 automated JUnit 5 tests covering CRUD, validations, and role restrictions:
```powershell
$env:JAVA_HOME = "C:\Program Files\Microsoft\jdk-17.0.20.101-hotspot"
$env:Path = "$env:JAVA_HOME\bin;C:\Users\Darshan jain\AppData\Local\Programs\apache-maven-3.9.6\bin;" + $env:Path
cd "c:\Users\Darshan jain\OneDrive\Desktop\EDUTRAC\backend"
mvn test
```
Result: `Tests run: 15, Failures: 0, Errors: 0, Skipped: 0 — BUILD SUCCESS`

---

## Troubleshooting

- **Port 8080 in use?**
  Run: `Stop-Process -Id (Get-NetTCPConnection -LocalPort 8080).OwningProcess -Force`
- **Port 5173 in use?**
  Vite will automatically pick port 5174, or run: `npx vite --port 5173`
- **Database connection error?**
  Verify `http://localhost:8080/api/health`. If MySQL is not running locally, use `-Dspring-boot.run.profiles=h2` to run with zero external dependencies.
