package com.edutrack.repository;

import com.edutrack.model.Subject;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class SubjectJdbcRepository {

    private final DataSource dataSource;

    public SubjectJdbcRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public List<Subject> findAll() {
        List<Subject> list = new ArrayList<>();
        String sql = "SELECT id, code, name, credits FROM subjects ORDER BY code ASC";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new Subject(
                        rs.getLong("id"),
                        rs.getString("code"),
                        rs.getString("name"),
                        rs.getInt("credits")
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Database error retrieving subjects: " + e.getMessage(), e);
        }
        return list;
    }

    public Optional<Subject> findById(Long id) {
        String sql = "SELECT id, code, name, credits FROM subjects WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(new Subject(
                            rs.getLong("id"),
                            rs.getString("code"),
                            rs.getString("name"),
                            rs.getInt("credits")
                    ));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Database error finding subject by id: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    public Optional<Subject> findByCode(String code) {
        String sql = "SELECT id, code, name, credits FROM subjects WHERE UPPER(code) = UPPER(?)";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, code.trim());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(new Subject(
                            rs.getLong("id"),
                            rs.getString("code"),
                            rs.getString("name"),
                            rs.getInt("credits")
                    ));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Database error finding subject by code: " + e.getMessage(), e);
        }
        return Optional.empty();
    }
}
