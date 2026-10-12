package com.edutrack.service;

import com.edutrack.exception.BadRequestException;
import com.edutrack.exception.ResourceNotFoundException;
import com.edutrack.model.DashboardSummary;
import com.edutrack.model.User;
import com.edutrack.model.dto.UpdateProfileRequest;
import com.edutrack.repository.AcademicJdbcRepository;
import com.edutrack.repository.UserJdbcRepository;
import org.springframework.stereotype.Service;

@Service
public class StudentService {

    private final UserJdbcRepository userRepository;
    private final AcademicJdbcRepository academicRepository;

    public StudentService(UserJdbcRepository userRepository, AcademicJdbcRepository academicRepository) {
        this.userRepository = userRepository;
        this.academicRepository = academicRepository;
    }

    public User getProfile(String studentId) {
        return userRepository.findByStudentId(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with ID: " + studentId));
    }

    public User updateProfile(String studentId, UpdateProfileRequest req) {
        User existing = getProfile(studentId);

        String newEmail = req.getEmail().trim().toLowerCase();
        if (userRepository.existsByEmailExcludingStudent(newEmail, studentId)) {
            throw new BadRequestException("Email '" + newEmail + "' is already in use by another student.");
        }

        boolean updated = userRepository.updateProfile(
                studentId,
                req.getName().trim(),
                newEmail,
                req.getBranch().trim(),
                req.getSemester()
        );

        if (!updated) {
            throw new RuntimeException("Failed to update profile for student " + studentId);
        }

        existing.setName(req.getName().trim());
        existing.setEmail(newEmail);
        existing.setBranch(req.getBranch().trim());
        existing.setSemester(req.getSemester());
        return existing;
    }

    public DashboardSummary getDashboardSummary(String studentId) {
        User student = getProfile(studentId);
        return academicRepository.getDashboardSummary(
                student.getStudentId(),
                student.getName(),
                student.getBranch(),
                student.getSemester()
        );
    }
}
