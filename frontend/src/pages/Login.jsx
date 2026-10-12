import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

export default function Login() {
  const [identifier, setIdentifier] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  const { login } = useAuth();
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');

    if (!identifier.trim() || !password.trim()) {
      setError('Please enter your Student ID / Email and Password.');
      return;
    }

    setLoading(true);
    try {
      const user = await login(identifier.trim(), password.trim());
      if (user.role === 'ADMIN') {
        navigate('/admin');
      } else {
        navigate('/dashboard');
      }
    } catch (err) {
      setError(err.message || 'Login failed. Please check credentials.');
    } finally {
      setLoading(false);
    }
  };

  const fillDemoStudent = () => {
    setIdentifier('STU101');
    setPassword('StudentPassword123!');
    setError('');
  };

  const fillDemoAdmin = () => {
    setIdentifier('admin@edutrack.edu');
    setPassword('AdminPassword123!');
    setError('');
  };

  return (
    <div className="auth-wrapper">
      <div className="auth-card">
        <div className="auth-header">
          <div style={{ display: 'inline-flex', alignItems: 'center', gap: '0.5rem', marginBottom: '0.5rem' }}>
            <div className="brand-icon">E</div>
            <span style={{ fontSize: '1.4rem', fontWeight: 800, color: 'var(--primary)' }}>EduTrack</span>
          </div>
          <h1>Student Login</h1>
          <p>Access your real-time academic records and assignments</p>
        </div>

        {error && (
          <div className="alert alert-danger" role="alert">
            <span>{error}</span>
          </div>
        )}

        <form onSubmit={handleSubmit}>
          <div className="form-group">
            <label className="form-label" htmlFor="identifier">
              Student ID or Email Address
            </label>
            <input
              id="identifier"
              type="text"
              className="form-input"
              placeholder="e.g. STU101 or john.doe@edutrack.edu"
              value={identifier}
              onChange={(e) => setIdentifier(e.target.value)}
              disabled={loading}
              required
            />
          </div>

          <div className="form-group">
            <label className="form-label" htmlFor="password">
              Password
            </label>
            <input
              id="password"
              type="password"
              className="form-input"
              placeholder="Enter your password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              disabled={loading}
              required
            />
          </div>

          <button
            type="submit"
            className="btn btn-primary"
            style={{ width: '100%', marginTop: '0.5rem' }}
            disabled={loading}
          >
            {loading ? 'Authenticating...' : 'Login to EduTrack'}
          </button>
        </form>

        <div style={{ marginTop: '1.25rem', textAlign: 'center', fontSize: '0.875rem' }}>
          Don't have an account?{' '}
          <Link to="/register" style={{ color: 'var(--primary)', fontWeight: 600, textDecoration: 'none' }}>
            Register here
          </Link>
        </div>

        <div className="demo-credentials-box">
          <div style={{ fontWeight: 600, marginBottom: '0.25rem', color: 'var(--text-main)' }}>
            Demo Viva Test Accounts:
          </div>
          <div>Student: <code>STU101</code> / <code>StudentPassword123!</code></div>
          <div>Admin: <code>admin@edutrack.edu</code> / <code>AdminPassword123!</code></div>
          <div className="demo-btn-group">
            <button type="button" onClick={fillDemoStudent} className="btn btn-secondary btn-sm" style={{ flex: 1 }}>
              Fill Student
            </button>
            <button type="button" onClick={fillDemoAdmin} className="btn btn-secondary btn-sm" style={{ flex: 1 }}>
              Fill Admin
            </button>
          </div>
        </div>
      </div>
    </div>
  );
}
