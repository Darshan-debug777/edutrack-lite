import React, { useState, useEffect } from 'react';
import { api } from '../api/client';
import Modal from '../components/Modal';

export default function UnifiedDashboard() {
  // Main Data States
  const [students, setStudents] = useState([]);
  const [subjects, setSubjects] = useState([]);
  const [assignments, setAssignments] = useState([]);
  const [search, setSearch] = useState('');
  const [activeTab, setActiveTab] = useState('students'); // 'students', 'academics', 'assignments'

  // Loading & Feedback
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');

  // Selected Student for Academics
  const [selectedStudentId, setSelectedStudentId] = useState('');
  const [selectedStudentMarks, setSelectedStudentMarks] = useState([]);
  const [selectedStudentAtt, setSelectedStudentAtt] = useState([]);

  // Modals
  const [modalType, setModalType] = useState(null); // 'addStudent', 'editStudent'
  const [editTarget, setEditTarget] = useState(null);

  // Forms
  const [studentForm, setStudentForm] = useState({
    studentId: '',
    name: '',
    email: '',
    branch: 'Computer Science',
    semester: 4,
    password: 'StudentPassword123!',
  });

  const [marksForm, setMarksForm] = useState({
    subjectId: '',
    marksObtained: '',
    maxMarks: '100',
  });

  const [attForm, setAttForm] = useState({
    subjectId: '',
    classesAttended: '',
    totalClasses: '40',
  });

  const [assignmentForm, setAssignmentForm] = useState({
    title: '',
    subjectId: '',
    description: '',
    dueDate: new Date(Date.now() + 7 * 86400000).toISOString().split('T')[0],
  });

  useEffect(() => {
    loadAllData();
  }, []);

  const loadAllData = async () => {
    setLoading(true);
    setError('');
    try {
      const [stuRes, subRes, assignRes] = await Promise.all([
        api.get('/admin/students'),
        api.get('/admin/subjects'),
        api.get('/admin/assignments'),
      ]);

      if (stuRes && stuRes.success) {
        setStudents(stuRes.data || []);
        if (stuRes.data?.length > 0 && !selectedStudentId) {
          setSelectedStudentId(stuRes.data[0].studentId);
          loadStudentAcademics(stuRes.data[0].studentId);
        }
      }
      if (subRes && subRes.success) {
        setSubjects(subRes.data || []);
        if (subRes.data?.length > 0) {
          setMarksForm((prev) => ({ ...prev, subjectId: subRes.data[0].id }));
          setAttForm((prev) => ({ ...prev, subjectId: subRes.data[0].id }));
          setAssignmentForm((prev) => ({ ...prev, subjectId: subRes.data[0].id }));
        }
      }
      if (assignRes && assignRes.success) {
        setAssignments(assignRes.data || []);
      }
    } catch (err) {
      setError(err.message || 'Could not connect to EduTrack backend server.');
    } finally {
      setLoading(false);
    }
  };

  const loadStudentAcademics = async (studentId) => {
    if (!studentId) return;
    try {
      const [mRes, aRes] = await Promise.all([
        api.get(`/admin/students/${studentId}/marks`),
        api.get(`/admin/students/${studentId}/attendance`),
      ]);
      if (mRes && mRes.success) setSelectedStudentMarks(mRes.data || []);
      if (aRes && aRes.success) setSelectedStudentAtt(aRes.data || []);
    } catch {
      // non-fatal
    }
  };

  const handleSelectStudentChange = (id) => {
    setSelectedStudentId(id);
    loadStudentAcademics(id);
  };

  // ==========================================
  // Student CRUD Operations
  // ==========================================
  const handleOpenAddStudent = () => {
    setStudentForm({
      studentId: '',
      name: '',
      email: '',
      branch: 'Computer Science',
      semester: 4,
      password: 'StudentPassword123!',
    });
    setModalType('addStudent');
  };

  const handleOpenEditStudent = (student) => {
    setEditTarget(student);
    setStudentForm({
      studentId: student.studentId,
      name: student.name,
      email: student.email,
      branch: student.branch,
      semester: student.semester,
      password: '',
    });
    setModalType('editStudent');
  };

  const handleSaveStudent = async (e) => {
    e.preventDefault();
    setError('');
    setSuccess('');

    // Form validation
    if (!studentForm.name.trim() || !studentForm.email.trim()) {
      setError('Please provide both student name and email.');
      return;
    }

    try {
      if (modalType === 'addStudent') {
        if (!studentForm.studentId.trim()) {
          setError('Student ID is required.');
          return;
        }
        const res = await api.post('/admin/students', studentForm);
        if (res && res.success) {
          setSuccess(`Student ${studentForm.studentId} registered in database!`);
          setModalType(null);
          loadAllData();
        }
      } else {
        const res = await api.put(`/admin/students/${editTarget.studentId}`, {
          name: studentForm.name.trim(),
          email: studentForm.email.trim().toLowerCase(),
          branch: studentForm.branch,
          semester: Number(studentForm.semester),
        });
        if (res && res.success) {
          setSuccess(`Student ${editTarget.studentId} updated successfully in database!`);
          setModalType(null);
          loadAllData();
        }
      }
    } catch (err) {
      setError(err.message || 'Operation failed. Please check inputs.');
    }
  };

  const handleDeleteStudent = async (studentId) => {
    if (!window.confirm(`Permanently delete student ${studentId} and all associated academic records from MySQL?`)) {
      return;
    }
    setError('');
    try {
      const res = await api.delete(`/admin/students/${studentId}`);
      if (res && res.success) {
        setSuccess(`Student ${studentId} deleted.`);
        setStudents((prev) => prev.filter((s) => s.studentId !== studentId));
        if (selectedStudentId === studentId) {
          const remaining = students.filter((s) => s.studentId !== studentId);
          if (remaining.length > 0) {
            setSelectedStudentId(remaining[0].studentId);
            loadStudentAcademics(remaining[0].studentId);
          } else {
            setSelectedStudentId('');
            setSelectedStudentMarks([]);
            setSelectedStudentAtt([]);
          }
        }
      }
    } catch (err) {
      setError(err.message || 'Failed to delete student.');
    }
  };

  // ==========================================
  // Marks & Attendance Operations
  // ==========================================
  const handleSaveMark = async (e) => {
    e.preventDefault();
    if (!selectedStudentId) {
      setError('Please select a student first.');
      return;
    }
    setError('');
    setSuccess('');

    const obtained = parseFloat(marksForm.marksObtained);
    const max = parseFloat(marksForm.maxMarks);

    if (isNaN(obtained) || obtained < 0) {
      setError('Marks obtained cannot be negative.');
      return;
    }
    if (obtained > max) {
      setError(`Marks obtained (${obtained}) cannot exceed maximum marks (${max}).`);
      return;
    }

    try {
      const res = await api.post('/admin/marks', {
        studentId: selectedStudentId,
        subjectId: Number(marksForm.subjectId),
        marksObtained: obtained,
        maxMarks: max,
      });
      if (res && res.success) {
        setSuccess('Marks updated and saved to database!');
        loadStudentAcademics(selectedStudentId);
      }
    } catch (err) {
      setError(err.message || 'Failed to update marks.');
    }
  };

  const handleSaveAttendance = async (e) => {
    e.preventDefault();
    if (!selectedStudentId) {
      setError('Please select a student first.');
      return;
    }
    setError('');
    setSuccess('');

    const attended = parseInt(attForm.classesAttended, 10);
    const total = parseInt(attForm.totalClasses, 10);

    if (isNaN(attended) || attended < 0) {
      setError('Classes attended cannot be negative.');
      return;
    }
    if (attended > total) {
      setError(`Attended classes (${attended}) cannot exceed total classes (${total}).`);
      return;
    }

    try {
      const res = await api.post('/admin/attendance', {
        studentId: selectedStudentId,
        subjectId: Number(attForm.subjectId),
        classesAttended: attended,
        totalClasses: total,
      });
      if (res && res.success) {
        setSuccess('Attendance updated and saved to database!');
        loadStudentAcademics(selectedStudentId);
      }
    } catch (err) {
      setError(err.message || 'Failed to record attendance.');
    }
  };

  // ==========================================
  // Assignments Operations
  // ==========================================
  const handleCreateAssignment = async (e) => {
    e.preventDefault();
    if (!assignmentForm.title.trim() || !assignmentForm.dueDate) {
      setError('Assignment title and due date are required.');
      return;
    }
    setError('');
    setSuccess('');
    try {
      const res = await api.post('/admin/assignments', {
        title: assignmentForm.title.trim(),
        subjectId: Number(assignmentForm.subjectId),
        description: assignmentForm.description.trim(),
        dueDate: assignmentForm.dueDate,
      });
      if (res && res.success) {
        setSuccess('Assignment added to MySQL!');
        setAssignmentForm((prev) => ({ ...prev, title: '', description: '' }));
        const updated = await api.get('/admin/assignments');
        if (updated && updated.success) setAssignments(updated.data || []);
      }
    } catch (err) {
      setError(err.message || 'Failed to create assignment.');
    }
  };

  const handleToggleAssignment = async (assignmentId, currentStatus) => {
    setError('');
    const nextStatus = currentStatus === 'COMPLETED' ? 'PENDING' : 'COMPLETED';
    try {
      const res = await api.patch(`/admin/assignments/${assignmentId}/status`, {
        status: nextStatus,
        studentId: selectedStudentId || 'STU101',
      });
      if (res && res.success) {
        setAssignments((prev) =>
          prev.map((a) => (a.id === assignmentId ? { ...a, status: nextStatus } : a))
        );
        setSuccess(`Assignment marked as ${nextStatus}!`);
      }
    } catch (err) {
      setError(err.message || 'Failed to update assignment status.');
    }
  };

  const handleDeleteAssignment = async (assignmentId) => {
    if (!window.confirm('Delete this assignment permanently?')) return;
    try {
      const res = await api.delete(`/admin/assignments/${assignmentId}`);
      if (res && res.success) {
        setAssignments((prev) => prev.filter((a) => a.id !== assignmentId));
        setSuccess('Assignment deleted from database.');
      }
    } catch (err) {
      setError(err.message || 'Failed to delete assignment.');
    }
  };

  // Filtering students
  const filteredStudents = students.filter((s) => {
    if (!search.trim()) return true;
    const q = search.toLowerCase();
    return (
      s.studentId.toLowerCase().includes(q) ||
      s.name.toLowerCase().includes(q) ||
      s.branch.toLowerCase().includes(q) ||
      s.email.toLowerCase().includes(q)
    );
  });

  // Calculate Auto Percentage for Marks preview
  const previewMarkPct =
    marksForm.marksObtained && marksForm.maxMarks && Number(marksForm.maxMarks) > 0
      ? ((Number(marksForm.marksObtained) / Number(marksForm.maxMarks)) * 100).toFixed(1)
      : null;

  // Calculate Auto Percentage for Attendance preview
  const previewAttPct =
    attForm.classesAttended && attForm.totalClasses && Number(attForm.totalClasses) > 0
      ? ((Number(attForm.classesAttended) / Number(attForm.totalClasses)) * 100).toFixed(1)
      : null;

  return (
    <div style={{ maxWidth: 1160, margin: '0 auto', paddingBottom: '3rem' }}>
      {/* Top Header */}
      <div className="card" style={{ marginBottom: '1.25rem', background: 'linear-gradient(135deg, #ffffff 0%, #f8fafc 100%)' }}>
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', flexWrap: 'wrap', gap: '1rem' }}>
          <div>
            <div style={{ display: 'flex', alignItems: 'center', gap: '0.65rem', marginBottom: '0.2rem' }}>
              <div className="brand-icon">E</div>
              <h1 style={{ fontSize: '1.65rem' }}>EduTrack Lite</h1>
              <span className="badge badge-success">● Database Active</span>
            </div>
            <p style={{ fontSize: '0.875rem' }}>
              Student Management System — Add, Edit, Marks, Attendance & Assignments
            </p>
          </div>

          <div style={{ display: 'flex', gap: '0.5rem' }}>
            <button onClick={loadAllData} className="btn btn-secondary btn-sm" title="Refresh live records">
              ↻ Refresh
            </button>
            <button onClick={handleOpenAddStudent} className="btn btn-primary btn-sm">
              + Add Student
            </button>
          </div>
        </div>
      </div>

      {/* Global Alerts */}
      {error && (
        <div className="alert alert-danger" role="alert">
          <span>{error}</span>
          <button onClick={() => setError('')} className="btn btn-sm" style={{ background: 'transparent', border: 'none', color: 'inherit' }}>✕</button>
        </div>
      )}

      {success && (
        <div className="alert alert-success" role="alert">
          <span>{success}</span>
          <button onClick={() => setSuccess('')} className="btn btn-sm" style={{ background: 'transparent', border: 'none', color: 'inherit' }}>✕</button>
        </div>
      )}

      {/* Quick Stat Cards */}
      <div className="stats-grid" style={{ marginBottom: '1.5rem' }}>
        <div className="stat-card">
          <div className="stat-header">
            <span>TOTAL STUDENTS</span>
            <span>🎓</span>
          </div>
          <div className="stat-value">{students.length}</div>
          <div className="stat-subtext">Active records in database</div>
        </div>

        <div className="stat-card">
          <div className="stat-header">
            <span>SUBJECTS OFFERED</span>
            <span>📚</span>
          </div>
          <div className="stat-value">{subjects.length}</div>
          <div className="stat-subtext">Configured academic subjects</div>
        </div>

        <div className="stat-card">
          <div className="stat-header">
            <span>ASSIGNMENTS</span>
            <span>📝</span>
          </div>
          <div className="stat-value">{assignments.length}</div>
          <div className="stat-subtext">Tracked tasks and deadlines</div>
        </div>
      </div>

      {/* Main Tab Navigation */}
      <div style={{ display: 'flex', gap: '0.5rem', marginBottom: '1.25rem', borderBottom: '1px solid var(--border)', paddingBottom: '0.5rem' }}>
        <button
          className={`btn ${activeTab === 'students' ? 'btn-primary' : 'btn-secondary'} btn-sm`}
          onClick={() => setActiveTab('students')}
        >
          👨‍🎓 Students Roster ({students.length})
        </button>
        <button
          className={`btn ${activeTab === 'academics' ? 'btn-primary' : 'btn-secondary'} btn-sm`}
          onClick={() => setActiveTab('academics')}
        >
          📊 Marks & Attendance
        </button>
        <button
          className={`btn ${activeTab === 'assignments' ? 'btn-primary' : 'btn-secondary'} btn-sm`}
          onClick={() => setActiveTab('assignments')}
        >
          📋 Assignments ({assignments.length})
        </button>
      </div>

      {/* TAB 1: STUDENTS ROSTER */}
      {activeTab === 'students' && (
        <div className="card">
          <div className="card-header" style={{ flexWrap: 'wrap', gap: '0.75rem' }}>
            <h2 className="card-title">Student Records Table</h2>
            <div style={{ display: 'flex', gap: '0.5rem', flex: 1, maxWidth: 400 }}>
              <input
                type="text"
                className="form-input"
                placeholder="Search by ID, name, branch, or email..."
                value={search}
                onChange={(e) => setSearch(e.target.value)}
                style={{ fontSize: '0.85rem' }}
              />
              {search && (
                <button onClick={() => setSearch('')} className="btn btn-secondary btn-sm">
                  Clear
                </button>
              )}
            </div>
          </div>

          {loading ? (
            <div style={{ textAlign: 'center', padding: '2.5rem 0' }}>
              <p style={{ color: 'var(--text-muted)' }}>Loading student records from MySQL...</p>
            </div>
          ) : filteredStudents.length === 0 ? (
            <div style={{ textAlign: 'center', padding: '2.5rem 0' }}>
              <p style={{ color: 'var(--text-muted)' }}>No student records found.</p>
              <button onClick={handleOpenAddStudent} className="btn btn-primary btn-sm" style={{ marginTop: '0.75rem' }}>
                + Add First Student
              </button>
            </div>
          ) : (
            <div className="table-responsive">
              <table className="data-table">
                <thead>
                  <tr>
                    <th>Student ID</th>
                    <th>Full Name</th>
                    <th>Email</th>
                    <th>Branch</th>
                    <th>Semester</th>
                    <th style={{ textAlign: 'right' }}>Actions</th>
                  </tr>
                </thead>
                <tbody>
                  {filteredStudents.map((s) => (
                    <tr key={s.studentId}>
                      <td>
                        <span className="badge badge-info">{s.studentId}</span>
                      </td>
                      <td style={{ fontWeight: 600 }}>{s.name}</td>
                      <td>{s.email}</td>
                      <td>{s.branch}</td>
                      <td>Sem {s.semester}</td>
                      <td style={{ textAlign: 'right' }}>
                        <div style={{ display: 'inline-flex', gap: '0.35rem' }}>
                          <button
                            onClick={() => {
                              handleSelectStudentChange(s.studentId);
                              setActiveTab('academics');
                            }}
                            className="btn btn-secondary btn-sm"
                            title="Manage marks & attendance"
                          >
                            Academics
                          </button>
                          <button
                            onClick={() => handleOpenEditStudent(s)}
                            className="btn btn-secondary btn-sm"
                            title="Edit student"
                          >
                            Edit
                          </button>
                          <button
                            onClick={() => handleDeleteStudent(s.studentId)}
                            className="btn btn-outline-danger btn-sm"
                            title="Delete student"
                          >
                            Delete
                          </button>
                        </div>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </div>
      )}

      {/* TAB 2: MARKS & ATTENDANCE */}
      {activeTab === 'academics' && (
        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(320px, 1fr))', gap: '1.25rem' }}>
          {/* Left Column: Input Forms */}
          <div className="card">
            <div className="card-header">
              <h2 className="card-title">Enter Academic Data</h2>
            </div>

            {/* Student Selector */}
            <div className="form-group">
              <label className="form-label">Select Student</label>
              <select
                className="form-select"
                value={selectedStudentId}
                onChange={(e) => handleSelectStudentChange(e.target.value)}
              >
                {students.map((s) => (
                  <option key={s.studentId} value={s.studentId}>
                    {s.studentId} — {s.name} ({s.branch})
                  </option>
                ))}
              </select>
            </div>

            {/* Section A: Record Marks */}
            <form onSubmit={handleSaveMark} style={{ background: 'var(--bg-card-subtle)', padding: '1rem', borderRadius: 'var(--radius-md)', marginBottom: '1.25rem' }}>
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '0.5rem' }}>
                <h3 style={{ fontSize: '0.95rem' }}>Record Subject Marks</h3>
                {previewMarkPct !== null && (
                  <span className="badge badge-info">Calculated: {previewMarkPct}%</span>
                )}
              </div>

              <div className="form-group">
                <label className="form-label" style={{ fontSize: '0.8rem' }}>Subject</label>
                <select
                  className="form-select"
                  value={marksForm.subjectId}
                  onChange={(e) => setMarksForm({ ...marksForm, subjectId: e.target.value })}
                  required
                >
                  {subjects.map((sub) => (
                    <option key={sub.id} value={sub.id}>
                      {sub.code} — {sub.name}
                    </option>
                  ))}
                </select>
              </div>

              <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '0.5rem' }}>
                <div className="form-group">
                  <label className="form-label" style={{ fontSize: '0.8rem' }}>Marks Obtained</label>
                  <input
                    type="number"
                    step="0.5"
                    min="0"
                    max={marksForm.maxMarks}
                    className="form-input"
                    placeholder="e.g. 85.5"
                    value={marksForm.marksObtained}
                    onChange={(e) => setMarksForm({ ...marksForm, marksObtained: e.target.value })}
                    required
                  />
                </div>
                <div className="form-group">
                  <label className="form-label" style={{ fontSize: '0.8rem' }}>Max Marks</label>
                  <input
                    type="number"
                    min="1"
                    className="form-input"
                    value={marksForm.maxMarks}
                    onChange={(e) => setMarksForm({ ...marksForm, maxMarks: e.target.value })}
                    required
                  />
                </div>
              </div>

              <button type="submit" className="btn btn-primary btn-sm" style={{ width: '100%' }}>
                Save Marks to Database
              </button>
            </form>

            {/* Section B: Record Attendance */}
            <form onSubmit={handleSaveAttendance} style={{ background: 'var(--bg-card-subtle)', padding: '1rem', borderRadius: 'var(--radius-md)' }}>
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '0.5rem' }}>
                <h3 style={{ fontSize: '0.95rem' }}>Record Attendance</h3>
                {previewAttPct !== null && (
                  <span className={`badge ${Number(previewAttPct) >= 75 ? 'badge-success' : 'badge-danger'}`}>
                    Calculated: {previewAttPct}%
                  </span>
                )}
              </div>

              <div className="form-group">
                <label className="form-label" style={{ fontSize: '0.8rem' }}>Subject</label>
                <select
                  className="form-select"
                  value={attForm.subjectId}
                  onChange={(e) => setAttForm({ ...attForm, subjectId: e.target.value })}
                  required
                >
                  {subjects.map((sub) => (
                    <option key={sub.id} value={sub.id}>
                      {sub.code} — {sub.name}
                    </option>
                  ))}
                </select>
              </div>

              <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '0.5rem' }}>
                <div className="form-group">
                  <label className="form-label" style={{ fontSize: '0.8rem' }}>Classes Attended</label>
                  <input
                    type="number"
                    min="0"
                    max={attForm.totalClasses}
                    className="form-input"
                    placeholder="e.g. 34"
                    value={attForm.classesAttended}
                    onChange={(e) => setAttForm({ ...attForm, classesAttended: e.target.value })}
                    required
                  />
                </div>
                <div className="form-group">
                  <label className="form-label" style={{ fontSize: '0.8rem' }}>Total Classes</label>
                  <input
                    type="number"
                    min="1"
                    className="form-input"
                    value={attForm.totalClasses}
                    onChange={(e) => setAttForm({ ...attForm, totalClasses: e.target.value })}
                    required
                  />
                </div>
              </div>

              <button type="submit" className="btn btn-success btn-sm" style={{ width: '100%' }}>
                Save Attendance to Database
              </button>
            </form>
          </div>

          {/* Right Column: Live Student Summary Table */}
          <div className="card">
            <div className="card-header">
              <h2 className="card-title">
                Academic Records for {selectedStudentId}
              </h2>
              <button onClick={() => loadStudentAcademics(selectedStudentId)} className="btn btn-secondary btn-sm">
                ↻ Refresh
              </button>
            </div>

            <h3 style={{ fontSize: '0.95rem', marginBottom: '0.5rem' }}>Marks Table</h3>
            <div className="table-responsive" style={{ marginBottom: '1.25rem' }}>
              <table className="data-table" style={{ fontSize: '0.85rem' }}>
                <thead>
                  <tr>
                    <th>Subject</th>
                    <th>Marks</th>
                    <th>Max</th>
                    <th>Percentage</th>
                  </tr>
                </thead>
                <tbody>
                  {selectedStudentMarks.map((m) => (
                    <tr key={m.subjectId}>
                      <td><strong>{m.subjectCode}</strong>: {m.subjectName}</td>
                      <td style={{ fontWeight: 600 }}>{m.marksObtained}</td>
                      <td>{m.maxMarks}</td>
                      <td>
                        <span className={`badge ${m.percentage >= 75 ? 'badge-success' : 'badge-warning'}`}>
                          {m.percentage}%
                        </span>
                      </td>
                    </tr>
                  ))}
                  {selectedStudentMarks.length === 0 && (
                    <tr><td colSpan="4" style={{ textAlign: 'center' }}>No marks entered yet.</td></tr>
                  )}
                </tbody>
              </table>
            </div>

            <h3 style={{ fontSize: '0.95rem', marginBottom: '0.5rem' }}>Attendance Table</h3>
            <div className="table-responsive">
              <table className="data-table" style={{ fontSize: '0.85rem' }}>
                <thead>
                  <tr>
                    <th>Subject</th>
                    <th>Attended</th>
                    <th>Total</th>
                    <th>Percentage</th>
                  </tr>
                </thead>
                <tbody>
                  {selectedStudentAtt.map((a) => (
                    <tr key={a.subjectId}>
                      <td><strong>{a.subjectCode}</strong>: {a.subjectName}</td>
                      <td style={{ fontWeight: 600 }}>{a.classesAttended}</td>
                      <td>{a.totalClasses}</td>
                      <td>
                        <span className={`badge ${a.attendancePercentage >= 75 ? 'badge-success' : 'badge-danger'}`}>
                          {a.attendancePercentage}%
                        </span>
                      </td>
                    </tr>
                  ))}
                  {selectedStudentAtt.length === 0 && (
                    <tr><td colSpan="4" style={{ textAlign: 'center' }}>No attendance entered yet.</td></tr>
                  )}
                </tbody>
              </table>
            </div>
          </div>
        </div>
      )}

      {/* TAB 3: ASSIGNMENTS */}
      {activeTab === 'assignments' && (
        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(320px, 1fr))', gap: '1.25rem' }}>
          {/* Add Assignment Form */}
          <div className="card">
            <div className="card-header">
              <h2 className="card-title">Add Assignment</h2>
            </div>

            <form onSubmit={handleCreateAssignment}>
              <div className="form-group">
                <label className="form-label">Title</label>
                <input
                  type="text"
                  className="form-input"
                  placeholder="e.g. Implement Binary Search Tree"
                  value={assignmentForm.title}
                  onChange={(e) => setAssignmentForm({ ...assignmentForm, title: e.target.value })}
                  required
                />
              </div>

              <div className="form-group">
                <label className="form-label">Subject</label>
                <select
                  className="form-select"
                  value={assignmentForm.subjectId}
                  onChange={(e) => setAssignmentForm({ ...assignmentForm, subjectId: e.target.value })}
                  required
                >
                  {subjects.map((sub) => (
                    <option key={sub.id} value={sub.id}>
                      {sub.code} — {sub.name}
                    </option>
                  ))}
                </select>
              </div>

              <div className="form-group">
                <label className="form-label">Due Date</label>
                <input
                  type="date"
                  className="form-input"
                  value={assignmentForm.dueDate}
                  onChange={(e) => setAssignmentForm({ ...assignmentForm, dueDate: e.target.value })}
                  required
                />
              </div>

              <div className="form-group">
                <label className="form-label">Description</label>
                <textarea
                  className="form-textarea"
                  rows="3"
                  placeholder="Instructions or submission requirements..."
                  value={assignmentForm.description}
                  onChange={(e) => setAssignmentForm({ ...assignmentForm, description: e.target.value })}
                />
              </div>

              <button type="submit" className="btn btn-primary" style={{ width: '100%' }}>
                + Add Assignment to Database
              </button>
            </form>
          </div>

          {/* Assignments List */}
          <div className="card">
            <div className="card-header">
              <h2 className="card-title">Track Assignments ({assignments.length})</h2>
              <button onClick={loadAllData} className="btn btn-secondary btn-sm">
                ↻ Refresh
              </button>
            </div>

            {assignments.length === 0 ? (
              <p style={{ color: 'var(--text-muted)', textAlign: 'center', padding: '2rem 0' }}>
                No assignments recorded in database.
              </p>
            ) : (
              <div style={{ display: 'flex', flexDirection: 'column', gap: '0.75rem' }}>
                {assignments.map((a) => (
                  <div
                    key={a.id}
                    style={{
                      border: '1px solid var(--border)',
                      borderRadius: 'var(--radius-md)',
                      padding: '0.85rem',
                      display: 'flex',
                      justifyContent: 'space-between',
                      alignItems: 'center',
                      background: a.status === 'COMPLETED' ? '#f0fdf4' : 'var(--bg-card)',
                    }}
                  >
                    <div>
                      <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', marginBottom: '0.2rem' }}>
                        <span className="badge badge-info">{a.subjectCode}</span>
                        <strong style={{ fontSize: '0.95rem' }}>{a.title}</strong>
                        {a.status === 'COMPLETED' ? (
                          <span className="badge badge-success">✓ Completed</span>
                        ) : a.status === 'OVERDUE' ? (
                          <span className="badge badge-danger">⚠️ Overdue</span>
                        ) : (
                          <span className="badge badge-warning">⏳ Pending</span>
                        )}
                      </div>
                      <div style={{ fontSize: '0.8rem', color: 'var(--text-muted)' }}>
                        Due: <strong>{a.dueDate}</strong> &bull; {a.description || 'No description'}
                      </div>
                    </div>

                    <div style={{ display: 'flex', gap: '0.35rem' }}>
                      <button
                        onClick={() => handleToggleAssignment(a.id, a.status)}
                        className={`btn btn-sm ${a.status === 'COMPLETED' ? 'btn-secondary' : 'btn-success'}`}
                      >
                        {a.status === 'COMPLETED' ? 'Mark Pending' : '✓ Complete'}
                      </button>
                      <button
                        onClick={() => handleDeleteAssignment(a.id)}
                        className="btn btn-outline-danger btn-sm"
                      >
                        ✕
                      </button>
                    </div>
                  </div>
                ))}
              </div>
            )}
          </div>
        </div>
      )}

      {/* Add / Edit Student Modal */}
      <Modal
        isOpen={modalType === 'addStudent' || modalType === 'editStudent'}
        onClose={() => setModalType(null)}
        title={modalType === 'addStudent' ? 'Add New Student Record' : `Edit Student ${editTarget?.studentId}`}
      >
        <form onSubmit={handleSaveStudent}>
          {modalType === 'addStudent' && (
            <div className="form-group">
              <label className="form-label">Student ID</label>
              <input
                type="text"
                className="form-input"
                placeholder="e.g. STU104"
                value={studentForm.studentId}
                onChange={(e) => setStudentForm({ ...studentForm, studentId: e.target.value })}
                required
              />
            </div>
          )}

          <div className="form-group">
            <label className="form-label">Full Name</label>
            <input
              type="text"
              className="form-input"
              value={studentForm.name}
              onChange={(e) => setStudentForm({ ...studentForm, name: e.target.value })}
              required
            />
          </div>

          <div className="form-group">
            <label className="form-label">Email Address</label>
            <input
              type="email"
              className="form-input"
              value={studentForm.email}
              onChange={(e) => setStudentForm({ ...studentForm, email: e.target.value })}
              required
            />
          </div>

          <div style={{ display: 'grid', gridTemplateColumns: '1.5fr 1fr', gap: '0.75rem' }}>
            <div className="form-group">
              <label className="form-label">Branch</label>
              <select
                className="form-select"
                value={studentForm.branch}
                onChange={(e) => setStudentForm({ ...studentForm, branch: e.target.value })}
              >
                <option value="Computer Science">Computer Science</option>
                <option value="Information Technology">Information Technology</option>
                <option value="Electronics & Communication">Electronics & Communication</option>
                <option value="Mechanical Engineering">Mechanical Engineering</option>
                <option value="Civil Engineering">Civil Engineering</option>
                <option value="Electrical Engineering">Electrical Engineering</option>
              </select>
            </div>

            <div className="form-group">
              <label className="form-label">Semester</label>
              <input
                type="number"
                min="1"
                max="8"
                className="form-input"
                value={studentForm.semester}
                onChange={(e) => setStudentForm({ ...studentForm, semester: Number(e.target.value) })}
                required
              />
            </div>
          </div>

          <div style={{ display: 'flex', justifyContent: 'flex-end', gap: '0.5rem', marginTop: '1.25rem' }}>
            <button type="button" onClick={() => setModalType(null)} className="btn btn-secondary">
              Cancel
            </button>
            <button type="submit" className="btn btn-primary">
              Save Student to Database
            </button>
          </div>
        </form>
      </Modal>
    </div>
  );
}
