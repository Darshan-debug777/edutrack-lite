-- ========================================================
-- EduTrack Lite Seed Data
-- Passwords:
-- Admin: AdminPassword123!
-- Students: StudentPassword123!
-- ========================================================

-- Insert Seed Users
INSERT INTO users (student_id, name, email, password_hash, role, branch, semester)
SELECT 'ADMIN001', 'Prof. Alan Turing', 'admin@edutrack.edu', '$2a$10$QaO8mw26pMRp4IRjjw6Ir.f8byaAp8X7McJVQR4BFsKJBl/h9kFR.', 'ADMIN', 'Computer Science', 0
WHERE NOT EXISTS (SELECT 1 FROM users WHERE student_id = 'ADMIN001');

INSERT INTO users (student_id, name, email, password_hash, role, branch, semester)
SELECT 'STU101', 'John Doe', 'john.doe@edutrack.edu', '$2a$10$0c8Q24kVdPVCwkyChLWqI.KbVOi8weHAuqD2QDjALruQD3E3kwj/a', 'STUDENT', 'Computer Science', 4
WHERE NOT EXISTS (SELECT 1 FROM users WHERE student_id = 'STU101');

INSERT INTO users (student_id, name, email, password_hash, role, branch, semester)
SELECT 'STU102', 'Jane Smith', 'jane.smith@edutrack.edu', '$2a$10$0c8Q24kVdPVCwkyChLWqI.KbVOi8weHAuqD2QDjALruQD3E3kwj/a', 'STUDENT', 'Computer Science', 4
WHERE NOT EXISTS (SELECT 1 FROM users WHERE student_id = 'STU102');

-- Insert Seed Subjects
INSERT INTO subjects (code, name, credits)
SELECT 'CS201', 'Data Structures & Algorithms', 4
WHERE NOT EXISTS (SELECT 1 FROM subjects WHERE code = 'CS201');

INSERT INTO subjects (code, name, credits)
SELECT 'CS202', 'Database Management Systems', 4
WHERE NOT EXISTS (SELECT 1 FROM subjects WHERE code = 'CS202');

INSERT INTO subjects (code, name, credits)
SELECT 'CS203', 'Java Object Oriented Programming', 3
WHERE NOT EXISTS (SELECT 1 FROM subjects WHERE code = 'CS203');

INSERT INTO subjects (code, name, credits)
SELECT 'CS204', 'Operating Systems', 3
WHERE NOT EXISTS (SELECT 1 FROM subjects WHERE code = 'CS204');

INSERT INTO subjects (code, name, credits)
SELECT 'CS205', 'Computer Networks', 3
WHERE NOT EXISTS (SELECT 1 FROM subjects WHERE code = 'CS205');

-- Insert Marks for STU101
INSERT INTO marks (student_id, subject_id, marks_obtained, max_marks)
SELECT 'STU101', s.id, 85.0, 100.0 FROM subjects s WHERE s.code = 'CS201'
AND NOT EXISTS (SELECT 1 FROM marks m JOIN subjects sub ON m.subject_id = sub.id WHERE m.student_id = 'STU101' AND sub.code = 'CS201');

INSERT INTO marks (student_id, subject_id, marks_obtained, max_marks)
SELECT 'STU101', s.id, 78.5, 100.0 FROM subjects s WHERE s.code = 'CS202'
AND NOT EXISTS (SELECT 1 FROM marks m JOIN subjects sub ON m.subject_id = sub.id WHERE m.student_id = 'STU101' AND sub.code = 'CS202');

INSERT INTO marks (student_id, subject_id, marks_obtained, max_marks)
SELECT 'STU101', s.id, 92.0, 100.0 FROM subjects s WHERE s.code = 'CS203'
AND NOT EXISTS (SELECT 1 FROM marks m JOIN subjects sub ON m.subject_id = sub.id WHERE m.student_id = 'STU101' AND sub.code = 'CS203');

INSERT INTO marks (student_id, subject_id, marks_obtained, max_marks)
SELECT 'STU101', s.id, 74.0, 100.0 FROM subjects s WHERE s.code = 'CS204'
AND NOT EXISTS (SELECT 1 FROM marks m JOIN subjects sub ON m.subject_id = sub.id WHERE m.student_id = 'STU101' AND sub.code = 'CS204');

INSERT INTO marks (student_id, subject_id, marks_obtained, max_marks)
SELECT 'STU101', s.id, 88.0, 100.0 FROM subjects s WHERE s.code = 'CS205'
AND NOT EXISTS (SELECT 1 FROM marks m JOIN subjects sub ON m.subject_id = sub.id WHERE m.student_id = 'STU101' AND sub.code = 'CS205');

-- Insert Attendance for STU101
INSERT INTO attendance (student_id, subject_id, classes_attended, total_classes)
SELECT 'STU101', s.id, 36, 40 FROM subjects s WHERE s.code = 'CS201'
AND NOT EXISTS (SELECT 1 FROM attendance a JOIN subjects sub ON a.subject_id = sub.id WHERE a.student_id = 'STU101' AND sub.code = 'CS201');

INSERT INTO attendance (student_id, subject_id, classes_attended, total_classes)
SELECT 'STU101', s.id, 32, 40 FROM subjects s WHERE s.code = 'CS202'
AND NOT EXISTS (SELECT 1 FROM attendance a JOIN subjects sub ON a.subject_id = sub.id WHERE a.student_id = 'STU101' AND sub.code = 'CS202');

INSERT INTO attendance (student_id, subject_id, classes_attended, total_classes)
SELECT 'STU101', s.id, 38, 40 FROM subjects s WHERE s.code = 'CS203'
AND NOT EXISTS (SELECT 1 FROM attendance a JOIN subjects sub ON a.subject_id = sub.id WHERE a.student_id = 'STU101' AND sub.code = 'CS203');

INSERT INTO attendance (student_id, subject_id, classes_attended, total_classes)
SELECT 'STU101', s.id, 30, 40 FROM subjects s WHERE s.code = 'CS204'
AND NOT EXISTS (SELECT 1 FROM attendance a JOIN subjects sub ON a.subject_id = sub.id WHERE a.student_id = 'STU101' AND sub.code = 'CS204');

INSERT INTO attendance (student_id, subject_id, classes_attended, total_classes)
SELECT 'STU101', s.id, 34, 40 FROM subjects s WHERE s.code = 'CS205'
AND NOT EXISTS (SELECT 1 FROM attendance a JOIN subjects sub ON a.subject_id = sub.id WHERE a.student_id = 'STU101' AND sub.code = 'CS205');

-- Insert Seed Assignments
INSERT INTO assignments (title, subject_id, description, due_date, created_by)
SELECT 'Implement Binary Search Tree in Java', s.id, 'Implement BST with insert, search, delete, and traversal methods.', '2026-10-25', 'ADMIN001'
FROM subjects s WHERE s.code = 'CS201'
AND NOT EXISTS (SELECT 1 FROM assignments WHERE title = 'Implement Binary Search Tree in Java');

INSERT INTO assignments (title, subject_id, description, due_date, created_by)
SELECT 'Normalize University Schema to 3NF', s.id, 'Identify functional dependencies and decompose relational tables to BCNF/3NF.', '2026-10-30', 'ADMIN001'
FROM subjects s WHERE s.code = 'CS202'
AND NOT EXISTS (SELECT 1 FROM assignments WHERE title = 'Normalize University Schema to 3NF');

INSERT INTO assignments (title, subject_id, description, due_date, created_by)
SELECT 'Spring Boot REST Controller Lab', s.id, 'Submit GitHub repo with unit tests for Spring Boot Student Controller.', '2026-10-05', 'ADMIN001'
FROM subjects s WHERE s.code = 'CS203'
AND NOT EXISTS (SELECT 1 FROM assignments WHERE title = 'Spring Boot REST Controller Lab');

INSERT INTO assignments (title, subject_id, description, due_date, created_by)
SELECT 'CPU Scheduling Algorithms Simulator', s.id, 'Simulate FCFS, SJF and Round Robin scheduling algorithms with turnaround times.', '2026-11-10', 'ADMIN001'
FROM subjects s WHERE s.code = 'CS204'
AND NOT EXISTS (SELECT 1 FROM assignments WHERE title = 'CPU Scheduling Algorithms Simulator');

-- Map Assignments for STU101
INSERT INTO student_assignments (assignment_id, student_id, status)
SELECT a.id, 'STU101', 'PENDING' FROM assignments a WHERE a.title = 'Implement Binary Search Tree in Java'
AND NOT EXISTS (SELECT 1 FROM student_assignments sa WHERE sa.assignment_id = a.id AND sa.student_id = 'STU101');

INSERT INTO student_assignments (assignment_id, student_id, status)
SELECT a.id, 'STU101', 'PENDING' FROM assignments a WHERE a.title = 'Normalize University Schema to 3NF'
AND NOT EXISTS (SELECT 1 FROM student_assignments sa WHERE sa.assignment_id = a.id AND sa.student_id = 'STU101');

INSERT INTO student_assignments (assignment_id, student_id, status)
SELECT a.id, 'STU101', 'PENDING' FROM assignments a WHERE a.title = 'Spring Boot REST Controller Lab'
AND NOT EXISTS (SELECT 1 FROM student_assignments sa WHERE sa.assignment_id = a.id AND sa.student_id = 'STU101');

INSERT INTO student_assignments (assignment_id, student_id, status)
SELECT a.id, 'STU101', 'COMPLETED' FROM assignments a WHERE a.title = 'CPU Scheduling Algorithms Simulator'
AND NOT EXISTS (SELECT 1 FROM student_assignments sa WHERE sa.assignment_id = a.id AND sa.student_id = 'STU101');
