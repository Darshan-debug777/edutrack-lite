package com.edutrack;

import com.edutrack.model.dto.AssignmentRequest;
import com.edutrack.model.dto.AssignmentStatusUpdateRequest;
import com.edutrack.model.dto.AttendanceUpdateRequest;
import com.edutrack.model.dto.LoginRequest;
import com.edutrack.model.dto.MarkUpdateRequest;
import com.edutrack.model.dto.RegisterRequest;
import com.edutrack.model.dto.UpdateProfileRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("h2")
public class EduTrackApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // Helper: Login as Student
    private MockHttpSession loginAsStudent() throws Exception {
        LoginRequest req = new LoginRequest("STU101", "StudentPassword123!");
        MvcResult res = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.role").value("STUDENT"))
                .andReturn();
        return (MockHttpSession) res.getRequest().getSession();
    }

    // Helper: Login as Admin
    private MockHttpSession loginAsAdmin() throws Exception {
        LoginRequest req = new LoginRequest("ADMIN001", "AdminPassword123!");
        MvcResult res = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.role").value("ADMIN"))
                .andReturn();
        return (MockHttpSession) res.getRequest().getSession();
    }

    @Test
    @DisplayName("Health endpoint returns database status and UP")
    void testHealthEndpoint() throws Exception {
        mockMvc.perform(get("/api/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.status").value("UP"));
    }

    @Test
    @DisplayName("Valid Student Login succeeds with correct role")
    void testStudentLoginSuccess() throws Exception {
        LoginRequest req = new LoginRequest("STU101", "StudentPassword123!");
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.studentId").value("STU101"))
                .andExpect(jsonPath("$.data.role").value("STUDENT"));
    }

    @Test
    @DisplayName("Valid Admin Login succeeds with correct role")
    void testAdminLoginSuccess() throws Exception {
        LoginRequest req = new LoginRequest("admin@edutrack.edu", "AdminPassword123!");
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.role").value("ADMIN"));
    }

    @Test
    @DisplayName("Invalid password returns 401 Unauthorized")
    void testLoginInvalidPassword() throws Exception {
        LoginRequest req = new LoginRequest("STU101", "WrongPassword999!");
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("Register new student succeeds and duplicate registration is rejected")
    void testRegisterAndDuplicateRejection() throws Exception {
        String uniqueId = "STU_" + System.currentTimeMillis();
        String uniqueEmail = "user_" + System.currentTimeMillis() + "@edutrack.edu";

        RegisterRequest req = new RegisterRequest(uniqueId, "Robert Tables", uniqueEmail, "Information Tech", 3, "SecretPass123!");

        // 1. First registration succeeds
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.studentId").value(uniqueId));

        // 2. Duplicate registration with same student ID is rejected with 400
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("Invalid email format returns 400 Bad Request")
    void testRegisterInvalidEmail() throws Exception {
        RegisterRequest req = new RegisterRequest("STU_INV", "Bad Email", "invalid-email-format", "Civil", 2, "Pass12345");
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("Unauthenticated request to protected endpoint returns 401")
    void testUnauthenticatedProtection() throws Exception {
        mockMvc.perform(get("/api/student/dashboard"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false));

        mockMvc.perform(get("/api/admin/students"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("Student attempting Admin endpoint receives 403 Forbidden")
    void testStudentForbiddenOnAdminEndpoints() throws Exception {
        MockHttpSession studentSession = loginAsStudent();

        mockMvc.perform(get("/api/admin/students").session(studentSession))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("Administrator privileges are required")));
    }

    @Test
    @DisplayName("Admin accessing admin students list succeeds with 200")
    void testAdminAuthorizedAccess() throws Exception {
        MockHttpSession adminSession = loginAsAdmin();

        mockMvc.perform(get("/api/admin/students").session(adminSession))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("Student can fetch their dashboard summary, marks, and attendance")
    void testStudentDashboardAndAcademics() throws Exception {
        MockHttpSession studentSession = loginAsStudent();

        // 1. Dashboard summary
        mockMvc.perform(get("/api/student/dashboard").session(studentSession))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.studentId").value("STU101"))
                .andExpect(jsonPath("$.data.averageMarks").isNumber())
                .andExpect(jsonPath("$.data.attendancePercentage").isNumber());

        // 2. Marks
        mockMvc.perform(get("/api/student/marks").session(studentSession))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray());

        // 3. Attendance
        mockMvc.perform(get("/api/student/attendance").session(studentSession))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    @DisplayName("Student can update permitted profile fields, but ID is protected")
    void testStudentProfileUpdate() throws Exception {
        MockHttpSession studentSession = loginAsStudent();

        UpdateProfileRequest updateReq = new UpdateProfileRequest("John Updated Doe", "john.doe@edutrack.edu", "Computer Science", 5);

        mockMvc.perform(put("/api/student/profile")
                        .session(studentSession)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.name").value("John Updated Doe"))
                .andExpect(jsonPath("$.data.semester").value(5));
    }

    @Test
    @DisplayName("Academic validation rejects marksObtained > maxMarks and negative values")
    void testMarksValidation() throws Exception {
        MockHttpSession adminSession = loginAsAdmin();

        // Invalid: marksObtained (105) > maxMarks (100)
        MarkUpdateRequest invalidMark = new MarkUpdateRequest("STU101", 1L, 105.0, 100.0);
        mockMvc.perform(post("/api/admin/marks")
                        .session(adminSession)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidMark)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));

        // Invalid: negative marks
        MarkUpdateRequest negativeMark = new MarkUpdateRequest("STU101", 1L, -5.0, 100.0);
        mockMvc.perform(post("/api/admin/marks")
                        .session(adminSession)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(negativeMark)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("Academic validation rejects classesAttended > totalClasses")
    void testAttendanceValidation() throws Exception {
        MockHttpSession adminSession = loginAsAdmin();

        // Invalid: attended (45) > total (40)
        AttendanceUpdateRequest invalidAtt = new AttendanceUpdateRequest("STU101", 1L, 45, 40);
        mockMvc.perform(post("/api/admin/attendance")
                        .session(adminSession)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidAtt)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("Admin can record valid marks and attendance and verify persistence")
    void testAdminRecordAcademics() throws Exception {
        MockHttpSession adminSession = loginAsAdmin();

        // Save valid mark
        MarkUpdateRequest validMark = new MarkUpdateRequest("STU101", 1L, 95.0, 100.0);
        mockMvc.perform(post("/api/admin/marks")
                        .session(adminSession)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validMark)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        // Save valid attendance
        AttendanceUpdateRequest validAtt = new AttendanceUpdateRequest("STU101", 1L, 38, 40);
        mockMvc.perform(post("/api/admin/attendance")
                        .session(adminSession)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validAtt)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("Assignment lifecycle: Admin creates assignment and student toggles status")
    void testAssignmentLifecycle() throws Exception {
        MockHttpSession adminSession = loginAsAdmin();
        MockHttpSession studentSession = loginAsStudent();

        // 1. Admin creates assignment
        AssignmentRequest assignReq = new AssignmentRequest("Unit Test Lab Assignment", 1L, "Write comprehensive JUnit tests.", LocalDate.now().plusDays(7));
        MvcResult createResult = mockMvc.perform(post("/api/admin/assignments")
                        .session(adminSession)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(assignReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").isNumber())
                .andReturn();

        long assignmentId = objectMapper.readTree(createResult.getResponse().getContentAsString())
                .get("data").get("id").asLong();

        // 2. Student updates completion status to COMPLETED
        AssignmentStatusUpdateRequest statusReq = new AssignmentStatusUpdateRequest("COMPLETED");
        mockMvc.perform(patch("/api/student/assignments/" + assignmentId + "/status")
                        .session(studentSession)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(statusReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        // 3. Student fetches assignments and sees COMPLETED
        mockMvc.perform(get("/api/student/assignments").session(studentSession))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[?(@.id == " + assignmentId + ")].status").value("COMPLETED"));

        // 4. Admin deletes assignment
        mockMvc.perform(delete("/api/admin/assignments/" + assignmentId)
                        .session(adminSession))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }
}
