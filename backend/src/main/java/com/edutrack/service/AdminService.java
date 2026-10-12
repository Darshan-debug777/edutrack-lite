package com.edutrack.service;

import com.edutrack.exception.ResourceNotFoundException;
import com.edutrack.model.User;
import com.edutrack.repository.UserJdbcRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminService {

    private final UserJdbcRepository userRepository;

    public AdminService(UserJdbcRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> getAllStudents(String search) {
        return userRepository.findAllStudents(search);
    }

    public void deleteStudent(String studentId) {
        if (!userRepository.existsByStudentId(studentId)) {
            throw new ResourceNotFoundException("Student not found with ID: " + studentId);
        }
        boolean deleted = userRepository.deleteByStudentId(studentId);
        if (!deleted) {
            throw new RuntimeException("Failed to delete student with ID: " + studentId);
        }
    }
}
