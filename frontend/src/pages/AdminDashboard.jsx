import React, { useState, useEffect } from 'react';
import { api } from '../api/client';
import Modal from '../components/Modal';

export default function AdminDashboard() {
  const [students, setStudents] = useState([]);
  const [subjects, setSubjects] = useState([]);
  const [search, setSearch] = useState('');
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');

  // Modals state
  const [activeModal, setActiveModal] = useState(null); // 'addStudent', 'editStudent', 'marks', 'attendance', 'assignments', 'addAssignment'
  const [selectedStudent, setSelectedStudent] = useState(null);

  // Student Form State
  const [studentForm, setStudentForm] = useState({
    studentId: '',
    name: '',
    email: '',
    branch: 'Computer Science',
    semester: 4,
    password: '',
  });

  // Marks Form State
  const [studentMarks, setStudentMarks] = useState([]);
  const [marksForm, setMarksForm] = useState({ subjectId: '', marksObtained: '', maxMarks: '100' });

  // Attendance Form State
  const [studentAttendance, setStudentAttendance] = useState([]);
  const [attForm, setAttForm] = useState({ subjectId: '', classesAttended: '', totalClasses: '40' });

  // Assignments State
  const [allAssignments, setAllAssignments] = useState([]);
  const [assignmentForm, setAssignmentForm] = useState({
    title: '',
    subjectId: '',
    description: '',
    dueDate: '',
  });

  useEffect(() => {
    fetchInitialData();
  }, []);

  const fetchInitialData = async () => {
    setLoading(true);
    setError('');
    try {
      const [stuRes, subRes] = await Promise.all([
        api.get('/admin/students'),
        api.get('/admin/subjects'),
      ]);

      if (stuRes && stuRes.success) setStudents(stuRes.data || []);
      if (subRes && subRes.success) {
        setSubjects(subRes.data || []);
        if (subRes.data?.length > 0) {
          setMarksForm((prev) => ({ ...prev, subjectId: subRes.data[0].id }));
          setAttForm((prev) => ({ ...prev, subjectId: subRes.data[0].id }));
          setAssignmentForm((prev) => ({ ...prev, subjectId: subRes.data[0].id }));
        }
      }
    } catch (err) {
      setError(err.message || 'Failed to initialize admin dashboard');
    } finally {
      setLoading(false);
    }
  };

  const handleSearch = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError('');
    try {
      const endpoint = search.trim() ? `/admin/students?search=${encodeURIComponent(search.trim())}` : '/admin/students';
      const res = await api.get(endpoint);
      if (res && res.success) {
        setStudents(res.data || []);
      }
    } catch (err) {
      setError(err.message || 'Error searching student records.');
    } finally {
      setLoading(false);
    }
  };

  // Add / Edit Student
  const handleOpenAddStudent = () => {
    setStudentForm({
      studentId: '',
      name: '',
      email: '',
      branch: 'Computer Science',
      semester: 4,
      password: 'StudentPassword123!',
    });
    setActiveModal('addStudent');
  };

  const handleOpenEditStudent = (student) => {
    setSelectedStudent(student);
    setStudentForm({
      studentId: student.studentId,
      name: student.name,
      email: student.email,
      branch: student.branch,
      semester: student.semester,
      password: '',
    });
    setActiveModal('editStudent');
  };

  const handleSaveStudent = async (e) => {
    e.preventDefault();
    setError('');
    try {
      if (activeModal === 'addStudent') {
        const res = await api.post('/admin/students', studentForm);
        if (res && res.success) {
          setSuccess(`Student ${studentForm.studentId} created in database!`);
          setActiveModal(null);
          fetchInitialData();
        }
      } else if (activeModal === 'editStudent') {
        const res = await api.put(`/admin/students/${selectedStudent.studentId}`, {
          name: studentForm.name,
          email: studentForm.email,
          branch: studentForm.branch,
          semester: Number(studentForm.semester),
        });
        if (res && res.success) {
          setSuccess(`Student ${selectedStudent.studentId} updated successfully!`);
          setActiveModal(null);
          fetchInitialData();
        }
      }
    } catch (err) {
      setError(err.message || 'Failed to save student record.');
    }
  };

  const handleDeleteStudent = async (studentId) => {
    if (!window.confirm(`Are you sure you want to permanently delete student ${studentId}? All associated marks and attendance records will be removed.`)) {
      return;
    }
    setError('');
    try {
      const res = await api.delete(`/admin/students/${studentId}`);
      if (res && res.success) {
        setSuccess(`Student ${studentId} deleted successfully.`);
        setStudents((prev) => prev.filter((s) => s.studentId !== studentId));
      }
    } catch (err) {
      setError(err.message || 'Failed to delete student.');
    }
  };

  // Manage Marks
  const handleOpenMarks = async (student) => {
    setSelectedStudent(student);
    setError('');
    try {
      const res = await api.get(`/admin/students/${student.studentId}/marks`);
      if (res && res.success) {
        setStudentMarks(res.data || []);
        setActiveModal('marks');
      }
    } catch (err) {
      setError(err.message || 'Failed to load marks.');
    }
  };

  const handleSaveMark = async (e) => {
    e.preventDefault();
    setError('');
    try {
      const res = await api.post('/admin/marks', {
        studentId: selectedStudent.studentId,
        subjectId: Number(marksForm.subjectId),
        marksObtained: parseFloat(marksForm.marksObtained),
        maxMarks: parseFloat(marksForm.maxMarks),
      });
      if (res && res.success) {
        setSuccess('Marks updated successfully in MySQL!');
        const updatedMarks = await api.get(`/admin/students/${selectedStudent.studentId}/marks`);
        if (updatedMarks && updatedMarks.success) {
          setStudentMarks(updatedMarks.data || []);
        }
      }
    } catch (err) {
      setError(err.message || 'Failed to update marks.');
    }
  };

  // Manage Attendance
  const handleOpenAttendance = async (student) => {
    setSelectedStudent(student);
    setError('');
    try {
      const res = await api.get(`/admin/students/${student.studentId}/attendance`);
      if (res && res.success) {
        setStudentAttendance(res.data || []);
        setActiveModal('attendance');
      }
    } catch (err) {
      setError(err.message || 'Failed to load attendance.');
    }
  };

  const handleSaveAttendance = async (e) => {
    e.preventDefault();
    setError('');
    try {
      const res = await api.post('/admin/attendance', {
        studentId: selectedStudent.studentId,
        subjectId: Number(attForm.subjectId),
        classesAttended: parseInt(attForm.classesAttended, 10),
        totalClasses: parseInt(attForm.totalClasses, 10),
      });
      if (res && res.success) {
        setSuccess('Attendance recorded successfully in MySQL!');
        const updatedAtt = await api.get(`/admin/students/${selectedStudent.studentId}/attendance`);
        if (updatedAtt && updatedAtt.success) {
          setStudentAttendance(updatedAtt.data || []);
        }
      }
    } catch (err) {
      setError(err.message || 'Failed to record attendance.');
    }
  };

  // Manage Assignments
  const handleOpenAssignments = async () => {
    setError('');
    try {
      const res = await api.get('/admin/assignments');
      if (res && res.success) {
        setAllAssignments(res.data || []);
        setActiveModal('assignments');
      }
    } catch (err) {
      setError(err.message || 'Failed to load assignments.');
    }
  };

  const handleCreateAssignment = async (e) => {
    e.preventDefault();
    setError('');
    try {
      const res = await api.post('/admin/assignments', {
        title: assignmentForm.title,
        subjectId: Number(assignmentForm.subjectId),
        description: assignmentForm.description,
        dueDate: assignmentForm.dueDate,
      });
      if (res && res.success) {
        setSuccess('New assignment published successfully!');
        const updated = await api.get('/admin/assignments');
        if (updated && updated.success) setAllAssignments(updated.data || []);
        setAssignmentForm((prev) => ({ ...prev, title: '', description: '', dueDate: '' }));
      }
    } catch (err) {
      setError(err.message || 'Failed to create assignment.');
    }
  };

  const handleDeleteAssignment = async (id) => {
    if (!window.confirm('Delete this assignment permanently?')) return;
    try {
      const res = await api.delete(`/admin/assignments/${id}`);
      if (res && res.success) {
        setAllAssignments((prev) => prev.filter((a) => a.id !== id));
        setSuccess('Assignment deleted successfully.');
      }
    } catch (err) {
      setError(err.message || 'Failed to delete assignment.');
    }
  };

  return (
    <div>
      {/* Header Banner */}
      <div className="card" style={{ marginBottom: '1.5rem' }}>
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', flexWrap: 'wrap', gap: '1rem' }}>
          <div>
            <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', marginBottom: '0.25rem' }}>
              <h1 className="card-title" style={{ fontSize: '1.5rem' }}>Administrator Dashboard</h1>
              <span className="badge badge-warning">Admin Console</span>
            </div>
            <p style={{ fontSize: '0.85rem' }}>
              Manage students, course marks, attendance, and assignment submissions directly in MySQL
            </p>
          </div>

          <div style={{ display: 'flex', gap: '0.5rem', flexWrap: 'wrap' }}>
            <button onClick={handleOpenAddStudent} className="btn btn-primary btn-sm">
              + Add Student
            </button>
            <button onClick={handleOpenAssignments} className="btn btn-secondary btn-sm">
              📋 Manage Assignments
            </button>
            <button onClick={fetchInitialData} className="btn btn-secondary btn-sm">
              ↻ Refresh
            </button>
          </div>
        </div>
      </div>

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

      {/* Search and Filter Bar */}
      <div className="card" style={{ marginBottom: '1.5rem', padding: '1rem 1.25rem' }}>
        <form onSubmit={handleSearch} style={{ display: 'flex', gap: '0.75rem', alignItems: 'center' }}>
          <input
            type="text"
            className="form-input"
            placeholder="Search by student ID, full name, email, or branch..."
            value={search}
            onChange={(e) => setSearch(e.target.value)}
            style={{ flex: 1 }}
          />
          <button type="submit" className="btn btn-primary btn-sm">
            🔍 Search
          </button>
          {search && (
            <button
              type="button"
              onClick={() => { setSearch(''); fetchInitialData(); }}
              className="btn btn-secondary btn-sm"
            >
              Clear
            </button>
          )}
        </form>
      </div>

      {/* Students Data Table */}
      <div className="card">
        <div className="card-header">
          <h2 className="card-title" style={{ fontSize: '1.15rem' }}>
            Enrolled Students ({students.length})
          </h2>
        </div>

        {loading ? (
          <div style={{ textAlign: 'center', padding: '2.5rem 0' }}>
            <p style={{ color: 'var(--text-muted)' }}>Querying student records from database...</p>
          </div>
        ) : students.length === 0 ? (
          <div style={{ textAlign: 'center', padding: '2.5rem 0' }}>
            <p style={{ color: 'var(--text-muted)' }}>No student records found.</p>
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
                  <th>Sem</th>
                  <th style={{ textAlign: 'right' }}>Actions</th>
                </tr>
              </thead>
              <tbody>
                {students.map((s) => (
                  <tr key={s.studentId}>
                    <td>
                      <span className="badge badge-info">{s.studentId}</span>
                    </td>
                    <td style={{ fontWeight: 600 }}>{s.name}</td>
                    <td>{s.email}</td>
                    <td>{s.branch}</td>
                    <td>{s.semester}</td>
                    <td style={{ textAlign: 'right' }}>
                      <div style={{ display: 'inline-flex', gap: '0.35rem' }}>
                        <button
                          onClick={() => handleOpenMarks(s)}
                          className="btn btn-secondary btn-sm"
                          title="Record subject marks"
                        >
                          Marks
                        </button>
                        <button
                          onClick={() => handleOpenAttendance(s)}
                          className="btn btn-secondary btn-sm"
                          title="Record attendance"
                        >
                          Attendance
                        </button>
                        <button
                          onClick={() => handleOpenEditStudent(s)}
                          className="btn btn-secondary btn-sm"
                          title="Edit profile"
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

      {/* Add / Edit Student Modal */}
      <Modal
        isOpen={activeModal === 'addStudent' || activeModal === 'editStudent'}
        onClose={() => setActiveModal(null)}
        title={activeModal === 'addStudent' ? 'Add New Student Record' : `Edit Student ${selectedStudent?.studentId}`}
      >
        <form onSubmit={handleSaveStudent}>
          {activeModal === 'addStudent' && (
            <div className="form-group">
              <label className="form-label">Student ID (Roll Number)</label>
              <input
                type="text"
                className="form-input"
                placeholder="e.g. STU103"
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

          {activeModal === 'addStudent' && (
            <div className="form-group">
              <label className="form-label">Initial Password</label>
              <input
                type="password"
                className="form-input"
                value={studentForm.password}
                onChange={(e) => setStudentForm({ ...studentForm, password: e.target.value })}
                required
              />
            </div>
          )}

          <div style={{ display: 'flex', justifyContent: 'flex-end', gap: '0.5rem', marginTop: '1.25rem' }}>
            <button type="button" onClick={() => setActiveModal(null)} className="btn btn-secondary">
              Cancel
            </button>
            <button type="submit" className="btn btn-primary">
              Save Student
            </button>
          </div>
        </form>
      </Modal>

      {/* Manage Marks Modal */}
      <Modal
        isOpen={activeModal === 'marks'}
        onClose={() => setActiveModal(null)}
        title={`Academic Marks: ${selectedStudent?.name} (${selectedStudent?.studentId})`}
      >
        <div style={{ marginBottom: '1.25rem' }}>
          <h4 style={{ fontSize: '0.95rem', marginBottom: '0.5rem' }}>Current Recorded Marks:</h4>
          <div className="table-responsive">
            <table className="data-table" style={{ fontSize: '0.85rem' }}>
              <thead>
                <tr>
                  <th>Subject</th>
                  <th>Marks</th>
                  <th>Max</th>
                  <th>%</th>
                </tr>
              </thead>
              <tbody>
                {studentMarks.map((m) => (
                  <tr key={m.subjectId}>
                    <td><strong>{m.subjectCode}</strong>: {m.subjectName}</td>
                    <td>{m.marksObtained}</td>
                    <td>{m.maxMarks}</td>
                    <td>{m.percentage}%</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>

        <form onSubmit={handleSaveMark} style={{ borderTop: '1px solid var(--border)', paddingTop: '1rem' }}>
          <h4 style={{ fontSize: '0.95rem', marginBottom: '0.75rem' }}>Record or Update Marks:</h4>
          <div className="form-group">
            <label className="form-label">Subject</label>
            <select
              className="form-select"
              value={marksForm.subjectId}
              onChange={(e) => setMarksForm({ ...marksForm, subjectId: e.target.value })}
              required
            >
              {subjects.map((s) => (
                <option key={s.id} value={s.id}>
                  {s.code} - {s.name}
                </option>
              ))}
            </select>
          </div>

          <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '0.75rem' }}>
            <div className="form-group">
              <label className="form-label">Marks Obtained</label>
              <input
                type="number"
                step="0.5"
                min="0"
                max={marksForm.maxMarks}
                className="form-input"
                placeholder="e.g. 88.5"
                value={marksForm.marksObtained}
                onChange={(e) => setMarksForm({ ...marksForm, marksObtained: e.target.value })}
                required
              />
            </div>
            <div className="form-group">
              <label className="form-label">Maximum Marks</label>
              <input
                type="number"
                step="1"
                min="1"
                className="form-input"
                value={marksForm.maxMarks}
                onChange={(e) => setMarksForm({ ...marksForm, maxMarks: e.target.value })}
                required
              />
            </div>
          </div>

          <div style={{ display: 'flex', justifyContent: 'flex-end', gap: '0.5rem', marginTop: '1rem' }}>
            <button type="button" onClick={() => setActiveModal(null)} className="btn btn-secondary">
              Close
            </button>
            <button type="submit" className="btn btn-success">
              Save Marks to MySQL
            </button>
          </div>
        </form>
      </Modal>

      {/* Manage Attendance Modal */}
      <Modal
        isOpen={activeModal === 'attendance'}
        onClose={() => setActiveModal(null)}
        title={`Attendance Records: ${selectedStudent?.name} (${selectedStudent?.studentId})`}
      >
        <div style={{ marginBottom: '1.25rem' }}>
          <h4 style={{ fontSize: '0.95rem', marginBottom: '0.5rem' }}>Current Attendance:</h4>
          <div className="table-responsive">
            <table className="data-table" style={{ fontSize: '0.85rem' }}>
              <thead>
                <tr>
                  <th>Subject</th>
                  <th>Attended</th>
                  <th>Total</th>
                  <th>%</th>
                </tr>
              </thead>
              <tbody>
                {studentAttendance.map((a) => (
                  <tr key={a.subjectId}>
                    <td><strong>{a.subjectCode}</strong>: {a.subjectName}</td>
                    <td>{a.classesAttended}</td>
                    <td>{a.totalClasses}</td>
                    <td>{a.attendancePercentage}%</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>

        <form onSubmit={handleSaveAttendance} style={{ borderTop: '1px solid var(--border)', paddingTop: '1rem' }}>
          <h4 style={{ fontSize: '0.95rem', marginBottom: '0.75rem' }}>Record or Update Attendance:</h4>
          <div className="form-group">
            <label className="form-label">Subject</label>
            <select
              className="form-select"
              value={attForm.subjectId}
              onChange={(e) => setAttForm({ ...attForm, subjectId: e.target.value })}
              required
            >
              {subjects.map((s) => (
                <option key={s.id} value={s.id}>
                  {s.code} - {s.name}
                </option>
              ))}
            </select>
          </div>

          <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '0.75rem' }}>
            <div className="form-group">
              <label className="form-label">Classes Attended</label>
              <input
                type="number"
                min="0"
                max={attForm.totalClasses}
                className="form-input"
                placeholder="e.g. 36"
                value={attForm.classesAttended}
                onChange={(e) => setAttForm({ ...attForm, classesAttended: e.target.value })}
                required
              />
            </div>
            <div className="form-group">
              <label className="form-label">Total Conducted</label>
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

          <div style={{ display: 'flex', justifyContent: 'flex-end', gap: '0.5rem', marginTop: '1rem' }}>
            <button type="button" onClick={() => setActiveModal(null)} className="btn btn-secondary">
              Close
            </button>
            <button type="submit" className="btn btn-success">
              Save Attendance to MySQL
            </button>
          </div>
        </form>
      </Modal>

      {/* Manage Assignments Modal */}
      <Modal
        isOpen={activeModal === 'assignments'}
        onClose={() => setActiveModal(null)}
        title="Admin Assignments Console"
      >
        <form onSubmit={handleCreateAssignment} style={{ marginBottom: '1.5rem', background: 'var(--bg-card-subtle)', padding: '1rem', borderRadius: 'var(--radius-md)' }}>
          <h4 style={{ fontSize: '0.95rem', marginBottom: '0.75rem' }}>Create New Assignment:</h4>
          <div className="form-group">
            <label className="form-label">Title</label>
            <input
              type="text"
              className="form-input"
              placeholder="e.g. Lab Experiment 4: Multi-threading"
              value={assignmentForm.title}
              onChange={(e) => setAssignmentForm({ ...assignmentForm, title: e.target.value })}
              required
            />
          </div>

          <div style={{ display: 'grid', gridTemplateColumns: '1.5fr 1fr', gap: '0.75rem' }}>
            <div className="form-group">
              <label className="form-label">Subject</label>
              <select
                className="form-select"
                value={assignmentForm.subjectId}
                onChange={(e) => setAssignmentForm({ ...assignmentForm, subjectId: e.target.value })}
                required
              >
                {subjects.map((s) => (
                  <option key={s.id} value={s.id}>
                    {s.code} - {s.name}
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
          </div>

          <div className="form-group">
            <label className="form-label">Description / Instructions</label>
            <textarea
              className="form-textarea"
              rows="2"
              placeholder="Provide assignment guidelines or submission requirements..."
              value={assignmentForm.description}
              onChange={(e) => setAssignmentForm({ ...assignmentForm, description: e.target.value })}
            />
          </div>

          <button type="submit" className="btn btn-primary btn-sm" style={{ width: '100%' }}>
            + Publish Assignment to MySQL
          </button>
        </form>

        <h4 style={{ fontSize: '0.95rem', marginBottom: '0.5rem' }}>Existing Assignments ({allAssignments.length}):</h4>
        <div style={{ maxHeight: '300px', overflowY: 'auto' }}>
          {allAssignments.map((a) => (
            <div key={a.id} style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', padding: '0.65rem 0', borderBottom: '1px solid var(--border)' }}>
              <div>
                <div style={{ fontWeight: 600, fontSize: '0.9rem' }}>{a.title}</div>
                <div style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>
                  {a.subjectCode} &bull; Due: {a.dueDate}
                </div>
              </div>
              <button
                type="button"
                onClick={() => handleDeleteAssignment(a.id)}
                className="btn btn-outline-danger btn-sm"
              >
                Delete
              </button>
            </div>
          ))}
        </div>
      </Modal>
    </div>
  );
}
