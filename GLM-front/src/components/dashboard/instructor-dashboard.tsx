'use client'

import { useState, useEffect, useCallback } from 'react'
import { instructorApi, enrollmentApi, type CourseResponse, type EnrollmentResponse, formatDate, formatDuration, ApiError } from '@/lib/api'
import { useAuthStore } from '@/lib/auth-store'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import { Button } from '@/components/ui/button'
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
import {
  AlertDialog,
  AlertDialogAction,
  AlertDialogCancel,
  AlertDialogContent,
  AlertDialogDescription,
  AlertDialogFooter,
  AlertDialogHeader,
  AlertDialogTitle,
} from '@/components/ui/alert-dialog'
import { useToast } from '@/hooks/use-toast'
import { CourseForm } from '@/components/courses/course-form'
import type { Status } from '@/lib/api'
import {
  BookOpen,
  Plus,
  Pencil,
  Trash2,
  RefreshCw,
  Clock,
  User,
  Calendar,
  ClipboardList,
  GraduationCap,
  LayoutDashboard,
  Loader2,
} from 'lucide-react'

function getStatusBadgeVariant(status: Status) {
  switch (status) {
    case 'ACTIVE': return 'default' as const
    case 'COMPLETED': return 'secondary' as const
    case 'DROPPED': return 'destructive' as const
    default: return 'outline' as const
  }
}

interface InstructorDashboardProps {
  activeView: string
}

export function InstructorDashboard({ activeView }: InstructorDashboardProps) {
  const { user, logout } = useAuthStore()
  const { toast } = useToast()
  const [currentTab, setCurrentTab] = useState<string>(
    activeView === 'student-enrollments' ? 'enrollments' : activeView === 'manage-courses' ? 'courses' : 'overview'
  )

  // Courses state
  const [courses, setCourses] = useState<CourseResponse[]>([])
  const [coursesLoading, setCoursesLoading] = useState(false)

  // Enrollments state
  const [enrollments, setEnrollments] = useState<EnrollmentResponse[]>([])
  const [enrollmentsLoading, setEnrollmentsLoading] = useState(false)

  // Course form state
  const [courseFormOpen, setCourseFormOpen] = useState(false)
  const [editingCourse, setEditingCourse] = useState<CourseResponse | null>(null)

  // Delete confirmation state
  const [deleteTarget, setDeleteTarget] = useState<CourseResponse | null>(null)
  const [deleting, setDeleting] = useState(false)

  const fetchCourses = useCallback(async () => {
    if (!user?.token) return
    setCoursesLoading(true)
    try {
      // Instructors can use student endpoint to list courses
      const { studentApi } = await import('@/lib/api')
      const data = await studentApi.getCourses({ page: 0, size: 100, sort: 'createdAt,desc' }, user.token)
      // Filter courses that belong to this instructor
      const allCourses = data.content || []
      setCourses(allCourses.filter((c) => c.instructorId === user.logInId))
    } catch (error) {
      if (error instanceof ApiError && error.status === 401) {
        logout()
      } else {
        toast({ title: 'Error', description: 'Failed to fetch courses', variant: 'destructive' })
      }
    } finally {
      setCoursesLoading(false)
    }
  }, [user?.token, user?.logInId, logout, toast])

  const fetchEnrollments = useCallback(async () => {
    if (!user?.token) return
    setEnrollmentsLoading(true)
    try {
      const data = await enrollmentApi.getEnrollments(user.logInId, user.token)
      setEnrollments(Array.isArray(data) ? data : [])
    } catch (error) {
      if (error instanceof ApiError && error.status === 401) {
        logout()
      } else {
        toast({ title: 'Error', description: 'Failed to fetch enrollments', variant: 'destructive' })
      }
    } finally {
      setEnrollmentsLoading(false)
    }
  }, [user?.token, user?.logInId, logout, toast])

  useEffect(() => {
    if (user?.token) {
      fetchCourses()
      fetchEnrollments()
    }
  }, [user?.token, fetchCourses, fetchEnrollments])

  useEffect(() => {
    if (activeView === 'student-enrollments') setCurrentTab('enrollments')
    else if (activeView === 'manage-courses') setCurrentTab('courses')
    else setCurrentTab('overview')
  }, [activeView])

  const handleCreateCourse = () => {
    setEditingCourse(null)
    setCourseFormOpen(true)
  }

  const handleEditCourse = (course: CourseResponse) => {
    setEditingCourse(course)
    setCourseFormOpen(true)
  }

  const handleDeleteCourse = async () => {
    if (!deleteTarget || !user?.token) return
    setDeleting(true)
    try {
      await instructorApi.deleteCourse(deleteTarget.courseId, user.token)
      toast({
        title: 'Course Deleted',
        description: `"${deleteTarget.courseTitle}" has been deleted.`,
      })
      setDeleteTarget(null)
      fetchCourses()
    } catch (error) {
      const message = error instanceof ApiError ? error.message : 'Delete failed.'
      toast({ title: 'Delete Failed', description: message, variant: 'destructive' })
    } finally {
      setDeleting(false)
    }
  }

  return (
    <div className="p-4 md:p-6 space-y-6 max-w-7xl mx-auto">
      {/* Welcome header */}
      <div>
        <h1 className="text-2xl md:text-3xl font-bold">Instructor Dashboard</h1>
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
            My Courses
          </TabsTrigger>
          <TabsTrigger value="enrollments" className="gap-2">
            <ClipboardList className="h-4 w-4 hidden sm:block" />
            Enrollments
          </TabsTrigger>
        </TabsList>

        {/* Overview Tab */}
        <TabsContent value="overview" className="space-y-6 mt-6">
          <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
            <Card>
              <CardContent className="p-6">
                <div className="flex items-center gap-4">
                  <div className="rounded-lg bg-primary/10 p-3">
                    <BookOpen className="h-6 w-6 text-primary" />
                  </div>
                  <div>
                    <p className="text-sm text-muted-foreground">My Courses</p>
                    <p className="text-2xl font-bold">{courses.length}</p>
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
                    <p className="text-sm text-muted-foreground">Total Enrollments</p>
                    <p className="text-2xl font-bold">{enrollments.length}</p>
                  </div>
                </div>
              </CardContent>
            </Card>
          </div>

          {/* Quick: Recent Courses */}
          <Card>
            <CardHeader className="pb-3">
              <div className="flex items-center justify-between">
                <CardTitle className="text-lg flex items-center gap-2">
                  <BookOpen className="h-5 w-5" />
                  Recent Courses
                </CardTitle>
                <Button variant="outline" size="sm" onClick={() => setCurrentTab('courses')}>
                  View All
                </Button>
              </div>
            </CardHeader>
            <CardContent>
              {coursesLoading ? (
                <div className="space-y-3">
                  {Array.from({ length: 3 }).map((_, i) => (
                    <Skeleton key={i} className="h-12 w-full" />
                  ))}
                </div>
              ) : courses.length === 0 ? (
                <div className="text-center py-8">
                  <BookOpen className="h-10 w-10 text-muted-foreground/50 mx-auto mb-3" />
                  <p className="text-muted-foreground">No courses yet</p>
                  <Button size="sm" className="mt-3" onClick={handleCreateCourse}>
                    <Plus className="h-4 w-4 mr-1" /> Create Course
                  </Button>
                </div>
              ) : (
                <div className="space-y-3">
                  {courses.slice(0, 5).map((course) => (
                    <div key={course.courseId} className="flex items-center justify-between p-3 rounded-lg border">
                      <div className="flex items-center gap-3">
                        <BookOpen className="h-4 w-4 text-muted-foreground" />
                        <div>
                          <p className="font-medium text-sm">{course.courseTitle}</p>
                          <p className="text-xs text-muted-foreground">
                            Duration: {formatDuration(course.courseDuration)}
                          </p>
                        </div>
                      </div>
                      <div className="flex items-center gap-2">
                        <Button variant="ghost" size="sm" onClick={() => handleEditCourse(course)}>
                          <Pencil className="h-4 w-4" />
                        </Button>
                        <Button variant="ghost" size="sm" className="text-destructive" onClick={() => setDeleteTarget(course)}>
                          <Trash2 className="h-4 w-4" />
                        </Button>
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
          <div className="flex items-center justify-between">
            <p className="text-sm text-muted-foreground">
              {courses.length} course{courses.length !== 1 ? 's' : ''}
            </p>
            <div className="flex gap-2">
              <Button variant="ghost" size="sm" onClick={fetchCourses} disabled={coursesLoading}>
                <RefreshCw className={`h-4 w-4 mr-1 ${coursesLoading ? 'animate-spin' : ''}`} />
                Refresh
              </Button>
              <Button size="sm" onClick={handleCreateCourse}>
                <Plus className="h-4 w-4 mr-1" />
                Create Course
              </Button>
            </div>
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
                  <h3 className="text-lg font-medium">No courses yet</h3>
                  <p className="text-sm text-muted-foreground mt-1">Create your first course to get started</p>
                  <Button className="mt-4" onClick={handleCreateCourse}>
                    <Plus className="h-4 w-4 mr-2" /> Create Course
                  </Button>
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
                          <TableHead>Updated</TableHead>
                          <TableHead className="text-right">Actions</TableHead>
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
                            <TableCell>
                              <span className="text-muted-foreground text-sm flex items-center gap-1">
                                <Calendar className="h-3 w-3" />
                                {formatDate(course.updatedAt)}
                              </span>
                            </TableCell>
                            <TableCell className="text-right">
                              <div className="flex justify-end gap-1">
                                <Button variant="ghost" size="sm" onClick={() => handleEditCourse(course)}>
                                  <Pencil className="h-4 w-4" />
                                </Button>
                                <Button variant="ghost" size="sm" className="text-destructive hover:text-destructive" onClick={() => setDeleteTarget(course)}>
                                  <Trash2 className="h-4 w-4" />
                                </Button>
                              </div>
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
                            <div className="flex gap-1">
                              <Button variant="ghost" size="sm" onClick={() => handleEditCourse(course)}>
                                <Pencil className="h-4 w-4" />
                              </Button>
                              <Button variant="ghost" size="sm" className="text-destructive" onClick={() => setDeleteTarget(course)}>
                                <Trash2 className="h-4 w-4" />
                              </Button>
                            </div>
                          </div>
                          <div className="flex flex-wrap gap-2 text-sm text-muted-foreground">
                            <span className="flex items-center gap-1"><User className="h-3 w-3" />{course.instructorName}</span>
                            <Badge variant="outline" className="gap-1 text-xs"><Clock className="h-3 w-3" />{formatDuration(course.courseDuration)}</Badge>
                          </div>
                          <div className="flex gap-3 text-xs text-muted-foreground">
                            <span>Created: {formatDate(course.createdAt)}</span>
                            <span>Updated: {formatDate(course.updatedAt)}</span>
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
                  <h3 className="text-lg font-medium">No enrollments</h3>
                  <p className="text-sm text-muted-foreground mt-1">Students haven&apos;t enrolled in your courses yet</p>
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
                              <Badge variant={getStatusBadgeVariant(enrollment.status)}>{enrollment.status}</Badge>
                            </TableCell>
                            <TableCell>
                              <div className="flex items-center gap-2 min-w-[120px]">
                                <Progress value={enrollment.progress || 0} className="h-2 flex-1" />
                                <span className="text-xs text-muted-foreground w-8 text-right">{enrollment.progress || 0}%</span>
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
                        <div className="space-y-2">
                          <div className="flex items-start justify-between">
                            <h3 className="font-medium flex items-center gap-2">
                              <BookOpen className="h-4 w-4 text-muted-foreground" />
                              {enrollment.courseName}
                            </h3>
                            <Badge variant={getStatusBadgeVariant(enrollment.status)}>{enrollment.status}</Badge>
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

      {/* Course Form Dialog */}
      <CourseForm
        open={courseFormOpen}
        onOpenChange={setCourseFormOpen}
        course={editingCourse}
        onSuccess={fetchCourses}
      />

      {/* Delete Confirmation */}
      <AlertDialog open={!!deleteTarget} onOpenChange={(open) => !open && setDeleteTarget(null)}>
        <AlertDialogContent>
          <AlertDialogHeader>
            <AlertDialogTitle>Delete Course</AlertDialogTitle>
            <AlertDialogDescription>
              Are you sure you want to delete &quot;{deleteTarget?.courseTitle}&quot;? This action cannot be undone.
            </AlertDialogDescription>
          </AlertDialogHeader>
          <AlertDialogFooter>
            <AlertDialogCancel disabled={deleting}>Cancel</AlertDialogCancel>
            <AlertDialogAction
              onClick={handleDeleteCourse}
              disabled={deleting}
              className="bg-destructive text-destructive-foreground hover:bg-destructive/90"
            >
              {deleting ? (
                <>
                  <Loader2 className="mr-2 h-4 w-4 animate-spin" />
                  Deleting...
                </>
              ) : (
                'Delete'
              )}
            </AlertDialogAction>
          </AlertDialogFooter>
        </AlertDialogContent>
      </AlertDialog>
    </div>
  )
}
