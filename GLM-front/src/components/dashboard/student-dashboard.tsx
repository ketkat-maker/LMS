'use client'

import { useState, useEffect, useCallback } from 'react'
import { studentApi, enrollmentApi, type CourseResponse, type EnrollmentResponse, type PageResponse, type CourseQueryParams, formatDate, formatDuration, ApiError } from '@/lib/api'
import { useAuthStore } from '@/lib/auth-store'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Badge } from '@/components/ui/badge'
import { Progress } from '@/components/ui/progress'
import { Skeleton } from '@/components/ui/skeleton'
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs'
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from '@/components/ui/table'
import { useToast } from '@/hooks/use-toast'
import { EnrollmentForm } from '@/components/enrollments/enrollment-form'
import type { Status } from '@/lib/api'
import {
  Search,
  ChevronLeft,
  ChevronRight,
  BookOpen,
  Clock,
  User,
  Calendar,
  RefreshCw,
  ClipboardList,
  GraduationCap,
  LayoutDashboard,
  Plus,
} from 'lucide-react'

function getStatusBadgeVariant(status: Status) {
  switch (status) {
    case 'ACTIVE': return 'default' as const
    case 'COMPLETED': return 'secondary' as const
    case 'DROPPED': return 'destructive' as const
    default: return 'outline' as const
  }
}

type StudentView = 'overview' | 'courses' | 'enrollments'

interface StudentDashboardProps {
  activeView: string
}

export function StudentDashboard({ activeView }: StudentDashboardProps) {
  const { user } = useAuthStore()
  const { toast } = useToast()
  const [currentTab, setCurrentTab] = useState<string>(
    activeView === 'enrollments' ? 'enrollments' : activeView === 'courses' ? 'courses' : 'overview'
  )

  // Courses state
  const [courses, setCourses] = useState<CourseResponse[]>([])
  const [coursesLoading, setCoursesLoading] = useState(false)
  const [searchTitle, setSearchTitle] = useState('')
  const [page, setPage] = useState(0)
  const [totalPages, setTotalPages] = useState(0)
  const [totalElements, setTotalElements] = useState(0)
  const pageSize = 10

  // Enrollments state
  const [enrollments, setEnrollments] = useState<EnrollmentResponse[]>([])
  const [enrollmentsLoading, setEnrollmentsLoading] = useState(false)

  // Enrollment form state
  const [enrollDialogOpen, setEnrollDialogOpen] = useState(false)
  const [selectedCourse, setSelectedCourse] = useState<CourseResponse | null>(null)

  const fetchCourses = useCallback(async () => {
    if (!user?.token) return
    setCoursesLoading(true)
    try {
      const params: CourseQueryParams = {
        courseTitle: searchTitle || undefined,
        page,
        size: pageSize,
        sort: 'createdAt,desc',
      }
      const data = await studentApi.getCourses(params, user.token)
      setCourses(data.content || [])
      setTotalPages(data.totalPages || 0)
      setTotalElements(data.totalElements || 0)
    } catch (error) {
      if (error instanceof ApiError && error.status === 401) {
        useAuthStore.getState().logout()
      } else {
        toast({ title: 'Error', description: 'Failed to fetch courses', variant: 'destructive' })
      }
    } finally {
      setCoursesLoading(false)
    }
  }, [user?.token, searchTitle, page, toast])

  const fetchEnrollments = useCallback(async () => {
    if (!user?.token) return
    setEnrollmentsLoading(true)
    try {
      const data = await enrollmentApi.getEnrollments(user.logInId, user.token)
      setEnrollments(Array.isArray(data) ? data : [])
    } catch (error) {
      if (error instanceof ApiError && error.status === 401) {
        useAuthStore.getState().logout()
      } else {
        toast({ title: 'Error', description: 'Failed to fetch enrollments', variant: 'destructive' })
      }
    } finally {
      setEnrollmentsLoading(false)
    }
  }, [user?.token, toast])

  useEffect(() => {
    if (user?.token) {
      fetchCourses()
      fetchEnrollments()
    }
  }, [user?.token, fetchCourses, fetchEnrollments])

  useEffect(() => {
    if (activeView === 'enrollments') setCurrentTab('enrollments')
    else if (activeView === 'courses') setCurrentTab('courses')
    else setCurrentTab('overview')
  }, [activeView])

  const handleEnroll = (course: CourseResponse) => {
    setSelectedCourse(course)
    setEnrollDialogOpen(true)
  }

  const handleEnrollSuccess = () => {
    fetchEnrollments()
    fetchCourses()
  }

  const activeEnrollments = enrollments.filter((e) => e.status === 'ACTIVE')
  const completedEnrollments = enrollments.filter((e) => e.status === 'COMPLETED')

  return (
    <div className="p-4 md:p-6 space-y-6 max-w-7xl mx-auto">
      {/* Welcome header */}
      <div>
        <h1 className="text-2xl md:text-3xl font-bold">Student Dashboard</h1>
        <p className="text-muted-foreground mt-1">Welcome back, {user?.fullName}!</p>
      </div>

      <Tabs value={currentTab} onValueChange={setCurrentTab}>
        <TabsList className="grid w-full grid-cols-3">
          <TabsTrigger value="overview" className="gap-2">
            <LayoutDashboard className="h-4 w-4 hidden sm:block" />
            Overview
          </TabsTrigger>
          <TabsTrigger value="courses" className="gap-2">
            <BookOpen className="h-4 w-4 hidden sm:block" />
            Courses
          </TabsTrigger>
          <TabsTrigger value="enrollments" className="gap-2">
            <ClipboardList className="h-4 w-4 hidden sm:block" />
            My Enrollments
          </TabsTrigger>
        </TabsList>

        {/* Overview Tab */}
        <TabsContent value="overview" className="space-y-6 mt-6">
          {/* Stats Cards */}
          <div className="grid grid-cols-1 sm:grid-cols-3 gap-4">
            <Card>
              <CardContent className="p-6">
                <div className="flex items-center gap-4">
                  <div className="rounded-lg bg-primary/10 p-3">
                    <BookOpen className="h-6 w-6 text-primary" />
                  </div>
                  <div>
                    <p className="text-sm text-muted-foreground">Available Courses</p>
                    <p className="text-2xl font-bold">{totalElements}</p>
                  </div>
                </div>
              </CardContent>
            </Card>
            <Card>
              <CardContent className="p-6">
                <div className="flex items-center gap-4">
                  <div className="rounded-lg bg-green-500/10 p-3">
                    <GraduationCap className="h-6 w-6 text-green-600" />
                  </div>
                  <div>
                    <p className="text-sm text-muted-foreground">Active Enrollments</p>
                    <p className="text-2xl font-bold">{activeEnrollments.length}</p>
                  </div>
                </div>
              </CardContent>
            </Card>
            <Card>
              <CardContent className="p-6">
                <div className="flex items-center gap-4">
                  <div className="rounded-lg bg-yellow-500/10 p-3">
                    <ClipboardList className="h-6 w-6 text-yellow-600" />
                  </div>
                  <div>
                    <p className="text-sm text-muted-foreground">Completed</p>
                    <p className="text-2xl font-bold">{completedEnrollments.length}</p>
                  </div>
                </div>
              </CardContent>
            </Card>
          </div>

          {/* Quick: Recent Enrollments */}
          <Card>
            <CardHeader className="pb-3">
              <div className="flex items-center justify-between">
                <CardTitle className="text-lg flex items-center gap-2">
                  <ClipboardList className="h-5 w-5" />
                  Recent Enrollments
                </CardTitle>
                <Button variant="outline" size="sm" onClick={() => setCurrentTab('enrollments')}>
                  View All
                </Button>
              </div>
            </CardHeader>
            <CardContent>
              {enrollmentsLoading ? (
                <div className="space-y-3">
                  {Array.from({ length: 3 }).map((_, i) => (
                    <Skeleton key={i} className="h-12 w-full" />
                  ))}
                </div>
              ) : enrollments.length === 0 ? (
                <div className="text-center py-8">
                  <ClipboardList className="h-10 w-10 text-muted-foreground/50 mx-auto mb-3" />
                  <p className="text-muted-foreground">No enrollments yet</p>
                  <Button variant="outline" size="sm" className="mt-3" onClick={() => setCurrentTab('courses')}>
                    Browse Courses
                  </Button>
                </div>
              ) : (
                <div className="space-y-3">
                  {enrollments.slice(0, 5).map((enrollment, index) => (
                    <div key={index} className="flex items-center justify-between p-3 rounded-lg border">
                      <div className="flex items-center gap-3">
                        <BookOpen className="h-4 w-4 text-muted-foreground" />
                        <div>
                          <p className="font-medium text-sm">{enrollment.courseName}</p>
                          <p className="text-xs text-muted-foreground">
                            Enrolled: {formatDate(enrollment.enrollAt)}
                          </p>
                        </div>
                      </div>
                      <div className="flex items-center gap-3">
                        <div className="flex items-center gap-2 min-w-[100px]">
                          <Progress value={enrollment.progress || 0} className="h-2 flex-1" />
                          <span className="text-xs text-muted-foreground w-8 text-right">
                            {enrollment.progress || 0}%
                          </span>
                        </div>
                        <Badge variant={getStatusBadgeVariant(enrollment.status)}>
                          {enrollment.status}
                        </Badge>
                      </div>
                    </div>
                  ))}
                </div>
              )}
            </CardContent>
          </Card>
        </TabsContent>

        {/* Courses Tab */}
        <TabsContent value="courses" className="space-y-4 mt-6">
          {/* Search Bar */}
          <Card>
            <CardContent className="p-4">
              <div className="flex flex-col sm:flex-row gap-3">
                <div className="relative flex-1">
                  <Search className="absolute left-3 top-3 h-4 w-4 text-muted-foreground" />
                  <Input
                    placeholder="Search courses by title..."
                    value={searchTitle}
                    onChange={(e) => setSearchTitle(e.target.value)}
                    onKeyDown={(e) => e.key === 'Enter' && fetchCourses()}
                    className="pl-10"
                  />
                </div>
                <div className="flex gap-2">
                  <Button onClick={() => { setPage(0); fetchCourses() }}>
                    <Search className="h-4 w-4 mr-2" />
                    Search
                  </Button>
                  <Button variant="outline" onClick={() => { setSearchTitle(''); setPage(0) }}>
                    Clear
                  </Button>
                </div>
              </div>
            </CardContent>
          </Card>

          <div className="flex items-center justify-between">
            <p className="text-sm text-muted-foreground">
              {totalElements} course{totalElements !== 1 ? 's' : ''} found
            </p>
            <Button variant="ghost" size="sm" onClick={fetchCourses} disabled={coursesLoading}>
              <RefreshCw className={`h-4 w-4 mr-1 ${coursesLoading ? 'animate-spin' : ''}`} />
              Refresh
            </Button>
          </div>

          <Card>
            <CardContent className="p-0">
              {coursesLoading ? (
                <div className="p-6 space-y-3">
                  {Array.from({ length: 5 }).map((_, i) => (
                    <Skeleton key={i} className="h-12 w-full" />
                  ))}
                </div>
              ) : courses.length === 0 ? (
                <div className="flex flex-col items-center justify-center py-12">
                  <BookOpen className="h-12 w-12 text-muted-foreground/50 mb-4" />
                  <h3 className="text-lg font-medium">No courses found</h3>
                  <p className="text-sm text-muted-foreground mt-1">Try adjusting your search</p>
                </div>
              ) : (
                <>
                  <div className="hidden md:block">
                    <Table>
                      <TableHeader>
                        <TableRow>
                          <TableHead>Course Title</TableHead>
                          <TableHead>Instructor</TableHead>
                          <TableHead>Duration</TableHead>
                          <TableHead>Created</TableHead>
                          <TableHead className="text-right">Action</TableHead>
                        </TableRow>
                      </TableHeader>
                      <TableBody>
                        {courses.map((course) => (
                          <TableRow key={course.courseId}>
                            <TableCell className="font-medium">
                              <div className="flex items-center gap-2">
                                <BookOpen className="h-4 w-4 text-muted-foreground" />
                                {course.courseTitle}
                              </div>
                            </TableCell>
                            <TableCell>
                              <div className="flex items-center gap-1">
                                <User className="h-3 w-3 text-muted-foreground" />
                                {course.instructorName}
                              </div>
                            </TableCell>
                            <TableCell>
                              <Badge variant="outline" className="gap-1">
                                <Clock className="h-3 w-3" />
                                {formatDuration(course.courseDuration)}
                              </Badge>
                            </TableCell>
                            <TableCell>
                              <span className="text-muted-foreground text-sm flex items-center gap-1">
                                <Calendar className="h-3 w-3" />
                                {formatDate(course.createdAt)}
                              </span>
                            </TableCell>
                            <TableCell className="text-right">
                              <Button size="sm" onClick={() => handleEnroll(course)}>
                                <Plus className="h-4 w-4 mr-1" />
                                Enroll
                              </Button>
                            </TableCell>
                          </TableRow>
                        ))}
                      </TableBody>
                    </Table>
                  </div>
                  <div className="md:hidden space-y-3 p-4">
                    {courses.map((course) => (
                      <Card key={course.courseId} className="p-4">
                        <div className="space-y-2">
                          <div className="flex items-start justify-between">
                            <h3 className="font-medium flex items-center gap-2">
                              <BookOpen className="h-4 w-4 text-muted-foreground" />
                              {course.courseTitle}
                            </h3>
                            <Button size="sm" onClick={() => handleEnroll(course)}>
                              Enroll
                            </Button>
                          </div>
                          <div className="flex flex-wrap gap-2 text-sm text-muted-foreground">
                            <span className="flex items-center gap-1">
                              <User className="h-3 w-3" />
                              {course.instructorName}
                            </span>
                            <Badge variant="outline" className="gap-1 text-xs">
                              <Clock className="h-3 w-3" />
                              {formatDuration(course.courseDuration)}
                            </Badge>
                          </div>
                        </div>
                      </Card>
                    ))}
                  </div>
                </>
              )}
            </CardContent>
          </Card>

          {/* Pagination */}
          {totalPages > 1 && (
            <div className="flex items-center justify-between">
              <p className="text-sm text-muted-foreground">Page {page + 1} of {totalPages}</p>
              <div className="flex gap-2">
                <Button variant="outline" size="sm" onClick={() => setPage((p) => Math.max(0, p - 1))} disabled={page === 0 || coursesLoading}>
                  <ChevronLeft className="h-4 w-4 mr-1" /> Previous
                </Button>
                <Button variant="outline" size="sm" onClick={() => setPage((p) => Math.min(totalPages - 1, p + 1))} disabled={page >= totalPages - 1 || coursesLoading}>
                  Next <ChevronRight className="h-4 w-4 ml-1" />
                </Button>
              </div>
            </div>
          )}
        </TabsContent>

        {/* Enrollments Tab */}
        <TabsContent value="enrollments" className="space-y-4 mt-6">
          <div className="flex items-center justify-between">
            <p className="text-sm text-muted-foreground">
              {enrollments.length} enrollment{enrollments.length !== 1 ? 's' : ''}
            </p>
            <Button variant="ghost" size="sm" onClick={fetchEnrollments} disabled={enrollmentsLoading}>
              <RefreshCw className={`h-4 w-4 mr-1 ${enrollmentsLoading ? 'animate-spin' : ''}`} />
              Refresh
            </Button>
          </div>

          <Card>
            <CardContent className="p-0">
              {enrollmentsLoading ? (
                <div className="p-6 space-y-3">
                  {Array.from({ length: 4 }).map((_, i) => (
                    <Skeleton key={i} className="h-12 w-full" />
                  ))}
                </div>
              ) : enrollments.length === 0 ? (
                <div className="flex flex-col items-center justify-center py-12">
                  <ClipboardList className="h-12 w-12 text-muted-foreground/50 mb-4" />
                  <h3 className="text-lg font-medium">No enrollments yet</h3>
                  <p className="text-sm text-muted-foreground mt-1">Browse courses and enroll to get started</p>
                  <Button variant="outline" size="sm" className="mt-3" onClick={() => setCurrentTab('courses')}>
                    Browse Courses
                  </Button>
                </div>
              ) : (
                <>
                  <div className="hidden md:block">
                    <Table>
                      <TableHeader>
                        <TableRow>
                          <TableHead>Course</TableHead>
                          <TableHead>Student</TableHead>
                          <TableHead>Status</TableHead>
                          <TableHead>Progress</TableHead>
                          <TableHead>Enrolled Date</TableHead>
                        </TableRow>
                      </TableHeader>
                      <TableBody>
                        {enrollments.map((enrollment, index) => (
                          <TableRow key={index}>
                            <TableCell className="font-medium">
                              <div className="flex items-center gap-2">
                                <BookOpen className="h-4 w-4 text-muted-foreground" />
                                {enrollment.courseName}
                              </div>
                            </TableCell>
                            <TableCell>
                              <div className="flex items-center gap-1">
                                <User className="h-3 w-3 text-muted-foreground" />
                                {enrollment.studentName}
                              </div>
                            </TableCell>
                            <TableCell>
                              <Badge variant={getStatusBadgeVariant(enrollment.status)}>
                                {enrollment.status}
                              </Badge>
                            </TableCell>
                            <TableCell>
                              <div className="flex items-center gap-2 min-w-[120px]">
                                <Progress value={enrollment.progress || 0} className="h-2 flex-1" />
                                <span className="text-xs text-muted-foreground w-8 text-right">
                                  {enrollment.progress || 0}%
                                </span>
                              </div>
                            </TableCell>
                            <TableCell>
                              <span className="text-muted-foreground text-sm flex items-center gap-1">
                                <Calendar className="h-3 w-3" />
                                {formatDate(enrollment.enrollAt)}
                              </span>
                            </TableCell>
                          </TableRow>
                        ))}
                      </TableBody>
                    </Table>
                  </div>
                  <div className="md:hidden space-y-3 p-4">
                    {enrollments.map((enrollment, index) => (
                      <Card key={index} className="p-4">
                        <div className="space-y-3">
                          <div className="flex items-start justify-between">
                            <h3 className="font-medium flex items-center gap-2">
                              <BookOpen className="h-4 w-4 text-muted-foreground" />
                              {enrollment.courseName}
                            </h3>
                            <Badge variant={getStatusBadgeVariant(enrollment.status)}>
                              {enrollment.status}
                            </Badge>
                          </div>
                          <div className="flex items-center gap-2">
                            <Progress value={enrollment.progress || 0} className="h-2 flex-1" />
                            <span className="text-xs text-muted-foreground">{enrollment.progress || 0}%</span>
                          </div>
                          <div className="flex justify-between text-xs text-muted-foreground">
                            <span className="flex items-center gap-1"><User className="h-3 w-3" />{enrollment.studentName}</span>
                            <span className="flex items-center gap-1"><Calendar className="h-3 w-3" />{formatDate(enrollment.enrollAt)}</span>
                          </div>
                        </div>
                      </Card>
                    ))}
                  </div>
                </>
              )}
            </CardContent>
          </Card>
        </TabsContent>
      </Tabs>

      {/* Enrollment Dialog */}
      <EnrollmentForm
        open={enrollDialogOpen}
        onOpenChange={setEnrollDialogOpen}
        course={selectedCourse}
        onSuccess={handleEnrollSuccess}
      />
    </div>
  )
}
