import React, { useState, useEffect } from 'react';
import { api } from '../api/client';
import { useAuth } from '../context/AuthContext';

export default function MarksAttendance() {
  const { user } = useAuth();
  const [marks, setMarks] = useState([]);
  const [attendance, setAttendance] = useState([]);
  const [activeTab, setActiveTab] = useState('both'); // 'both', 'marks', 'attendance'
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    fetchAcademicData();
  }, []);

  const fetchAcademicData = async () => {
    setLoading(true);
    setError('');
    try {
      const [marksRes, attRes] = await Promise.all([
        api.get('/student/marks'),
        api.get('/student/attendance'),
      ]);

      if (marksRes && marksRes.success) {
        setMarks(marksRes.data || []);
      }
      if (attRes && attRes.success) {
        setAttendance(attRes.data || []);
      }
    } catch (err) {
      setError(err.message || 'Failed to retrieve academic records');
    } finally {
      setLoading(false);
    }
  };

  // Merge marks and attendance by subjectId
  const combinedData = marks.map((m) => {
    const att = attendance.find((a) => a.subjectId === m.subjectId) || {
      classesAttended: 0,
      totalClasses: 0,
      attendancePercentage: 0.0,
    };
    return {
      subjectId: m.subjectId,
      subjectCode: m.subjectCode,
      subjectName: m.subjectName,
      marksObtained: m.marksObtained,
      maxMarks: m.maxMarks,
      marksPercentage: m.percentage,
      classesAttended: att.classesAttended,
      totalClasses: att.totalClasses,
      attendancePercentage: att.attendancePercentage,
    };
  });

  // Aggregate metrics
  const totalMarksObtained = combinedData.reduce((acc, curr) => acc + curr.marksObtained, 0);
  const totalMaxMarks = combinedData.reduce((acc, curr) => acc + curr.maxMarks, 0);
  const overallAverageMarks = totalMaxMarks > 0 ? ((totalMarksObtained / totalMaxMarks) * 100).toFixed(1) : 0;

  const totalClassesAttended = combinedData.reduce((acc, curr) => acc + curr.classesAttended, 0);
  const grandTotalClasses = combinedData.reduce((acc, curr) => acc + curr.totalClasses, 0);
  const overallAttendancePct = grandTotalClasses > 0 ? ((totalClassesAttended / grandTotalClasses) * 100).toFixed(1) : 0;

  return (
    <div>
      <div className="card" style={{ marginBottom: '1.5rem' }}>
        <div className="card-header" style={{ borderBottom: 'none', paddingBottom: 0, marginBottom: 0 }}>
          <div>
            <h1 className="card-title" style={{ fontSize: '1.4rem' }}>Subject-wise Marks & Attendance</h1>
            <p style={{ fontSize: '0.85rem' }}>Verified academic performance retrieved directly from MySQL</p>
          </div>
          <div style={{ display: 'flex', gap: '0.5rem', flexWrap: 'wrap' }}>
            <div style={{ display: 'flex', background: 'var(--bg-card-subtle)', borderRadius: 'var(--radius-md)', padding: 3 }}>
              <button
                type="button"
                className={`btn btn-sm ${activeTab === 'both' ? 'btn-primary' : 'btn-secondary'}`}
                style={{ border: 'none' }}
                onClick={() => setActiveTab('both')}
              >
                All Combined
              </button>
              <button
                type="button"
                className={`btn btn-sm ${activeTab === 'marks' ? 'btn-primary' : 'btn-secondary'}`}
                style={{ border: 'none' }}
                onClick={() => setActiveTab('marks')}
              >
                View Marks
              </button>
              <button
                type="button"
                className={`btn btn-sm ${activeTab === 'attendance' ? 'btn-primary' : 'btn-secondary'}`}
                style={{ border: 'none' }}
                onClick={() => setActiveTab('attendance')}
              >
                View Attendance
              </button>
            </div>
            <button onClick={fetchAcademicData} className="btn btn-secondary btn-sm">
              ↻ Refresh
            </button>
          </div>
        </div>
      </div>

      {error && (
        <div className="alert alert-danger" role="alert">
          <span>{error}</span>
        </div>
      )}

      {/* Aggregate Overview Banner */}
      <div className="stats-grid" style={{ marginBottom: '1.5rem' }}>
        <div className="stat-card">
          <div className="stat-header">
            <span>OVERALL ACADEMIC SCORE</span>
            <span>🎯</span>
          </div>
          <div className="stat-value">{overallAverageMarks}%</div>
          <div className="stat-subtext">Total: {totalMarksObtained} / {totalMaxMarks} Marks</div>
        </div>

        <div className="stat-card">
          <div className="stat-header">
            <span>OVERALL ATTENDANCE</span>
            <span>📅</span>
          </div>
          <div className="stat-value" style={{ color: overallAttendancePct >= 75 ? 'var(--success)' : 'var(--danger)' }}>
            {overallAttendancePct}%
          </div>
          <div className="stat-subtext">
            Attended: {totalClassesAttended} / {grandTotalClasses} Classes Conducted
          </div>
        </div>
      </div>

      {/* Table Card */}
      <div className="card">
        {loading ? (
          <div style={{ textAlign: 'center', padding: '2rem 0' }}>
            <p style={{ color: 'var(--text-muted)' }}>Loading academic data...</p>
          </div>
        ) : combinedData.length === 0 ? (
          <div style={{ textAlign: 'center', padding: '2rem 0' }}>
            <p style={{ color: 'var(--text-muted)' }}>No academic records found for this student.</p>
          </div>
        ) : (
          <div className="table-responsive">
            <table className="data-table">
              <thead>
                <tr>
                  <th>Code</th>
                  <th>Subject Name</th>
                  {(activeTab === 'both' || activeTab === 'marks') && (
                    <>
                      <th>Marks Obtained</th>
                      <th>Max Marks</th>
                      <th>Score %</th>
                      <th>Grade Status</th>
                    </>
                  )}
                  {(activeTab === 'both' || activeTab === 'attendance') && (
                    <>
                      <th>Classes Attended</th>
                      <th>Total Classes</th>
                      <th>Attendance %</th>
                      <th>Eligibility</th>
                    </>
                  )}
                </tr>
              </thead>
              <tbody>
                {combinedData.map((row) => (
                  <tr key={row.subjectId}>
                    <td>
                      <span className="badge badge-info">{row.subjectCode}</span>
                    </td>
                    <td style={{ fontWeight: 600 }}>{row.subjectName}</td>

                    {(activeTab === 'both' || activeTab === 'marks') && (
                      <>
                        <td style={{ fontWeight: 600 }}>{row.marksObtained}</td>
                        <td style={{ color: 'var(--text-muted)' }}>{row.maxMarks}</td>
                        <td>
                          <strong>{row.marksPercentage}%</strong>
                        </td>
                        <td>
                          {row.marksPercentage >= 75 ? (
                            <span className="badge badge-success">Distinction</span>
                          ) : row.marksPercentage >= 50 ? (
                            <span className="badge badge-warning">Pass</span>
                          ) : (
                            <span className="badge badge-danger">Needs Help</span>
                          )}
                        </td>
                      </>
                    )}

                    {(activeTab === 'both' || activeTab === 'attendance') && (
                      <>
                        <td style={{ fontWeight: 600 }}>{row.classesAttended}</td>
                        <td style={{ color: 'var(--text-muted)' }}>{row.totalClasses}</td>
                        <td>
                          <strong style={{ color: row.attendancePercentage >= 75 ? 'var(--success)' : 'var(--danger)' }}>
                            {row.attendancePercentage}%
                          </strong>
                        </td>
                        <td>
                          {row.attendancePercentage >= 75 ? (
                            <span className="badge badge-success">Eligible</span>
                          ) : (
                            <span className="badge badge-danger">Shortage (&lt;75%)</span>
                          )}
                        </td>
                      </>
                    )}
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>
    </div>
  );
}
