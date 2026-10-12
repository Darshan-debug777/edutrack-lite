package com.edutrack.controller;

import com.edutrack.model.dto.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/health")
public class HealthController {

    private final DataSource dataSource;

    public HealthController(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Map<String, Object>>> checkHealth() {
        Map<String, Object> details = new HashMap<>();
        try (Connection conn = dataSource.getConnection()) {
            DatabaseMetaData metaData = conn.getMetaData();
            details.put("status", "UP");
            details.put("databaseProduct", metaData.getDatabaseProductName());
            details.put("databaseVersion", metaData.getDatabaseProductVersion());
            details.put("driverName", metaData.getDriverName());

            List<String> tables = new ArrayList<>();
            try (ResultSet rs = metaData.getTables(conn.getCatalog(), null, "%", new String[]{"TABLE"})) {
                while (rs.next()) {
                    tables.add(rs.getString("TABLE_NAME"));
                }
            }
            details.put("tablesFound", tables);
            return ResponseEntity.ok(ApiResponse.ok("EduTrack Lite Backend & Database are healthy and connected!", details));
        } catch (SQLException e) {
            details.put("status", "DOWN");
            details.put("error", e.getMessage());
            details.put("troubleshooting", "Could not connect to database. Ensure MySQL server is running on localhost:3306 or check application.properties.");
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(new ApiResponse<>(false, "Database connection unavailable: " + e.getMessage(), details));
        }
    }
}
