import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

export default function Register() {
  const [formData, setFormData] = useState({
    studentId: '',
    name: '',
    email: '',
    branch: 'Computer Science',
    semester: 4,
    password: '',
  });

  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const [loading, setLoading] = useState(false);

  const { register } = useAuth();
  const navigate = useNavigate();

  const branches = [
    'Computer Science',
    'Information Technology',
    'Electronics & Communication',
    'Mechanical Engineering',
    'Civil Engineering',
    'Electrical Engineering',
  ];

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData((prev) => ({
      ...prev,
      [name]: name === 'semester' ? parseInt(value, 10) || 1 : value,
    }));
  };

  const validate = () => {
    if (!formData.studentId.trim()) return 'Student ID is required.';
    if (!formData.name.trim()) return 'Full name is required.';
    if (!formData.email.trim()) return 'Email is required.';

    // Email format validation
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!emailRegex.test(formData.email.trim())) {
      return 'Please enter a valid email address.';
    }

    if (!formData.branch.trim()) return 'Branch is required.';
    if (formData.semester < 1 || formData.semester > 8) {
      return 'Semester must be between 1 and 8.';
    }
    if (!formData.password || formData.password.length < 6) {
      return 'Password must be at least 6 characters long.';
    }

    return null;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setSuccess('');

    const validationError = validate();
    if (validationError) {
      setError(validationError);
      return;
    }

    setLoading(true);
    try {
      const res = await register({
        studentId: formData.studentId.trim(),
        name: formData.name.trim(),
        email: formData.email.trim().toLowerCase(),
        branch: formData.branch,
        semester: Number(formData.semester),
        password: formData.password,
      });

      if (res && res.success) {
        setSuccess('Registration successful! Redirecting to login...');
        setTimeout(() => {
          navigate('/login');
        }, 1500);
      } else {
        setError(res?.message || 'Registration failed.');
      }
    } catch (err) {
      setError(err.message || 'Registration failed. Please check inputs.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="auth-wrapper">
      <div className="auth-card" style={{ maxWidth: 480 }}>
        <div className="auth-header">
          <div style={{ display: 'inline-flex', alignItems: 'center', gap: '0.5rem', marginBottom: '0.5rem' }}>
            <div className="brand-icon">E</div>
            <span style={{ fontSize: '1.4rem', fontWeight: 800, color: 'var(--primary)' }}>EduTrack</span>
          </div>
          <h1>Create Student Account</h1>
          <p>Register to manage marks, attendance & assignments</p>
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

        <form onSubmit={handleSubmit}>
          <div className="form-group">
            <label className="form-label" htmlFor="studentId">
              Student ID (Roll Number)
            </label>
            <input
              id="studentId"
              name="studentId"
              type="text"
              className="form-input"
              placeholder="e.g. STU105"
              value={formData.studentId}
              onChange={handleChange}
              disabled={loading}
              required
            />
          </div>

          <div className="form-group">
            <label className="form-label" htmlFor="name">
              Full Name
            </label>
            <input
              id="name"
              name="name"
              type="text"
              className="form-input"
              placeholder="e.g. Alex Johnson"
              value={formData.name}
              onChange={handleChange}
              disabled={loading}
              required
            />
          </div>

          <div className="form-group">
            <label className="form-label" htmlFor="email">
              Email Address
            </label>
            <input
              id="email"
              name="email"
              type="email"
              className="form-input"
              placeholder="e.g. alex.j@edutrack.edu"
              value={formData.email}
              onChange={handleChange}
              disabled={loading}
              required
            />
          </div>

          <div style={{ display: 'grid', gridTemplateColumns: '1.5fr 1fr', gap: '0.75rem' }}>
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
                disabled={loading}
              >
                {branches.map((b) => (
                  <option key={b} value={b}>{b}</option>
                ))}
              </select>
            </div>

            <div className="form-group">
              <label className="form-label" htmlFor="semester">
                Semester (1-8)
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
                disabled={loading}
                required
              />
            </div>
          </div>

          <div className="form-group">
            <label className="form-label" htmlFor="password">
              Password (min. 6 characters)
            </label>
            <input
              id="password"
              name="password"
              type="password"
              className="form-input"
              placeholder="Create a strong password"
              value={formData.password}
              onChange={handleChange}
              disabled={loading}
              required
            />
          </div>

          <div style={{ display: 'flex', gap: '0.75rem', marginTop: '1.25rem' }}>
            <Link to="/login" className="btn btn-secondary" style={{ flex: 1 }}>
              Return to Login
            </Link>
            <button
              type="submit"
              className="btn btn-primary"
              style={{ flex: 1.5 }}
              disabled={loading}
            >
              {loading ? 'Registering...' : 'Register'}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}
