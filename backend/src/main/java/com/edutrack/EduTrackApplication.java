package com.edutrack;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;

@SpringBootApplication
public class EduTrackApplication {

    private static final Logger log = LoggerFactory.getLogger(EduTrackApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(EduTrackApplication.class, args);
    }

    @Bean
    public CommandLineRunner verifyDatabaseConnection(DataSource dataSource) {
        return args -> {
            try (Connection conn = dataSource.getConnection()) {
                DatabaseMetaData meta = conn.getMetaData();
                log.info("============================================================");
                log.info(" EDUTRACK LITE STARTED SUCCESSFULLY!");
                log.info(" Connected Database: {} {}", meta.getDatabaseProductName(), meta.getDatabaseProductVersion());
                log.info(" JDBC Driver: {}", meta.getDriverName());
                log.info(" REST API available at: http://localhost:8080/api");
                log.info(" Health check: http://localhost:8080/api/health");
                log.info("============================================================");
            } catch (Exception e) {
                log.error("============================================================");
                log.error(" [CRITICAL] DATABASE CONNECTION FAILED!");
                log.error(" Error: {}", e.getMessage());
                log.error(" Troubleshooting Guide:");
                log.error(" 1. Ensure MySQL Server is started (e.g. via XAMPP, WAMP, or Windows Services).");
                log.error(" 2. Verify username & password in 'src/main/resources/application.properties'.");
                log.error(" 3. To run offline/in-memory mode, use: --spring.profiles.active=h2");
                log.error("============================================================");
            }
        };
    }
}
