-- ========================================================
-- EduTrack Lite Database Schema (PostgreSQL Compatible)
-- Safe, non-destructive table creation
-- ========================================================

CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    student_id VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL DEFAULT 'STUDENT',
    branch VARCHAR(50) NOT NULL,
    semester INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS subjects (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(20) NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL,
    credits INT DEFAULT 3
);

CREATE TABLE IF NOT EXISTS marks (
    id BIGSERIAL PRIMARY KEY,
    student_id VARCHAR(50) NOT NULL,
    subject_id BIGINT NOT NULL,
    marks_obtained DOUBLE PRECISION NOT NULL,
    max_marks DOUBLE PRECISION NOT NULL DEFAULT 100.0,
    CONSTRAINT fk_marks_user FOREIGN KEY (student_id) REFERENCES users(student_id) ON DELETE CASCADE,
    CONSTRAINT fk_marks_subject FOREIGN KEY (subject_id) REFERENCES subjects(id) ON DELETE CASCADE,
    CONSTRAINT uk_student_subject_mark UNIQUE (student_id, subject_id)
);

CREATE TABLE IF NOT EXISTS attendance (
    id BIGSERIAL PRIMARY KEY,
    student_id VARCHAR(50) NOT NULL,
    subject_id BIGINT NOT NULL,
    classes_attended INT NOT NULL DEFAULT 0,
    total_classes INT NOT NULL DEFAULT 0,
    CONSTRAINT fk_att_user FOREIGN KEY (student_id) REFERENCES users(student_id) ON DELETE CASCADE,
    CONSTRAINT fk_att_subject FOREIGN KEY (subject_id) REFERENCES subjects(id) ON DELETE CASCADE,
    CONSTRAINT uk_student_subject_att UNIQUE (student_id, subject_id)
);

CREATE TABLE IF NOT EXISTS assignments (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(150) NOT NULL,
    subject_id BIGINT NOT NULL,
    description TEXT,
    due_date DATE NOT NULL,
    created_by VARCHAR(50) DEFAULT 'ADMIN',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_assign_subject FOREIGN KEY (subject_id) REFERENCES subjects(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS student_assignments (
    id BIGSERIAL PRIMARY KEY,
    assignment_id BIGINT NOT NULL,
    student_id VARCHAR(50) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_sa_assign FOREIGN KEY (assignment_id) REFERENCES assignments(id) ON DELETE CASCADE,
    CONSTRAINT fk_sa_user FOREIGN KEY (student_id) REFERENCES users(student_id) ON DELETE CASCADE,
    CONSTRAINT uk_student_assignment UNIQUE (assignment_id, student_id)
);