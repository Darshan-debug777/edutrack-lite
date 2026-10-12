package com.edutrack.repository;

import com.edutrack.model.User;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class UserJdbcRepository {

    private final DataSource dataSource;

    public UserJdbcRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public Optional<User> findByIdentifier(String identifier) {
        String sql = "SELECT id, student_id, name, email, password_hash, role, branch, semester, created_at " +
                     "FROM users WHERE student_id = ? OR LOWER(email) = LOWER(?)";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, identifier.trim());
            ps.setString(2, identifier.trim());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRowToUser(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Database error finding user by identifier: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    public Optional<User> findByStudentId(String studentId) {
        String sql = "SELECT id, student_id, name, email, password_hash, role, branch, semester, created_at " +
                     "FROM users WHERE student_id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, studentId.trim());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRowToUser(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Database error finding user by student ID: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    public boolean existsByStudentId(String studentId) {
        String sql = "SELECT 1 FROM users WHERE student_id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, studentId.trim());
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Database error checking student ID: " + e.getMessage(), e);
        }
    }

    public boolean existsByEmail(String email) {
        String sql = "SELECT 1 FROM users WHERE LOWER(email) = LOWER(?)";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email.trim());
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Database error checking email: " + e.getMessage(), e);
        }
    }

    public boolean existsByEmailExcludingStudent(String email, String excludeStudentId) {
        String sql = "SELECT 1 FROM users WHERE LOWER(email) = LOWER(?) AND student_id != ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email.trim());
            ps.setString(2, excludeStudentId.trim());
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Database error checking duplicate email: " + e.getMessage(), e);
        }
    }

    public User save(User user) {
        String sql = "INSERT INTO users (student_id, name, email, password_hash, role, branch, semester) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, user.getStudentId().trim());
            ps.setString(2, user.getName().trim());
            ps.setString(3, user.getEmail().trim());
            ps.setString(4, user.getPasswordHash());
            ps.setString(5, user.getRole() != null ? user.getRole() : "STUDENT");
            ps.setString(6, user.getBranch().trim());
            ps.setInt(7, user.getSemester());

            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    user.setId(rs.getLong(1));
                }
            }
            return user;
        } catch (SQLException e) {
            throw new RuntimeException("Database error saving user: " + e.getMessage(), e);
        }
    }

    public boolean updateProfile(String studentId, String name, String email, String branch, Integer semester) {
        String sql = "UPDATE users SET name = ?, email = ?, branch = ?, semester = ? WHERE student_id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name.trim());
            ps.setString(2, email.trim());
            ps.setString(3, branch.trim());
            ps.setInt(4, semester);
            ps.setString(5, studentId.trim());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Database error updating user profile: " + e.getMessage(), e);
        }
    }

    public List<User> findAllStudents(String search) {
        List<User> list = new ArrayList<>();
        String sql = "SELECT id, student_id, name, email, password_hash, role, branch, semester, created_at " +
                     "FROM users WHERE role = 'STUDENT'";
        if (search != null && !search.trim().isEmpty()) {
            sql += " AND (LOWER(name) LIKE ? OR LOWER(student_id) LIKE ? OR LOWER(email) LIKE ? OR LOWER(branch) LIKE ?)";
        }
        sql += " ORDER BY student_id ASC";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            if (search != null && !search.trim().isEmpty()) {
                String pattern = "%" + search.trim().toLowerCase() + "%";
                ps.setString(1, pattern);
                ps.setString(2, pattern);
                ps.setString(3, pattern);
                ps.setString(4, pattern);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRowToUser(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Database error retrieving students: " + e.getMessage(), e);
        }
        return list;
    }

    public boolean deleteByStudentId(String studentId) {
        String sql = "DELETE FROM users WHERE student_id = ? AND role = 'STUDENT'";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, studentId.trim());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Database error deleting student: " + e.getMessage(), e);
        }
    }

    private User mapRowToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getLong("id"));
        user.setStudentId(rs.getString("student_id"));
        user.setName(rs.getString("name"));
        user.setEmail(rs.getString("email"));
        user.setPasswordHash(rs.getString("password_hash"));
        user.setRole(rs.getString("role"));
        user.setBranch(rs.getString("branch"));
        user.setSemester(rs.getInt("semester"));
        Timestamp ts = rs.getTimestamp("created_at");
        if (ts != null) {
            user.setCreatedAt(ts.toLocalDateTime());
        }
        return user;
    }
}
