import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { api } from '../api/client';
import { useAuth } from '../context/AuthContext';

export default function StudentDashboard() {
  const { user } = useAuth();
  const [summary, setSummary] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    fetchDashboardData();
  }, []);

  const fetchDashboardData = async () => {
    setLoading(true);
    setError('');
    try {
      const res = await api.get('/student/dashboard');
      if (res && res.success) {
        setSummary(res.data);
      } else {
        setError(res?.message || 'Failed to load dashboard metrics');
      }
    } catch (err) {
      setError(err.message || 'Could not connect to backend server');
    } finally {
      setLoading(false);
    }
  };

  if (loading) {
    return (
      <div style={{ textAlign: 'center', padding: '3rem 0' }}>
        <p style={{ color: 'var(--text-muted)', fontSize: '1.1rem' }}>
          Loading your academic records from database...
        </p>
      </div>
    );
  }

  if (error) {
    return (
      <div className="card" style={{ maxWidth: 600, margin: '2rem auto', textAlign: 'center' }}>
        <div className="alert alert-danger" style={{ marginBottom: '1.5rem' }}>
          {error}
        </div>
        <button onClick={fetchDashboardData} className="btn btn-primary">
          Retry Loading Data
        </button>
      </div>
    );
  }

  const avgMarks = summary?.averageMarks ?? 0;
  const attendance = summary?.attendancePercentage ?? 0;
  const pending = summary?.pendingAssignments ?? 0;
  const completed = summary?.completedAssignments ?? 0;

  return (
    <div>
      {/* Header Banner */}
      <div className="card" style={{ marginBottom: '1.75rem', background: 'linear-gradient(135deg, #ffffff 0%, #f8fafc 100%)' }}>
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', flexWrap: 'wrap', gap: '1rem' }}>
          <div>
            <div style={{ display: 'flex', alignItems: 'center', gap: '0.65rem', marginBottom: '0.25rem' }}>
              <h1 style={{ fontSize: '1.75rem' }}>Welcome back, {summary?.name || user?.name}!</h1>
              <span className="badge badge-info">{summary?.studentId || user?.studentId}</span>
            </div>
            <p>
              Branch: <strong>{summary?.branch || user?.branch}</strong> &bull; Semester: <strong>{summary?.semester || user?.semester}</strong>
            </p>
          </div>

          <div style={{ display: 'flex', gap: '0.65rem' }}>
            <button onClick={fetchDashboardData} className="btn btn-secondary btn-sm" title="Reload live data from MySQL">
              ↻ Refresh Data
            </button>
            <Link to="/profile" className="btn btn-primary btn-sm">
              My Profile
            </Link>
          </div>
        </div>
      </div>

      {/* 4 Stat Cards */}
      <div className="stats-grid">
        {/* Average Marks */}
        <div className="stat-card">
          <div className="stat-header">
            <span>AVERAGE MARKS</span>
            <span>📊</span>
          </div>
          <div className="stat-value">{avgMarks}%</div>
          <div className="progress-bar-bg">
            <div
              className={`progress-bar-fill ${avgMarks >= 75 ? 'success' : avgMarks >= 50 ? 'primary' : 'danger'}`}
              style={{ width: `${Math.min(avgMarks, 100)}%` }}
            />
          </div>
          <div className="stat-subtext" style={{ marginTop: '0.5rem' }}>
            {avgMarks >= 75 ? 'Excellent standing' : avgMarks >= 50 ? 'Average performance' : 'Needs improvement'}
          </div>
        </div>

        {/* Attendance Percentage */}
        <div className="stat-card">
          <div className="stat-header">
            <span>OVERALL ATTENDANCE</span>
            <span>📅</span>
          </div>
          <div className="stat-value">{attendance}%</div>
          <div className="progress-bar-bg">
            <div
              className={`progress-bar-fill ${attendance >= 75 ? 'success' : 'danger'}`}
              style={{ width: `${Math.min(attendance, 100)}%` }}
            />
          </div>
          <div className="stat-subtext" style={{ marginTop: '0.5rem', color: attendance < 75 ? 'var(--danger)' : 'var(--text-muted)' }}>
            {attendance >= 75 ? 'Eligible for examinations' : '⚠️ Below 75% college minimum criteria!'}
          </div>
        </div>

        {/* Pending Assignments */}
        <div className="stat-card">
          <div className="stat-header">
            <span>PENDING ASSIGNMENTS</span>
            <span>📝</span>
          </div>
          <div className="stat-value" style={{ color: pending > 0 ? 'var(--warning)' : 'var(--success)' }}>
            {pending}
          </div>
          <div className="stat-subtext">
            {pending === 0 ? 'All caught up!' : `${pending} assignment${pending > 1 ? 's' : ''} awaiting completion`}
          </div>
        </div>

        {/* Completed Assignments */}
        <div className="stat-card">
          <div className="stat-header">
            <span>COMPLETED ASSIGNMENTS</span>
            <span>✅</span>
          </div>
          <div className="stat-value" style={{ color: 'var(--success)' }}>
            {completed}
          </div>
          <div className="stat-subtext">Total submitted assignments</div>
        </div>
      </div>

      {/* Quick Navigation Cards */}
      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(320px, 1fr))', gap: '1.25rem' }}>
        <div className="card">
          <div className="card-header">
            <h2 className="card-title">Marks & Attendance Overview</h2>
            <Link to="/marks-attendance" className="btn btn-secondary btn-sm">
              View Detailed &rarr;
            </Link>
          </div>
          <p style={{ marginBottom: '1rem' }}>
            Inspect subject-wise theory & lab scores, attended classes, and total sessions recorded in MySQL.
          </p>
          <div style={{ display: 'flex', gap: '0.75rem' }}>
            <Link to="/marks-attendance" className="btn btn-primary btn-sm" style={{ flex: 1 }}>
              View Marks
            </Link>
            <Link to="/marks-attendance" className="btn btn-secondary btn-sm" style={{ flex: 1 }}>
              View Attendance
            </Link>
          </div>
        </div>

        <div className="card">
          <div className="card-header">
            <h2 className="card-title">Assignments Management</h2>
            <Link to="/assignments" className="btn btn-secondary btn-sm">
              Manage &rarr;
            </Link>
          </div>
          <p style={{ marginBottom: '1rem' }}>
            Track assigned submissions, due dates, overdue statuses, and mark tasks complete.
          </p>
          <Link to="/assignments" className="btn btn-primary btn-sm" style={{ display: 'block', textAlign: 'center' }}>
            Open Assignments
          </Link>
        </div>
      </div>
    </div>
  );
}
