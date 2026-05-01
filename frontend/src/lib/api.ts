import axios from 'axios';

const API_BASE_URL = 'http://localhost:8080/api/v1';

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// 🔐 Add token automatically to every request
api.interceptors.request.use((config) => {
  const token = localStorage.getItem('token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

// Helper function to handle unexpected responses
const handleResponse = (response: any) => {
  if (response.status !== 200 && response.status !== 202) {
    throw new Error(`Unexpected response from server: ${response.status}`);
  }
  return response.data;
};

// ============================
// 🔑 Auth API
// ============================
export const authApi = {
  login: async (credentials: { logInEmail: string; logInPassword: string }) => {
    const response = await api.post('/auth/login', credentials);
    return handleResponse(response);
  },

  signup: async (data: {
    userEmail: string;
    firstName: string;
    lastName: string;
    role: string;
    password: string;
  }) => {
    const response = await api.post('/auth/sign', data);
    return handleResponse(response);
  },
};

// ============================
// 🎓 Student API
// ============================
export const studentApi = {
  getAllCourses: async () => {
    const response = await api.get('/student/allCourses');
    return handleResponse(response);
  },
};

// ============================
// 🧾 Enrollment API
// ============================
export const enrollmentApi = {
  enroll: async (data: { courseName: string; studentName: string }) => {
    const response = await api.post('/enrollment', data);
    return handleResponse(response);
  },
};

// ============================
// 🧑‍🏫 Instructor API
// ============================
export const instructorApi = {
  createCourse: async (course: any) => {
    const response = await api.post('/instructor/createCourse', course);
    return handleResponse(response);
  },

  updateCourse: async (id: string, course: any) => {
    const response = await api.put(`/instructor/updateCourse/${id}`, course);
    return handleResponse(response);
  },

  deleteCourse: async (id: string) => {
    const response = await api.delete(`/instructor/deleteCourse/${id}`);
    return handleResponse(response);
  },
};

// ============================
// 🧑‍💼 Admin API
// ============================
export const adminApi = {
  getUsers: async () => {
    const response = await api.get('/admin/Users');
    return handleResponse(response);
  },

  getAllUsers: async () => {
    const response = await api.get('/admin/allUsers');
    return handleResponse(response);
  },

  deleteUser: async (id: string) => {
    const response = await api.delete(`/admin/${id}`);
    return handleResponse(response);
  },
};

export default api;
