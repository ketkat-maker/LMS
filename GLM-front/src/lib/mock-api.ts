'use client'

import type { Role, Status, AuthResponse, CourseResponse, EnrollmentResponse, UserResponse, PageResponse } from '@/lib/api'

// Demo user accounts for each role
const DEMO_ACCOUNTS = [
  { email: 'student@lms.com', password: 'Student123', role: 'STUDENT' as Role, firstName: 'Alex', lastName: 'Johnson' },
  { email: 'instructor@lms.com', password: 'Instructor123', role: 'INSTRUCTOR' as Role, firstName: 'Dr. Sarah', lastName: 'Williams' },
  { email: 'admin@lms.com', password: 'Admin123', role: 'ADMIN' as Role, firstName: 'James', lastName: 'Anderson' },
]

// Mock database
let MOCK_USERS = [
  { userId: 'u-001', userName: 'Alex Johnson', userEmail: 'student@lms.com', role: 'STUDENT' as Role, createdAt: '2025-01-15T10:30:00', password: 'Student123' },
  { userId: 'u-002', userName: 'Emily Davis', userEmail: 'emily@lms.com', role: 'STUDENT' as Role, createdAt: '2025-02-01T08:00:00', password: 'Student123' },
  { userId: 'u-003', userName: 'Marcus Brown', userEmail: 'marcus@lms.com', role: 'STUDENT' as Role, createdAt: '2025-02-10T14:20:00', password: 'Student123' },
  { userId: 'u-004', userName: 'Sophia Martinez', userEmail: 'sophia@lms.com', role: 'STUDENT' as Role, createdAt: '2025-03-05T09:15:00', password: 'Student123' },
  { userId: 'u-005', userName: 'Dr. Sarah Williams', userEmail: 'instructor@lms.com', role: 'INSTRUCTOR' as Role, createdAt: '2024-12-01T08:00:00', password: 'Instructor123' },
  { userId: 'u-006', userName: 'Prof. Michael Chen', userEmail: 'mchen@lms.com', role: 'INSTRUCTOR' as Role, createdAt: '2024-11-15T10:00:00', password: 'Instructor123' },
  { userId: 'u-007', userName: 'Dr. Lisa Park', userEmail: 'lpark@lms.com', role: 'INSTRUCTOR' as Role, createdAt: '2025-01-05T11:30:00', password: 'Instructor123' },
  { userId: 'u-008', userName: 'James Anderson', userEmail: 'admin@lms.com', role: 'ADMIN' as Role, createdAt: '2024-10-01T08:00:00', password: 'Admin123' },
]

let MOCK_COURSES = [
  { courseId: 'c-001', courseTitle: 'Introduction to Computer Science', instructorId: 'u-005', instructorName: 'Dr. Sarah Williams', courseDuration: '2025-01-01T36:00:00', createdAt: '2025-01-10T08:00:00', updatedAt: '2025-03-15T10:30:00' },
  { courseId: 'c-002', courseTitle: 'Advanced Data Structures', instructorId: 'u-005', instructorName: 'Dr. Sarah Williams', courseDuration: '2025-01-01T48:00:00', createdAt: '2025-01-20T09:00:00', updatedAt: '2025-03-20T14:00:00' },
  { courseId: 'c-003', courseTitle: 'Web Development Fundamentals', instructorId: 'u-006', instructorName: 'Prof. Michael Chen', courseDuration: '2025-01-01T30:00:00', createdAt: '2025-02-01T10:00:00', updatedAt: '2025-04-01T08:00:00' },
  { courseId: 'c-004', courseTitle: 'Machine Learning Basics', instructorId: 'u-006', instructorName: 'Prof. Michael Chen', courseDuration: '2025-01-01T42:00:00', createdAt: '2025-02-15T11:00:00', updatedAt: '2025-04-10T09:00:00' },
  { courseId: 'c-005', courseTitle: 'Database Management Systems', instructorId: 'u-007', instructorName: 'Dr. Lisa Park', courseDuration: '2025-01-01T28:00:00', createdAt: '2025-03-01T08:30:00', updatedAt: '2025-04-15T12:00:00' },
  { courseId: 'c-006', courseTitle: 'Software Engineering Principles', instructorId: 'u-007', instructorName: 'Dr. Lisa Park', courseDuration: '2025-01-01T34:00:00', createdAt: '2025-03-10T09:00:00', updatedAt: '2025-04-20T15:00:00' },
  { courseId: 'c-007', courseTitle: 'Artificial Intelligence', instructorId: 'u-005', instructorName: 'Dr. Sarah Williams', courseDuration: '2025-01-01T40:00:00', createdAt: '2025-03-15T10:00:00', updatedAt: '2025-04-25T11:00:00' },
  { courseId: 'c-008', courseTitle: 'Cybersecurity Fundamentals', instructorId: 'u-006', instructorName: 'Prof. Michael Chen', courseDuration: '2025-01-01T26:00:00', createdAt: '2025-04-01T08:00:00', updatedAt: '2025-04-28T10:00:00' },
  { courseId: 'c-009', courseTitle: 'Cloud Computing', instructorId: 'u-007', instructorName: 'Dr. Lisa Park', courseDuration: '2025-01-01T32:00:00', createdAt: '2025-04-05T09:30:00', updatedAt: '2025-04-29T14:00:00' },
  { courseId: 'c-010', courseTitle: 'Mobile App Development', instructorId: 'u-005', instructorName: 'Dr. Sarah Williams', courseDuration: '2025-01-01T38:00:00', createdAt: '2025-04-10T11:00:00', updatedAt: '2025-04-30T08:00:00' },
  { courseId: 'c-011', courseTitle: 'Operating Systems', instructorId: 'u-006', instructorName: 'Prof. Michael Chen', courseDuration: '2025-01-01T35:00:00', createdAt: '2025-04-12T07:00:00', updatedAt: '2025-04-30T09:00:00' },
  { courseId: 'c-012', courseTitle: 'Computer Networks', instructorId: 'u-007', instructorName: 'Dr. Lisa Park', courseDuration: '2025-01-01T30:00:00', createdAt: '2025-04-14T10:30:00', updatedAt: '2025-04-30T11:00:00' },
]

let MOCK_ENROLLMENTS = [
  { enrollmentId: 'e-001', userId: 'u-001', courseId: 'c-001', courseName: 'Introduction to Computer Science', studentName: 'Alex Johnson', status: 'ACTIVE' as Status, progress: 65, enrollAt: '2025-01-15T10:30:00' },
  { enrollmentId: 'e-002', userId: 'u-001', courseId: 'c-003', courseName: 'Web Development Fundamentals', studentName: 'Alex Johnson', status: 'ACTIVE' as Status, progress: 42, enrollAt: '2025-02-05T08:00:00' },
  { enrollmentId: 'e-003', userId: 'u-001', courseId: 'c-005', courseName: 'Database Management Systems', studentName: 'Alex Johnson', status: 'ACTIVE' as Status, progress: 28, enrollAt: '2025-03-10T09:00:00' },
  { enrollmentId: 'e-004', userId: 'u-001', courseId: 'c-002', courseName: 'Advanced Data Structures', studentName: 'Alex Johnson', status: 'COMPLETED' as Status, progress: 100, enrollAt: '2024-09-01T08:00:00' },
  { enrollmentId: 'e-005', userId: 'u-001', courseId: 'c-008', courseName: 'Cybersecurity Fundamentals', studentName: 'Alex Johnson', status: 'DROPPED' as Status, progress: 15, enrollAt: '2025-04-05T10:00:00' },
  { enrollmentId: 'e-006', userId: 'u-002', courseId: 'c-001', courseName: 'Introduction to Computer Science', studentName: 'Emily Davis', status: 'ACTIVE' as Status, progress: 78, enrollAt: '2025-01-18T11:00:00' },
  { enrollmentId: 'e-007', userId: 'u-002', courseId: 'c-004', courseName: 'Machine Learning Basics', studentName: 'Emily Davis', status: 'ACTIVE' as Status, progress: 55, enrollAt: '2025-02-20T09:30:00' },
  { enrollmentId: 'e-008', userId: 'u-003', courseId: 'c-003', courseName: 'Web Development Fundamentals', studentName: 'Marcus Brown', status: 'ACTIVE' as Status, progress: 88, enrollAt: '2025-02-15T14:00:00' },
  { enrollmentId: 'e-009', userId: 'u-003', courseId: 'c-007', courseName: 'Artificial Intelligence', studentName: 'Marcus Brown', status: 'ACTIVE' as Status, progress: 30, enrollAt: '2025-03-20T10:00:00' },
  { enrollmentId: 'e-010', userId: 'u-004', courseId: 'c-005', courseName: 'Database Management Systems', studentName: 'Sophia Martinez', status: 'COMPLETED' as Status, progress: 100, enrollAt: '2025-03-10T08:00:00' },
  { enrollmentId: 'e-011', userId: 'u-004', courseId: 'c-009', courseName: 'Cloud Computing', studentName: 'Sophia Martinez', status: 'ACTIVE' as Status, progress: 50, enrollAt: '2025-04-10T09:00:00' },
  { enrollmentId: 'e-012', userId: 'u-002', courseId: 'c-006', courseName: 'Software Engineering Principles', studentName: 'Emily Davis', status: 'ACTIVE' as Status, progress: 35, enrollAt: '2025-03-15T08:30:00' },
]

let mockTokenCounter = 100

function generateMockToken(): string {
  return `mock-jwt-token-${++mockTokenCounter}-${Date.now()}`
}

function generateMockId(): string {
  return `${Date.now()}-${Math.random().toString(36).substring(2, 9)}`
}

// Simulate network delay
function delay(ms: number = 300): Promise<void> {
  return new Promise((resolve) => setTimeout(resolve, ms + Math.random() * 200))
}

// ========== Mock API Functions ==========

export const mockAuthApi = {
  async login(email: string, password: string): Promise<AuthResponse> {
    await delay()
    const account = DEMO_ACCOUNTS.find((a) => a.email === email)
    const user = MOCK_USERS.find((u) => u.userEmail === email)

    if (!account || !user || user.password !== password) {
      throw { message: 'Invalid email or password', status: 401 }
    }

    return {
      logInId: user.userId,
      fullName: user.userName,
      logInEmail: user.userEmail,
      role: user.role,
      token: generateMockToken(),
      message: 'Login successful',
    }
  },

  async signUp(data: { userEmail: string; firstName: string; lastName: string; role: Role; password: string }): Promise<AuthResponse> {
    await delay()
    if (MOCK_USERS.some((u) => u.userEmail === data.userEmail)) {
      throw { message: 'Email already exists', status: 409 }
    }

    const userId = generateMockId()
    const fullName = `${data.firstName} ${data.lastName}`
    MOCK_USERS.push({
      userId,
      userName: fullName,
      userEmail: data.userEmail,
      role: data.role,
      createdAt: new Date().toISOString(),
      password: data.password,
    })

    return {
      logInId: userId,
      fullName,
      logInEmail: data.userEmail,
      role: data.role,
      token: generateMockToken(),
      message: 'Account created successfully',
    }
  },

  async resetPassword(email: string, newPassword: string): Promise<AuthResponse> {
    await delay()
    const user = MOCK_USERS.find((u) => u.userEmail === email)
    if (!user) {
      throw { message: 'User not found', status: 404 }
    }
    user.password = newPassword

    return {
      logInId: user.userId,
      fullName: user.userName,
      logInEmail: user.userEmail,
      role: user.role,
      token: generateMockToken(),
      message: 'Password reset successful',
    }
  },
}

export const mockStudentApi = {
  async getCourses(params: { courseTitle?: string; page?: number; size?: number; sort?: string }): Promise<PageResponse<CourseResponse>> {
    await delay()
    let filtered = [...MOCK_COURSES]

    if (params.courseTitle) {
      const search = params.courseTitle.toLowerCase()
      filtered = filtered.filter((c) => c.courseTitle.toLowerCase().includes(search))
    }

    // Default sort by createdAt desc
    filtered.sort((a, b) => new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime())

    const page = params.page ?? 0
    const size = params.size ?? 10
    const start = page * size
    const content = filtered.slice(start, start + size)

    return {
      content,
      totalElements: filtered.length,
      totalPages: Math.ceil(filtered.length / size),
      number: page,
      size,
      first: page === 0,
      last: start + size >= filtered.length,
      empty: content.length === 0,
    }
  },
}

export const mockInstructorApi = {
  async createCourse(data: { courseName: string; instructorName: string; courseDuration: string }, userId: string): Promise<CourseResponse> {
    await delay()
    const courseId = generateMockId()
    const now = new Date().toISOString()
    const newCourse = {
      courseId,
      courseTitle: data.courseName,
      instructorId: userId,
      instructorName: data.instructorName,
      courseDuration: data.courseDuration || '2025-01-01T30:00:00',
      createdAt: now,
      updatedAt: now,
    }
    MOCK_COURSES.push(newCourse)
    return newCourse
  },

  async updateCourse(courseId: string, data: { courseName: string; instructorName: string; courseDuration: string }): Promise<CourseResponse> {
    await delay()
    const course = MOCK_COURSES.find((c) => c.courseId === courseId)
    if (!course) throw { message: 'Course not found', status: 404 }

    course.courseTitle = data.courseName
    course.instructorName = data.instructorName
    course.courseDuration = data.courseDuration
    course.updatedAt = new Date().toISOString()
    return course
  },

  async deleteCourse(courseId: string): Promise<CourseResponse> {
    await delay()
    const index = MOCK_COURSES.findIndex((c) => c.courseId === courseId)
    if (index === -1) throw { message: 'Course not found', status: 404 }

    const deleted = MOCK_COURSES.splice(index, 1)[0]
    // Also remove related enrollments
    MOCK_ENROLLMENTS = MOCK_ENROLLMENTS.filter((e) => e.courseId !== courseId)
    return deleted
  },
}

export const mockEnrollmentApi = {
  async createEnrollment(data: { courseName: string; studentName: string; studentId: string; courseID: string }): Promise<EnrollmentResponse> {
    await delay()
    // Check for duplicate
    const exists = MOCK_ENROLLMENTS.some(
      (e) => e.userId === data.studentId && e.courseId === data.courseID
    )
    if (exists) {
      throw { message: 'Already enrolled in this course', status: 409 }
    }

    const enrollment = {
      enrollmentId: generateMockId(),
      userId: data.studentId,
      courseId: data.courseID,
      courseName: data.courseName,
      studentName: data.studentName,
      status: 'ACTIVE' as Status,
      progress: 0,
      enrollAt: new Date().toISOString(),
    }
    MOCK_ENROLLMENTS.push(enrollment)
    return enrollment
  },

  async getEnrollments(studentId: string): Promise<EnrollmentResponse[]> {
    await delay()
    return MOCK_ENROLLMENTS
      .filter((e) => e.userId === studentId)
      .map(({ courseName, studentName, status, progress, enrollAt }) => ({
        courseName,
        studentName,
        status,
        progress,
        enrollAt,
      }))
  },

  // For instructor: get enrollments for their courses
  async getEnrollmentsForInstructor(instructorId: string): Promise<EnrollmentResponse[]> {
    await delay()
    const instructorCourseIds = MOCK_COURSES
      .filter((c) => c.instructorId === instructorId)
      .map((c) => c.courseId)

    return MOCK_ENROLLMENTS
      .filter((e) => instructorCourseIds.includes(e.courseId))
      .map(({ courseName, studentName, status, progress, enrollAt }) => ({
        courseName,
        studentName,
        status,
        progress,
        enrollAt,
      }))
  },
}

export const mockAdminApi = {
  async getUsers(roles: Role[]): Promise<UserResponse[]> {
    await delay()
    return MOCK_USERS
      .filter((u) => roles.includes(u.role))
      .map((u) => ({
        UserId: u.userId,
        UserName: u.userName,
        UserEmail: u.userEmail,
        CreatedAt: u.createdAt,
        role: u.role,
      }))
  },

  async deleteUser(userId: string): Promise<void> {
    await delay()
    if (userId === 'u-008') {
      throw { message: 'Cannot delete the main admin account', status: 403 }
    }
    const index = MOCK_USERS.findIndex((u) => u.userId === userId)
    if (index === -1) throw { message: 'User not found', status: 404 }
    MOCK_USERS.splice(index, 1)
    // Also remove related enrollments
    MOCK_ENROLLMENTS = MOCK_ENROLLMENTS.filter((e) => e.userId !== userId)
  },
}

// Demo account credentials for display
export const DEMO_CREDENTIALS = DEMO_ACCOUNTS.map((a) => ({
  email: a.email,
  password: a.password,
  role: a.role,
}))
