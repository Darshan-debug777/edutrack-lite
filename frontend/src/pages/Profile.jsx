import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { api } from '../api/client';
import { useAuth } from '../context/AuthContext';

export default function Profile() {
  const { user, updateUserLocally } = useAuth();

  const [profile, setProfile] = useState(null);
  const [formData, setFormData] = useState({
    name: '',
    email: '',
    branch: '',
    semester: 1,
  });

  const [isEditing, setIsEditing] = useState(false);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');

  const branches = [
    'Computer Science',
    'Information Technology',
    'Electronics & Communication',
    'Mechanical Engineering',
    'Civil Engineering',
    'Electrical Engineering',
  ];

  useEffect(() => {
    fetchProfile();
  }, []);

  const fetchProfile = async () => {
    setLoading(true);
    setError('');
    try {
      const res = await api.get('/student/profile');
      if (res && res.success) {
        setProfile(res.data);
        resetForm(res.data);
      } else {
        setError(res?.message || 'Failed to fetch student profile');
      }
    } catch (err) {
      setError(err.message || 'Error connecting to database');
    } finally {
      setLoading(false);
    }
  };

  const resetForm = (data) => {
    setFormData({
      name: data.name || '',
      email: data.email || '',
      branch: data.branch || '',
      semester: data.semester || 1,
    });
  };

  const handleEdit = () => {
    setIsEditing(true);
    setError('');
    setSuccess('');
  };

  const handleCancel = () => {
    setIsEditing(false);
    setError('');
    setSuccess('');
    if (profile) {
      resetForm(profile);
    }
  };

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData((prev) => ({
      ...prev,
      [name]: name === 'semester' ? parseInt(value, 10) || 1 : value,
    }));
  };

  const handleSave = async (e) => {
    e.preventDefault();
    setError('');
    setSuccess('');

    // Client-side validations
    if (!formData.name.trim()) {
      setError('Full Name cannot be empty.');
      return;
    }
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!emailRegex.test(formData.email.trim())) {
      setError('Please provide a valid email format.');
      return;
    }
    if (formData.semester < 1 || formData.semester > 8) {
      setError('Semester must be between 1 and 8.');
      return;
    }

    setSaving(true);
    try {
      const res = await api.put('/student/profile', {
        name: formData.name.trim(),
        email: formData.email.trim().toLowerCase(),
        branch: formData.branch.trim(),
        semester: Number(formData.semester),
      });

      if (res && res.success) {
        setProfile(res.data);
        updateUserLocally(res.data);
        setIsEditing(false);
        setSuccess('Profile successfully updated and saved to MySQL!');
      } else {
        setError(res?.message || 'Failed to save profile changes.');
      }
    } catch (err) {
      setError(err.message || 'Error updating profile.');
    } finally {
      setSaving(false);
    }
  };

  if (loading) {
    return (
      <div style={{ textAlign: 'center', padding: '3rem 0' }}>
        <p style={{ color: 'var(--text-muted)' }}>Retrieving profile from database...</p>
      </div>
    );
  }

  return (
    <div style={{ maxWidth: 680, margin: '0 auto' }}>
      <div className="card">
        <div className="card-header">
          <div>
            <h1 className="card-title" style={{ fontSize: '1.4rem' }}>Student Profile</h1>
            <p style={{ fontSize: '0.85rem' }}>View and update your personal academic details</p>
          </div>
          <Link to="/dashboard" className="btn btn-secondary btn-sm">
            &larr; Back to Dashboard
          </Link>
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

        <form onSubmit={handleSave}>
          <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '1rem', marginBottom: '1rem' }}>
            {/* Student ID (Read Only) */}
            <div className="form-group">
              <label className="form-label">Student ID (Immutable)</label>
              <input
                type="text"
                className="form-input"
                value={profile?.studentId || ''}
                disabled
                title="Student ID is permanent and cannot be modified."
              />
              <span style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>Primary identifier in database</span>
            </div>

            {/* Role (Read Only) */}
            <div className="form-group">
              <label className="form-label">Role</label>
              <input
                type="text"
                className="form-input"
                value={profile?.role || ''}
                disabled
              />
            </div>
          </div>

          {/* Full Name */}
          <div className="form-group">
            <label className="form-label" htmlFor="name">
              Full Name
            </label>
            <input
              id="name"
              name="name"
              type="text"
              className="form-input"
              value={formData.name}
              onChange={handleChange}
              disabled={!isEditing || saving}
              required
            />
          </div>

          {/* Email Address */}
          <div className="form-group">
            <label className="form-label" htmlFor="email">
              Email Address
            </label>
            <input
              id="email"
              name="email"
              type="email"
              className="form-input"
              value={formData.email}
              onChange={handleChange}
              disabled={!isEditing || saving}
              required
            />
          </div>

          <div style={{ display: 'grid', gridTemplateColumns: '1.5fr 1fr', gap: '1rem' }}>
            {/* Branch */}
            <div className="form-group">
              <label className="form-label" htmlFor="branch">
                Branch / Major
              </label>
              <select
                id="branch"
                name="branch"
                className="form-select"
                value={formData.branch}
                onChange={handleChange}
                disabled={!isEditing || saving}
              >
                {branches.map((b) => (
                  <option key={b} value={b}>{b}</option>
                ))}
              </select>
            </div>

            {/* Semester */}
            <div className="form-group">
              <label className="form-label" htmlFor="semester">
                Semester
              </label>
              <input
                id="semester"
                name="semester"
                type="number"
                min="1"
                max="8"
                className="form-input"
                value={formData.semester}
                onChange={handleChange}
                disabled={!isEditing || saving}
                required
              />
            </div>
          </div>

          {/* Action Buttons */}
          <div style={{ display: 'flex', justifyContent: 'flex-end', gap: '0.75rem', marginTop: '1.5rem', paddingTop: '1rem', borderTop: '1px solid var(--border)' }}>
            {!isEditing ? (
              <button
                type="button"
                onClick={handleEdit}
                className="btn btn-primary"
              >
                ✏️ Edit Profile
              </button>
            ) : (
              <>
                <button
                  type="button"
                  onClick={handleCancel}
                  className="btn btn-secondary"
                  disabled={saving}
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  className="btn btn-success"
                  disabled={saving}
                >
                  {saving ? 'Saving to Database...' : '💾 Save Changes'}
                </button>
              </>
            )}
          </div>
        </form>
      </div>
    </div>
  );
}
