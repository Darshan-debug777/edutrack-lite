package com.edutrack.controller;

import com.edutrack.model.Assignment;
import com.edutrack.model.Attendance;
import com.edutrack.model.DashboardSummary;
import com.edutrack.model.Mark;
import com.edutrack.model.User;
import com.edutrack.model.dto.ApiResponse;
import com.edutrack.model.dto.AssignmentStatusUpdateRequest;
import com.edutrack.model.dto.UpdateProfileRequest;
import com.edutrack.service.AcademicService;
import com.edutrack.service.AssignmentService;
import com.edutrack.service.AuthService;
import com.edutrack.service.StudentService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/student")
public class StudentController {

    private final AuthService authService;
    private final StudentService studentService;
    private final AcademicService academicService;
    private final AssignmentService assignmentService;

    public StudentController(AuthService authService,
                             StudentService studentService,
                             AcademicService academicService,
                             AssignmentService assignmentService) {
        this.authService = authService;
        this.studentService = studentService;
        this.academicService = academicService;
        this.assignmentService = assignmentService;
    }

    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse<DashboardSummary>> getDashboardSummary(HttpSession session) {
        User user = authService.getCurrentUser(session);
        DashboardSummary summary = studentService.getDashboardSummary(user.getStudentId());
        return ResponseEntity.ok(ApiResponse.ok("Dashboard statistics retrieved successfully", summary));
    }

    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<User>> getProfile(HttpSession session) {
        User user = authService.getCurrentUser(session);
        User profile = studentService.getProfile(user.getStudentId());
        profile.setPasswordHash(null);
        return ResponseEntity.ok(ApiResponse.ok("Profile retrieved successfully", profile));
    }

    @PutMapping("/profile")
    public ResponseEntity<ApiResponse<User>> updateProfile(@Valid @RequestBody UpdateProfileRequest request,
                                                           HttpSession session) {
        User user = authService.getCurrentUser(session);
        User updated = studentService.updateProfile(user.getStudentId(), request);
        updated.setPasswordHash(null);
        // Refresh session with updated details
        session.setAttribute(AuthService.SESSION_USER_KEY, updated);
        return ResponseEntity.ok(ApiResponse.ok("Profile updated successfully in MySQL database!", updated));
    }

    @GetMapping("/marks")
    public ResponseEntity<ApiResponse<List<Mark>>> getMarks(HttpSession session) {
        User user = authService.getCurrentUser(session);
        List<Mark> marks = academicService.getMarksForStudent(user.getStudentId());
        return ResponseEntity.ok(ApiResponse.ok("Subject-wise marks retrieved successfully", marks));
    }

    @GetMapping("/attendance")
    public ResponseEntity<ApiResponse<List<Attendance>>> getAttendance(HttpSession session) {
        User user = authService.getCurrentUser(session);
        List<Attendance> attendance = academicService.getAttendanceForStudent(user.getStudentId());
        return ResponseEntity.ok(ApiResponse.ok("Subject-wise attendance retrieved successfully", attendance));
    }

    @GetMapping("/assignments")
    public ResponseEntity<ApiResponse<List<Assignment>>> getAssignments(HttpSession session) {
        User user = authService.getCurrentUser(session);
        List<Assignment> assignments = assignmentService.getAssignmentsForStudent(user.getStudentId());
        return ResponseEntity.ok(ApiResponse.ok("Assignments retrieved successfully", assignments));
    }

    @PatchMapping("/assignments/{id}/status")
    public ResponseEntity<ApiResponse<Void>> updateAssignmentStatus(@PathVariable Long id,
                                                                    @Valid @RequestBody AssignmentStatusUpdateRequest req,
                                                                    HttpSession session) {
        User user = authService.getCurrentUser(session);
        assignmentService.updateStatus(id, user.getStudentId(), req.getStatus());
        return ResponseEntity.ok(ApiResponse.ok("Assignment status updated to " + req.getStatus().toUpperCase() + "!"));
    }
}
