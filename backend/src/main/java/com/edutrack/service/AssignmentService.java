package com.edutrack.service;

import com.edutrack.exception.BadRequestException;
import com.edutrack.exception.ResourceNotFoundException;
import com.edutrack.model.Assignment;
import com.edutrack.model.dto.AssignmentRequest;
import com.edutrack.repository.AssignmentJdbcRepository;
import com.edutrack.repository.SubjectJdbcRepository;
import com.edutrack.repository.UserJdbcRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AssignmentService {

    private final AssignmentJdbcRepository assignmentRepository;
    private final SubjectJdbcRepository subjectRepository;
    private final UserJdbcRepository userRepository;

    public AssignmentService(AssignmentJdbcRepository assignmentRepository,
                             SubjectJdbcRepository subjectRepository,
                             UserJdbcRepository userRepository) {
        this.assignmentRepository = assignmentRepository;
        this.subjectRepository = subjectRepository;
        this.userRepository = userRepository;
    }

    public List<Assignment> getAssignmentsForStudent(String studentId) {
        if (!userRepository.existsByStudentId(studentId)) {
            throw new ResourceNotFoundException("Student not found with ID: " + studentId);
        }
        return assignmentRepository.findAllForStudent(studentId);
    }

    public List<Assignment> getAllAssignments() {
        return assignmentRepository.findAll();
    }

    public Assignment createAssignment(AssignmentRequest req, String adminId) {
        if (subjectRepository.findById(req.getSubjectId()).isEmpty()) {
            throw new ResourceNotFoundException("Subject not found with ID: " + req.getSubjectId());
        }

        Assignment assignment = new Assignment();
        assignment.setTitle(req.getTitle().trim());
        assignment.setSubjectId(req.getSubjectId());
        assignment.setDescription(req.getDescription() != null ? req.getDescription().trim() : "");
        assignment.setDueDate(req.getDueDate());
        assignment.setCreatedBy(adminId);

        return assignmentRepository.createAssignment(assignment);
    }

    public void updateAssignment(Long id, AssignmentRequest req) {
        if (assignmentRepository.findById(id).isEmpty()) {
            throw new ResourceNotFoundException("Assignment not found with ID: " + id);
        }
        if (subjectRepository.findById(req.getSubjectId()).isEmpty()) {
            throw new ResourceNotFoundException("Subject not found with ID: " + req.getSubjectId());
        }

        Assignment assignment = new Assignment();
        assignment.setTitle(req.getTitle().trim());
        assignment.setSubjectId(req.getSubjectId());
        assignment.setDescription(req.getDescription() != null ? req.getDescription().trim() : "");
        assignment.setDueDate(req.getDueDate());

        boolean ok = assignmentRepository.updateAssignment(id, assignment);
        if (!ok) {
            throw new RuntimeException("Failed to update assignment " + id);
        }
    }

    public void deleteAssignment(Long id) {
        if (assignmentRepository.findById(id).isEmpty()) {
            throw new ResourceNotFoundException("Assignment not found with ID: " + id);
        }
        boolean ok = assignmentRepository.deleteAssignment(id);
        if (!ok) {
            throw new RuntimeException("Failed to delete assignment " + id);
        }
    }

    public void updateStatus(Long id, String studentId, String status) {
        if (assignmentRepository.findById(id).isEmpty()) {
            throw new ResourceNotFoundException("Assignment not found with ID: " + id);
        }
        if (!userRepository.existsByStudentId(studentId)) {
            throw new ResourceNotFoundException("Student not found with ID: " + studentId);
        }
        if (!"PENDING".equalsIgnoreCase(status) && !"COMPLETED".equalsIgnoreCase(status)) {
            throw new BadRequestException("Status must be either 'PENDING' or 'COMPLETED'");
        }

        assignmentRepository.updateStudentStatus(id, studentId, status);
    }
}
