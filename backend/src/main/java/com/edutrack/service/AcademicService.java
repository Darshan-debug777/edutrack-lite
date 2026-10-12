package com.edutrack.service;

import com.edutrack.exception.BadRequestException;
import com.edutrack.exception.ResourceNotFoundException;
import com.edutrack.model.Attendance;
import com.edutrack.model.Mark;
import com.edutrack.model.Subject;
import com.edutrack.model.dto.AttendanceUpdateRequest;
import com.edutrack.model.dto.MarkUpdateRequest;
import com.edutrack.repository.AcademicJdbcRepository;
import com.edutrack.repository.SubjectJdbcRepository;
import com.edutrack.repository.UserJdbcRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AcademicService {

    private final AcademicJdbcRepository academicRepository;
    private final SubjectJdbcRepository subjectRepository;
    private final UserJdbcRepository userRepository;

    public AcademicService(AcademicJdbcRepository academicRepository,
                           SubjectJdbcRepository subjectRepository,
                           UserJdbcRepository userRepository) {
        this.academicRepository = academicRepository;
        this.subjectRepository = subjectRepository;
        this.userRepository = userRepository;
    }

    public List<Subject> getAllSubjects() {
        return subjectRepository.findAll();
    }

    public List<Mark> getMarksForStudent(String studentId) {
        verifyStudentExists(studentId);
        return academicRepository.findMarksByStudentId(studentId);
    }

    public List<Attendance> getAttendanceForStudent(String studentId) {
        verifyStudentExists(studentId);
        return academicRepository.findAttendanceByStudentId(studentId);
    }

    public void updateMark(MarkUpdateRequest req) {
        verifyStudentExists(req.getStudentId());
        verifySubjectExists(req.getSubjectId());

        if (req.getMaxMarks() == null || req.getMaxMarks() <= 0) {
            throw new BadRequestException("Maximum marks must be greater than zero.");
        }
        if (req.getMarksObtained() == null || req.getMarksObtained() < 0) {
            throw new BadRequestException("Marks obtained cannot be negative.");
        }
        if (req.getMarksObtained() > req.getMaxMarks()) {
            throw new BadRequestException("Marks obtained (" + req.getMarksObtained() +
                    ") cannot exceed maximum marks (" + req.getMaxMarks() + ").");
        }

        academicRepository.saveOrUpdateMark(
                req.getStudentId().trim(),
                req.getSubjectId(),
                req.getMarksObtained(),
                req.getMaxMarks()
        );
    }

    public void updateAttendance(AttendanceUpdateRequest req) {
        verifyStudentExists(req.getStudentId());
        verifySubjectExists(req.getSubjectId());

        if (req.getTotalClasses() == null || req.getTotalClasses() < 0) {
            throw new BadRequestException("Total classes cannot be negative.");
        }
        if (req.getClassesAttended() == null || req.getClassesAttended() < 0) {
            throw new BadRequestException("Classes attended cannot be negative.");
        }
        if (req.getClassesAttended() > req.getTotalClasses()) {
            throw new BadRequestException("Classes attended (" + req.getClassesAttended() +
                    ") cannot exceed total classes conducted (" + req.getTotalClasses() + ").");
        }

        academicRepository.saveOrUpdateAttendance(
                req.getStudentId().trim(),
                req.getSubjectId(),
                req.getClassesAttended(),
                req.getTotalClasses()
        );
    }

    private void verifyStudentExists(String studentId) {
        if (!userRepository.existsByStudentId(studentId)) {
            throw new ResourceNotFoundException("Student not found with ID: " + studentId);
        }
    }

    private void verifySubjectExists(Long subjectId) {
        if (subjectRepository.findById(subjectId).isEmpty()) {
            throw new ResourceNotFoundException("Subject not found with ID: " + subjectId);
        }
    }
}
