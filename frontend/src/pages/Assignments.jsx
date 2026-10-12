import React, { useState, useEffect } from 'react';
import { api } from '../api/client';

export default function Assignments() {
  const [assignments, setAssignments] = useState([]);
  const [filter, setFilter] = useState('ALL'); // 'ALL', 'PENDING', 'COMPLETED', 'OVERDUE'
  const [loading, setLoading] = useState(true);
  const [updatingId, setUpdatingId] = useState(null);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');

  useEffect(() => {
    fetchAssignments();
  }, []);

  const fetchAssignments = async () => {
    setLoading(true);
    setError('');
    try {
      const res = await api.get('/student/assignments');
      if (res && res.success) {
        setAssignments(res.data || []);
      } else {
        setError(res?.message || 'Failed to fetch assignments.');
      }
    } catch (err) {
      setError(err.message || 'Error connecting to database.');
    } finally {
      setLoading(false);
    }
  };

  const handleStatusChange = async (assignmentId, newStatus) => {
    setUpdatingId(assignmentId);
    setError('');
    setSuccess('');
    try {
      const res = await api.patch(`/student/assignments/${assignmentId}/status`, {
        status: newStatus,
      });

      if (res && res.success) {
        // Optimistically update or refetch
        setAssignments((prev) =>
          prev.map((a) => (a.id === assignmentId ? { ...a, status: newStatus } : a))
        );
        setSuccess(`Assignment status updated to ${newStatus}!`);
        setTimeout(() => setSuccess(''), 3000);
      } else {
        setError(res?.message || 'Failed to update assignment status.');
      }
    } catch (err) {
      setError(err.message || 'Error updating status in database.');
    } finally {
      setUpdatingId(null);
    }
  };

  const filteredAssignments = assignments.filter((item) => {
    if (filter === 'ALL') return true;
    return item.status === filter;
  });

  const getStatusBadge = (status) => {
    if (status === 'COMPLETED') {
      return <span className="badge badge-success">✓ Completed</span>;
    }
    if (status === 'OVERDUE') {
      return <span className="badge badge-danger">⚠️ Overdue</span>;
    }
    return <span className="badge badge-warning">⏳ Pending</span>;
  };

  return (
    <div>
      <div className="card" style={{ marginBottom: '1.5rem' }}>
        <div className="card-header" style={{ borderBottom: 'none', paddingBottom: 0, marginBottom: 0 }}>
          <div>
            <h1 className="card-title" style={{ fontSize: '1.4rem' }}>My Assignments & Tasks</h1>
            <p style={{ fontSize: '0.85rem' }}>Track deadlines and update submission status in real time</p>
          </div>
          <div style={{ display: 'flex', gap: '0.5rem', flexWrap: 'wrap' }}>
            <div style={{ display: 'flex', background: 'var(--bg-card-subtle)', borderRadius: 'var(--radius-md)', padding: 3 }}>
              {['ALL', 'PENDING', 'OVERDUE', 'COMPLETED'].map((f) => (
                <button
                  key={f}
                  type="button"
                  className={`btn btn-sm ${filter === f ? 'btn-primary' : 'btn-secondary'}`}
                  style={{ border: 'none' }}
                  onClick={() => setFilter(f)}
                >
                  {f}
                </button>
              ))}
            </div>
            <button onClick={fetchAssignments} className="btn btn-secondary btn-sm">
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

      {success && (
        <div className="alert alert-success" role="alert">
          <span>{success}</span>
        </div>
      )}

      {loading ? (
        <div style={{ textAlign: 'center', padding: '3rem 0' }}>
          <p style={{ color: 'var(--text-muted)' }}>Loading assignments from database...</p>
        </div>
      ) : filteredAssignments.length === 0 ? (
        <div className="card" style={{ textAlign: 'center', padding: '3rem 1.5rem' }}>
          <p style={{ color: 'var(--text-muted)', fontSize: '1.05rem', marginBottom: '1rem' }}>
            No assignments match the selected filter ({filter}).
          </p>
          <button onClick={() => setFilter('ALL')} className="btn btn-secondary btn-sm">
            Show All Assignments
          </button>
        </div>
      ) : (
        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(340px, 1fr))', gap: '1.25rem' }}>
          {filteredAssignments.map((a) => (
            <div key={a.id} className="card" style={{ display: 'flex', flexDirection: 'column', justifyContent: 'space-between' }}>
              <div>
                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', marginBottom: '0.75rem', gap: '0.5rem' }}>
                  <span className="badge badge-info">{a.subjectCode}</span>
                  {getStatusBadge(a.status)}
                </div>

                <h3 style={{ fontSize: '1.1rem', marginBottom: '0.35rem' }}>{a.title}</h3>
                <div style={{ fontSize: '0.8rem', color: 'var(--text-muted)', marginBottom: '0.75rem' }}>
                  Subject: <strong>{a.subjectName}</strong>
                </div>

                <p style={{ fontSize: '0.875rem', marginBottom: '1rem', color: 'var(--text-main)' }}>
                  {a.description || 'No additional instructions provided.'}
                </p>
              </div>

              <div style={{ paddingTop: '0.75rem', borderTop: '1px solid var(--border)', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                <div style={{ fontSize: '0.8rem' }}>
                  Due Date:{' '}
                  <strong style={{ color: a.status === 'OVERDUE' ? 'var(--danger)' : 'var(--text-main)' }}>
                    {a.dueDate}
                  </strong>
                </div>

                <div style={{ display: 'flex', gap: '0.4rem' }}>
                  {a.status === 'COMPLETED' ? (
                    <button
                      onClick={() => handleStatusChange(a.id, 'PENDING')}
                      className="btn btn-secondary btn-sm"
                      disabled={updatingId === a.id}
                      title="Mark assignment back to pending"
                    >
                      {updatingId === a.id ? 'Saving...' : 'Mark Pending'}
                    </button>
                  ) : (
                    <button
                      onClick={() => handleStatusChange(a.id, 'COMPLETED')}
                      className="btn btn-success btn-sm"
                      disabled={updatingId === a.id}
                      title="Mark assignment as completed"
                    >
                      {updatingId === a.id ? 'Saving...' : '✓ Mark Complete'}
                    </button>
                  )}
                </div>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}
