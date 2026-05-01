// API Types
export type Role = 'INSTRUCTOR' | 'STUDENT' | 'ADMIN'
export type Status = 'COMPLETED' | 'ACTIVE' | 'DROPPED'

export interface AuthResponse {
    logInId: string
    fullName: string
    logInEmail: string
    role: Role
    token: string
    message: string
}

// Raw response from backend (field names match Java record exactly)
interface RawAuthResponse {
    logInId: string | null
    fallName: string | null  // Backend has a typo: "fallName" instead of "fullName"
    fullName: string | null  // In case the backend fixes the typo
    logInEmail: string | null
    role: Role | null
    token: string | null
    message: string | null
}

export interface LoginRequest {
    logInEmail: string
    logInPassword: string
}

export interface SignUpRequest {
    userEmail: string
    firstName: string
    lastName: string
    role: Role
    password: string
}

export interface ResetPasswordRequest {
    userEmail: string
    newPassword: string
}

export interface CourseResponse {
    instructorId: string
    courseId: string
    courseTitle: string
    instructorName: string
    createdAt: string
    updatedAt: string
    courseDuration: string
}

export interface CreateCourseRequest {
    courseName: string
    instructorName: string
    courseDuration: string
}

export interface UpdateCourseRequest {
    courseName: string
    instructorName: string
    courseDuration: string
}

export interface EnrollmentResponse {
    courseName: string
    studentName: string
    status: Status
    enrollAt: string
    progress: number
}

export interface CreateEnrollmentRequest {
    courseName: string
    studentName: string
    studentId: string
    courseID: string
}

export interface UserResponse {
    UserId: string
    UserName: string
    UserEmail: string
    CreatedAt: string
    role: Role
}

export interface PageResponse<T> {
    content: T[]
    totalElements: number
    totalPages: number
    number: number
    size: number
    first: boolean
    last: boolean
    empty: boolean
}

export interface CourseQueryParams {
    courseTitle?: string
    createdFrom?: string
    createdTo?: string
    updatedFrom?: string
    updatedTo?: string
    page?: number
    size?: number
    sort?: string
}

// ========== Mock Mode Detection ==========

let _isMockMode: boolean | null = null

export function isMockMode(): boolean {
    return _isMockMode === true
}

export function setMockMode(value: boolean): void {
    _isMockMode = value
}

// Simple health check — just try to connect, don't send invalid credentials
async function checkBackendAvailable(): Promise<boolean> {
    try {
        const controller = new AbortController()
        const timeoutId = setTimeout(() => controller.abort(), 3000)
        // Use a simple GET to check if the backend is reachable
        // We don't send login credentials to avoid triggering rate limits or validation errors
        const response = await fetch('/api/v1/auth/login', {
            method: 'OPTIONS',
            signal: controller.signal,
        })
        clearTimeout(timeoutId)
        // If we get ANY response (even 405 Method Not Allowed), the backend is running
        if (response.status === 502) {
            _isMockMode = true
            return true
        }
        _isMockMode = false
        return false
    } catch {
        _isMockMode = true
        return true
    }
}

// Start detection on first load
if (typeof window !== 'undefined') {
    checkBackendAvailable()
}

// Base API URL — uses the Next.js proxy route
const API_BASE = '/api/v1'

class ApiError extends Error {
    status: number
    data: unknown

    constructor(message: string, status: number, data?: unknown) {
        super(message)
        this.name = 'ApiError'
        this.status = status
        this.data = data
    }
}

// Convert raw backend auth response to our clean interface
function mapAuthResponse(raw: RawAuthResponse): AuthResponse {
    return {
        logInId: raw.logInId || '',
        // Handle the backend typo: "fallName" instead of "fullName"
        fullName: raw.fullName || raw.fallName || '',
        logInEmail: raw.logInEmail || '',
        role: raw.role || 'STUDENT',
        token: raw.token || '',
        message: raw.message || '',
    }
}

async function apiFetch<T>(
    endpoint: string,
    options: RequestInit = {},
    token?: string
): Promise<T> {
    const url = `${API_BASE}${endpoint}`

    const headers: HeadersInit = {
        'Content-Type': 'application/json',
        ...options.headers,
    }

    if (token) {
        (headers as Record<string, string>)['Authorization'] = `Bearer ${token}`
    }

    const response = await fetch(url, {
        ...options,
        headers,
    })

    if (!response.ok) {
        let errorMessage = 'An unexpected error occurred'
        let errorData: unknown
        try {
            const contentType = response.headers.get('content-type')
            if (contentType && contentType.includes('application/json')) {
                errorData = await response.json()
                errorMessage =
                    (errorData as Record<string, string>).message ||
                    (errorData as Record<string, string>).error ||
                    errorMessage
            } else {
                const text = await response.text()
                errorMessage = text || errorMessage
                errorData = {message: text}
            }
        } catch {
            errorMessage = response.statusText || errorMessage
        }

        throw new ApiError(errorMessage, response.status, errorData)
    }

    if (response.status === 204) {
        return undefined as T
    }

    const contentType = response.headers.get('content-type')
    if (contentType && contentType.includes('application/json')) {
        return response.json()
    }

    return response.text() as Promise<T>
}

// ========== Auth API ==========

export const authApi = {
    login: async (data: LoginRequest): Promise<AuthResponse> => {
        if (_isMockMode) {
            const {mockAuthApi} = await import('./mock-api')
            return mockAuthApi.login(data.logInEmail, data.logInPassword)
        }
        const raw = await apiFetch<RawAuthResponse>('/auth/login', {
            method: 'POST',
            body: JSON.stringify(data),
        })
        return mapAuthResponse(raw)
    },

    signUp: async (data: SignUpRequest): Promise<AuthResponse> => {
        if (_isMockMode) {
            const {mockAuthApi} = await import('./mock-api')
            return mockAuthApi.signUp(data)
        }
        const raw = await apiFetch<RawAuthResponse>('/auth/sign', {
            method: 'POST',
            body: JSON.stringify(data),
        })
        return mapAuthResponse(raw)
    },

    resetPassword: async (data: ResetPasswordRequest): Promise<AuthResponse> => {
        if (_isMockMode) {
            const {mockAuthApi} = await import('./mock-api')
            return mockAuthApi.resetPassword(data.userEmail, data.newPassword)
        }
        const raw = await apiFetch<RawAuthResponse>('/auth/resetPassword', {
            method: 'POST',
            body: JSON.stringify(data),
        })
        return mapAuthResponse(raw)
    },

    deleteUser: (userId: string, token: string) =>
        apiFetch<string>(`/auth/${userId}`, {
            method: 'DELETE',
        }, token),
}

// ========== Admin API ==========

export const adminApi = {
    getUsers: async (roles: Role[], token: string): Promise<UserResponse[]> => {
        if (_isMockMode) {
            const {mockAdminApi} = await import('./mock-api')
            return mockAdminApi.getUsers(roles)
        }
        return apiFetch<UserResponse[]>('/admin/Users', {
            method: 'POST',
            body: JSON.stringify(roles),
        }, token)
    },

    deleteUser: async (id: string, token: string): Promise<string> => {
        if (_isMockMode) {
            const {mockAdminApi} = await import('./mock-api')
            await mockAdminApi.deleteUser(id)
            return 'User deleted successfully'
        }
        return apiFetch<string>(`/admin/${id}`, {
            method: 'DELETE',
        }, token)
    },
}

// ========== Instructor API ==========

export const instructorApi = {
    createCourse: async (data: CreateCourseRequest, token: string): Promise<CourseResponse> => {
        if (_isMockMode) {
            const {mockInstructorApi} = await import('./mock-api')
            const {useAuthStore} = await import('./auth-store')
            const userId = useAuthStore.getState().user?.logInId || ''
            return mockInstructorApi.createCourse(data, userId)
        }
        return apiFetch<CourseResponse>('/instructor/createCourse', {
            method: 'POST',
            body: JSON.stringify(data),
        }, token)
    },

    deleteCourse: async (id: string, token: string): Promise<CourseResponse> => {
        if (_isMockMode) {
            const {mockInstructorApi} = await import('./mock-api')
            return mockInstructorApi.deleteCourse(id)
        }
        return apiFetch<CourseResponse>(`/instructor/deleteCourse/${id}`, {
            method: 'DELETE',
        }, token)
    },

    updateCourse: async (id: string, data: UpdateCourseRequest, token: string): Promise<CourseResponse> => {
        if (_isMockMode) {
            const {mockInstructorApi} = await import('./mock-api')
            return mockInstructorApi.updateCourse(id, data)
        }
        return apiFetch<CourseResponse>(`/instructor/updateCourse/${id}`, {
            method: 'PUT',
            body: JSON.stringify(data),
        }, token)
    },
}

// ========== Student API ==========

export const studentApi = {
    getCourses: async (params: CourseQueryParams, token: string): Promise<PageResponse<CourseResponse>> => {
        if (_isMockMode) {
            const {mockStudentApi} = await import('./mock-api')
            return mockStudentApi.getCourses(params)
        }
        const searchParams = new URLSearchParams()
        if (params.courseTitle) searchParams.set('courseTitle', params.courseTitle)
        if (params.createdFrom) searchParams.set('createdFrom', params.createdFrom)
        if (params.createdTo) searchParams.set('createdTo', params.createdTo)
        if (params.updatedFrom) searchParams.set('updatedFrom', params.updatedFrom)
        if (params.updatedTo) searchParams.set('updatedTo', params.updatedTo)
        searchParams.set('page', String(params.page ?? 0))
        searchParams.set('size', String(params.size ?? 10))
        if (params.sort) searchParams.set('sort', params.sort)

        const query = searchParams.toString()
        return apiFetch<PageResponse<CourseResponse>>(
            `/student/courses${query ? `?${query}` : ''}`,
            {method: 'GET'},
            token
        )
    },
}

// ========== Enrollment API ==========

export const enrollmentApi = {
    createEnrollment: async (data: CreateEnrollmentRequest, token: string): Promise<EnrollmentResponse> => {
        if (_isMockMode) {
            const {mockEnrollmentApi} = await import('./mock-api')
            return mockEnrollmentApi.createEnrollment(data)
        }
        return apiFetch<EnrollmentResponse>('/enrollment/createEnroll', {
            method: 'POST',
            body: JSON.stringify(data),
        }, token)
    },

    getEnrollments: async (studentId: string, token: string): Promise<EnrollmentResponse[]> => {
        if (_isMockMode) {
            const {mockEnrollmentApi} = await import('./mock-api')
            const {useAuthStore} = await import('./auth-store')
            const role = useAuthStore.getState().user?.role
            if (role === 'INSTRUCTOR') {
                return mockEnrollmentApi.getEnrollmentsForInstructor(studentId)
            }
            return mockEnrollmentApi.getEnrollments(studentId)
        }
        return apiFetch<EnrollmentResponse[]>(`/enrollment/${studentId}`, {
            method: 'GET',
        }, token)
    },
}

// Helper to format date strings from the API
export function formatDate(dateStr: string): string {
    if (!dateStr) return 'N/A'
    try {
        const date = new Date(dateStr)
        return date.toLocaleDateString('en-US', {
            year: 'numeric',
            month: 'short',
            day: 'numeric',
        })
    } catch {
        return dateStr
    }
}

// Helper to format duration (LocalDateTime displays as time)
export function formatDuration(duration: string): string {
    if (!duration) return 'N/A'
    if (duration.includes('T')) {
        const timePart = duration.split('T')[1]
        return timePart || duration
    }
    return duration
}

export {ApiError}
