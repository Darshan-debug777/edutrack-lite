package com.edutrack.controller;

import com.edutrack.model.Assignment;
import com.edutrack.model.Attendance;
import com.edutrack.model.Mark;
import com.edutrack.model.Subject;
import com.edutrack.model.User;
import com.edutrack.model.dto.ApiResponse;
import com.edutrack.model.dto.AssignmentRequest;
import com.edutrack.model.dto.AttendanceUpdateRequest;
import com.edutrack.model.dto.MarkUpdateRequest;
import com.edutrack.model.dto.RegisterRequest;
import com.edutrack.model.dto.UpdateProfileRequest;
import com.edutrack.service.AcademicService;
import com.edutrack.service.AdminService;
import com.edutrack.service.AssignmentService;
import com.edutrack.service.AuthService;
import com.edutrack.service.StudentService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminService adminService;
    private final AuthService authService;
    private final StudentService studentService;
    private final AcademicService academicService;
    private final AssignmentService assignmentService;

    public AdminController(AdminService adminService,
                           AuthService authService,
                           StudentService studentService,
                           AcademicService academicService,
                           AssignmentService assignmentService) {
        this.adminService = adminService;
        this.authService = authService;
        this.studentService = studentService;
        this.academicService = academicService;
        this.assignmentService = assignmentService;
    }

    // ==========================================
    // Student Management
    // ==========================================
    @GetMapping("/students")
    public ResponseEntity<ApiResponse<List<User>>> getStudents(
            @RequestParam(required = false) String search) {
        List<User> students = adminService.getAllStudents(search);
        students.forEach(s -> s.setPasswordHash(null));
        return ResponseEntity.ok(ApiResponse.ok("Retrieved " + students.size() + " student records", students));
    }

    @PostMapping("/students")
    public ResponseEntity<ApiResponse<User>> createStudent(@Valid @RequestBody RegisterRequest request) {
        User created = authService.register(request);
        created.setPasswordHash(null);
        return ResponseEntity.ok(ApiResponse.ok("New student record created successfully!", created));
    }

    @GetMapping("/students/{studentId}")
    public ResponseEntity<ApiResponse<User>> getStudent(@PathVariable String studentId) {
        User student = studentService.getProfile(studentId);
        student.setPasswordHash(null);
        return ResponseEntity.ok(ApiResponse.ok("Student details retrieved", student));
    }

    @PutMapping("/students/{studentId}")
    public ResponseEntity<ApiResponse<User>> updateStudent(
            @PathVariable String studentId,
            @Valid @RequestBody UpdateProfileRequest request) {
        User updated = studentService.updateProfile(studentId, request);
        updated.setPasswordHash(null);
        return ResponseEntity.ok(ApiResponse.ok("Student record updated successfully!", updated));
    }

    @DeleteMapping("/students/{studentId}")
    public ResponseEntity<ApiResponse<Void>> deleteStudent(@PathVariable String studentId) {
        adminService.deleteStudent(studentId);
        return ResponseEntity.ok(ApiResponse.ok("Student " + studentId + " deleted successfully."));
    }

    // ==========================================
    // Subjects & Academics
    // ==========================================
    @GetMapping("/subjects")
    public ResponseEntity<ApiResponse<List<Subject>>> getSubjects() {
        List<Subject> subjects = academicService.getAllSubjects();
        return ResponseEntity.ok(ApiResponse.ok("Subjects retrieved", subjects));
    }

    @GetMapping("/students/{studentId}/marks")
    public ResponseEntity<ApiResponse<List<Mark>>> getStudentMarks(@PathVariable String studentId) {
        List<Mark> marks = academicService.getMarksForStudent(studentId);
        return ResponseEntity.ok(ApiResponse.ok("Marks retrieved for " + studentId, marks));
    }

    @PostMapping("/marks")
    public ResponseEntity<ApiResponse<Void>> updateMark(@Valid @RequestBody MarkUpdateRequest request) {
        academicService.updateMark(request);
        return ResponseEntity.ok(ApiResponse.ok("Marks saved successfully in MySQL!"));
    }

    @GetMapping("/students/{studentId}/attendance")
    public ResponseEntity<ApiResponse<List<Attendance>>> getStudentAttendance(@PathVariable String studentId) {
        List<Attendance> att = academicService.getAttendanceForStudent(studentId);
        return ResponseEntity.ok(ApiResponse.ok("Attendance retrieved for " + studentId, att));
    }

    @PostMapping("/attendance")
    public ResponseEntity<ApiResponse<Void>> updateAttendance(@Valid @RequestBody AttendanceUpdateRequest request) {
        academicService.updateAttendance(request);
        return ResponseEntity.ok(ApiResponse.ok("Attendance recorded successfully in MySQL!"));
    }

    // ==========================================
    // Assignments Management
    // ==========================================
    @GetMapping("/assignments")
    public ResponseEntity<ApiResponse<List<Assignment>>> getAllAssignments() {
        List<Assignment> list = assignmentService.getAllAssignments();
        return ResponseEntity.ok(ApiResponse.ok("Assignments retrieved", list));
    }

    @PostMapping("/assignments")
    public ResponseEntity<ApiResponse<Assignment>> createAssignment(@Valid @RequestBody AssignmentRequest request,
                                                                    HttpSession session) {
        User admin = session != null ? (User) session.getAttribute(AuthService.SESSION_USER_KEY) : null;
        String adminId = admin != null ? admin.getStudentId() : "ADMIN";
        Assignment created = assignmentService.createAssignment(request, adminId);
        return ResponseEntity.ok(ApiResponse.ok("Assignment created successfully!", created));
    }

    @PutMapping("/assignments/{id}")
    public ResponseEntity<ApiResponse<Void>> updateAssignment(@PathVariable Long id,
                                                              @Valid @RequestBody AssignmentRequest request) {
        assignmentService.updateAssignment(id, request);
        return ResponseEntity.ok(ApiResponse.ok("Assignment updated successfully!"));
    }

    @DeleteMapping("/assignments/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteAssignment(@PathVariable Long id) {
        assignmentService.deleteAssignment(id);
        return ResponseEntity.ok(ApiResponse.ok("Assignment deleted successfully!"));
    }

    @PostMapping("/assignments/{id}/status")
    public ResponseEntity<ApiResponse<Void>> postAssignmentStatus(
            @PathVariable Long id,
            @RequestBody(required = false) java.util.Map<String, String> body) {
        return updateAssignmentStatus(id, body);
    }

    @PatchMapping("/assignments/{id}/status")
    public ResponseEntity<ApiResponse<Void>> updateAssignmentStatus(
            @PathVariable Long id,
            @RequestBody(required = false) java.util.Map<String, String> body) {
        String status = (body != null && body.containsKey("status")) ? body.get("status") : "COMPLETED";
        String studentId = (body != null && body.containsKey("studentId")) ? body.get("studentId") : "STU101";
        assignmentService.updateStatus(id, studentId, status);
        return ResponseEntity.ok(ApiResponse.ok("Assignment status updated to " + status + "!"));
    }
}
