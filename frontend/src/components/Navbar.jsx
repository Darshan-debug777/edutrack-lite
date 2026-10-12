import React from 'react';
import { NavLink, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

export default function Navbar() {
  const { user, logout } = useAuth();
  const navigate = useNavigate();

  const handleLogout = async () => {
    await logout();
    navigate('/login');
  };

  if (!user) return null;

  const isAdmin = user.role === 'ADMIN';

  return (
    <header className="navbar">
      <div className="navbar-inner">
        <NavLink to={isAdmin ? '/admin' : '/dashboard'} className="brand">
          <div className="brand-icon">E</div>
          <span>EduTrack</span>
          <span className="brand-sub">Lite</span>
        </NavLink>

        <nav>
          <ul className="nav-links">
            {!isAdmin ? (
              <>
                <li>
                  <NavLink to="/dashboard" className={({ isActive }) => `nav-link ${isActive ? 'active' : ''}`}>
                    Dashboard
                  </NavLink>
                </li>
                <li>
                  <NavLink to="/profile" className={({ isActive }) => `nav-link ${isActive ? 'active' : ''}`}>
                    My Profile
                  </NavLink>
                </li>
                <li>
                  <NavLink to="/marks-attendance" className={({ isActive }) => `nav-link ${isActive ? 'active' : ''}`}>
                    Marks & Attendance
                  </NavLink>
                </li>
                <li>
                  <NavLink to="/assignments" className={({ isActive }) => `nav-link ${isActive ? 'active' : ''}`}>
                    Assignments
                  </NavLink>
                </li>
              </>
            ) : (
              <li>
                <NavLink to="/admin" className={({ isActive }) => `nav-link ${isActive ? 'active' : ''}`}>
                  Admin Panel
                </NavLink>
              </li>
            )}
          </ul>
        </nav>

        <div className="nav-user">
          <div className="user-badge">
            <span>{user.name}</span>
            <span className={`role-pill ${user.role.toLowerCase()}`}>{user.role}</span>
          </div>
          <button onClick={handleLogout} className="btn btn-secondary btn-sm" title="Log out of EduTrack">
            Logout
          </button>
        </div>
      </div>
    </header>
  );
}
