import React, { createContext, useContext, useState, useEffect } from 'react';
import { authApi } from '@/lib/api';
import { toast } from 'sonner';

interface User {
  email: string;
  firstName: string;
  lastName: string;
  role: 'STUDENT' | 'INSTRUCTOR' | 'ADMIN';
}

interface AuthContextType {
  user: User | null;
  token: string | null;
  login: (email: string, password: string) => Promise<void>;
  signup: (data: {
    userEmail: string;
    firstName: string;
    lastName: string;
    role: string;
    password: string;
  }) => Promise<void>;
  logout: () => void;
  isAuthenticated: boolean;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const AuthProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [user, setUser] = useState<User | null>(null);
  const [token, setToken] = useState<string | null>(null);

  useEffect(() => {
    const storedToken = localStorage.getItem('token');
    const storedUser = localStorage.getItem('user');

    // معالجة البيانات الفاسدة
    if (!storedToken || !storedUser || storedUser === 'undefined') {
      localStorage.clear();
      return;
    }

    try {
      const parsedUser = JSON.parse(storedUser);
      setToken(storedToken);
      setUser(parsedUser);
    } catch (error) {
      console.error('Corrupted user data in localStorage:', error);
      localStorage.clear();
    }
  }, []);

  const login = async (email: string, password: string) => {
  try {
    const data = await authApi.login({
      logInEmail: email,
      logInPassword: password,
    });

    // اطبع الرد للتأكيد
    console.log("✅ Login response:", data);

    if (!data?.token) {
      throw new Error("Invalid response structure: token missing");
    }

    const token = data.token;

    const userData = {
      email: data.logInEmail,
      firstName: data.fallName?.split(" ")[0] || "",
      lastName: data.fallName?.split(" ")[1] || "",
      role: data.role,
    };

    localStorage.setItem("token", token);
    localStorage.setItem("user", JSON.stringify(userData));

    setToken(token);
    setUser(userData);

    toast.success("Welcome back!");
  } catch (error: any) {
    console.error("❌ Login error:", error);
    toast.error(error.response?.data?.message || "Login failed");
    throw error;
  }
};


  const signup = async (data: {
    userEmail: string;
    firstName: string;
    lastName: string;
    role: string;
    password: string;
  }) => {
    try {
      await authApi.signup(data);
      toast.success('Account created! Please login.');
    } catch (error: any) {
      toast.error(error.response?.data?.message || 'Signup failed');
      throw error;
    }
  };

  const logout = () => {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    setToken(null);
    setUser(null);
    toast.success('Logged out successfully');
  };

  return (
    <AuthContext.Provider
      value={{
        user,
        token,
        login,
        signup,
        logout,
        isAuthenticated: !!token,
      }}
    >
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within AuthProvider');
  }
  return context;
};
