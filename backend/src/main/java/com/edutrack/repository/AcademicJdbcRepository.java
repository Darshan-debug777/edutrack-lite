package com.edutrack.repository;

import com.edutrack.model.Attendance;
import com.edutrack.model.DashboardSummary;
import com.edutrack.model.Mark;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class AcademicJdbcRepository {

    private final DataSource dataSource;

    public AcademicJdbcRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public List<Mark> findMarksByStudentId(String studentId) {
        List<Mark> list = new ArrayList<>();
        // Left join with subjects ensures every subject is returned even if marks have not been recorded yet
        String sql = "SELECT m.id AS mark_id, s.id AS subject_id, s.code AS subject_code, s.name AS subject_name, " +
                     "COALESCE(m.marks_obtained, 0.0) AS marks_obtained, COALESCE(m.max_marks, 100.0) AS max_marks " +
                     "FROM subjects s " +
                     "LEFT JOIN marks m ON s.id = m.subject_id AND m.student_id = ? " +
                     "ORDER BY s.code ASC";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, studentId.trim());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Long markId = rs.getObject("mark_id") != null ? rs.getLong("mark_id") : null;
                    list.add(new Mark(
                            markId,
                            studentId,
                            rs.getLong("subject_id"),
                            rs.getString("subject_code"),
                            rs.getString("subject_name"),
                            rs.getDouble("marks_obtained"),
                            rs.getDouble("max_marks")
                    ));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Database error loading marks for student " + studentId + ": " + e.getMessage(), e);
        }
        return list;
    }

    public List<Attendance> findAttendanceByStudentId(String studentId) {
        List<Attendance> list = new ArrayList<>();
        String sql = "SELECT a.id AS attendance_id, s.id AS subject_id, s.code AS subject_code, s.name AS subject_name, " +
                     "COALESCE(a.classes_attended, 0) AS classes_attended, COALESCE(a.total_classes, 0) AS total_classes " +
                     "FROM subjects s " +
                     "LEFT JOIN attendance a ON s.id = a.subject_id AND a.student_id = ? " +
                     "ORDER BY s.code ASC";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, studentId.trim());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Long attId = rs.getObject("attendance_id") != null ? rs.getLong("attendance_id") : null;
                    list.add(new Attendance(
                            attId,
                            studentId,
                            rs.getLong("subject_id"),
                            rs.getString("subject_code"),
                            rs.getString("subject_name"),
                            rs.getInt("classes_attended"),
                            rs.getInt("total_classes")
                    ));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Database error loading attendance for student " + studentId + ": " + e.getMessage(), e);
        }
        return list;
    }

    public void saveOrUpdateMark(String studentId, Long subjectId, Double marksObtained, Double maxMarks) {
        String checkSql = "SELECT id FROM marks WHERE student_id = ? AND subject_id = ?";
        String updateSql = "UPDATE marks SET marks_obtained = ?, max_marks = ? WHERE student_id = ? AND subject_id = ?";
        String insertSql = "INSERT INTO marks (student_id, subject_id, marks_obtained, max_marks) VALUES (?, ?, ?, ?)";

        try (Connection conn = dataSource.getConnection()) {
            boolean exists = false;
            try (PreparedStatement checkPs = conn.prepareStatement(checkSql)) {
                checkPs.setString(1, studentId.trim());
                checkPs.setLong(2, subjectId);
                try (ResultSet rs = checkPs.executeQuery()) {
                    exists = rs.next();
                }
            }

            if (exists) {
                try (PreparedStatement updatePs = conn.prepareStatement(updateSql)) {
                    updatePs.setDouble(1, marksObtained);
                    updatePs.setDouble(2, maxMarks);
                    updatePs.setString(3, studentId.trim());
                    updatePs.setLong(4, subjectId);
                    updatePs.executeUpdate();
                }
            } else {
                try (PreparedStatement insertPs = conn.prepareStatement(insertSql)) {
                    insertPs.setString(1, studentId.trim());
                    insertPs.setLong(2, subjectId);
                    insertPs.setDouble(3, marksObtained);
                    insertPs.setDouble(4, maxMarks);
                    insertPs.executeUpdate();
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Database error saving mark: " + e.getMessage(), e);
        }
    }

    public void saveOrUpdateAttendance(String studentId, Long subjectId, Integer classesAttended, Integer totalClasses) {
        String checkSql = "SELECT id FROM attendance WHERE student_id = ? AND subject_id = ?";
        String updateSql = "UPDATE attendance SET classes_attended = ?, total_classes = ? WHERE student_id = ? AND subject_id = ?";
        String insertSql = "INSERT INTO attendance (student_id, subject_id, classes_attended, total_classes) VALUES (?, ?, ?, ?)";

        try (Connection conn = dataSource.getConnection()) {
            boolean exists = false;
            try (PreparedStatement checkPs = conn.prepareStatement(checkSql)) {
                checkPs.setString(1, studentId.trim());
                checkPs.setLong(2, subjectId);
                try (ResultSet rs = checkPs.executeQuery()) {
                    exists = rs.next();
                }
            }

            if (exists) {
                try (PreparedStatement updatePs = conn.prepareStatement(updateSql)) {
                    updatePs.setInt(1, classesAttended);
                    updatePs.setInt(2, totalClasses);
                    updatePs.setString(3, studentId.trim());
                    updatePs.setLong(4, subjectId);
                    updatePs.executeUpdate();
                }
            } else {
                try (PreparedStatement insertPs = conn.prepareStatement(insertSql)) {
                    insertPs.setString(1, studentId.trim());
                    insertPs.setLong(2, subjectId);
                    insertPs.setInt(3, classesAttended);
                    insertPs.setInt(4, totalClasses);
                    insertPs.executeUpdate();
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Database error saving attendance: " + e.getMessage(), e);
        }
    }

    public DashboardSummary getDashboardSummary(String studentId, String name, String branch, Integer semester) {
        double avgMarks = 0.0;
        double overallAtt = 0.0;
        int pending = 0;
        int completed = 0;

        // Calculate average percentage of marks
        String marksSql = "SELECT marks_obtained, max_marks FROM marks WHERE student_id = ?";
        // Calculate attendance percentage: sum(classes_attended) / sum(total_classes)
        String attSql = "SELECT SUM(classes_attended) AS total_attended, SUM(total_classes) AS grand_total FROM attendance WHERE student_id = ?";
        // Count assignment status
        String assignSql = "SELECT " +
                "SUM(CASE WHEN COALESCE(sa.status, 'PENDING') = 'COMPLETED' THEN 1 ELSE 0 END) AS completed_count, " +
                "SUM(CASE WHEN COALESCE(sa.status, 'PENDING') != 'COMPLETED' THEN 1 ELSE 0 END) AS pending_count " +
                "FROM assignments a " +
                "LEFT JOIN student_assignments sa ON a.id = sa.assignment_id AND sa.student_id = ?";

        try (Connection conn = dataSource.getConnection()) {
            // 1. Marks
            try (PreparedStatement ps = conn.prepareStatement(marksSql)) {
                ps.setString(1, studentId.trim());
                try (ResultSet rs = ps.executeQuery()) {
                    double totalPercentage = 0.0;
                    int count = 0;
                    while (rs.next()) {
                        double obtained = rs.getDouble("marks_obtained");
                        double max = rs.getDouble("max_marks");
                        if (max > 0) {
                            totalPercentage += (obtained / max) * 100.0;
                            count++;
                        }
                    }
                    if (count > 0) {
                        avgMarks = Math.round((totalPercentage / count) * 10.0) / 10.0;
                    }
                }
            }

            // 2. Attendance
            try (PreparedStatement ps = conn.prepareStatement(attSql)) {
                ps.setString(1, studentId.trim());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        int attended = rs.getInt("total_attended");
                        int total = rs.getInt("grand_total");
                        if (total > 0) {
                            overallAtt = Math.round(((double) attended / (double) total * 100.0) * 10.0) / 10.0;
                        }
                    }
                }
            }

            // 3. Assignments
            try (PreparedStatement ps = conn.prepareStatement(assignSql)) {
                ps.setString(1, studentId.trim());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        completed = rs.getInt("completed_count");
                        pending = rs.getInt("pending_count");
                    }
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Database error calculating dashboard summary: " + e.getMessage(), e);
        }

        return new DashboardSummary(studentId, name, branch, semester, avgMarks, overallAtt, pending, completed);
    }
}
