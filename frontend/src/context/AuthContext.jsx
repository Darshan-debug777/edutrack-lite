import React, { createContext, useContext, useState, useEffect } from 'react';
import { api } from '../api/client';

const AuthContext = createContext(null);

export function AuthProvider({ children }) {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);

  // Check existing session on load
  useEffect(() => {
    checkSession();
  }, []);

  async function checkSession() {
    try {
      const res = await api.get('/auth/me');
      if (res && res.success && res.data) {
        setUser(res.data);
      } else {
        setUser(null);
      }
    } catch {
      setUser(null);
    } finally {
      setLoading(false);
    }
  }

  async function login(identifier, password) {
    const res = await api.post('/auth/login', { identifier, password });
    if (res && res.success && res.data) {
      setUser(res.data);
      return res.data;
    }
    throw new Error(res?.message || 'Login failed');
  }

  async function register(studentData) {
    const res = await api.post('/auth/register', studentData);
    return res;
  }

  async function logout() {
    try {
      await api.post('/auth/logout', {});
    } catch {
      // ignore
    } finally {
      setUser(null);
    }
  }

  function updateUserLocally(updatedFields) {
    setUser((prev) => (prev ? { ...prev, ...updatedFields } : prev));
  }

  const value = {
    user,
    loading,
    login,
    register,
    logout,
    checkSession,
    updateUserLocally,
  };

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth() {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
}
