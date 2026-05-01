'use client'

import { useState, useEffect, useCallback } from 'react'
import { adminApi, studentApi, type UserResponse, type Role, type PageResponse, type CourseResponse, formatDate, formatDuration, ApiError } from '@/lib/api'
import { useAuthStore } from '@/lib/auth-store'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import { Button } from '@/components/ui/button'
import { Badge } from '@/components/ui/badge'
import { Skeleton } from '@/components/ui/skeleton'
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs'
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select'
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
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from '@/components/ui/table'
import { Input } from '@/components/ui/input'
import { useToast } from '@/hooks/use-toast'
import {
  Users,
  Trash2,
  RefreshCw,
  UserCog,
  Mail,
  Calendar,
  Shield,
  BookOpen,
  LayoutDashboard,
  GraduationCap,
  User,
  Loader2,
  Search,
  Clock,
  ChevronLeft,
  ChevronRight,
} from 'lucide-react'

function getRoleBadgeVariant(role: Role) {
  switch (role) {
    case 'ADMIN': return 'destructive' as const
    case 'INSTRUCTOR': return 'default' as const
    case 'STUDENT': return 'secondary' as const
    default: return 'outline' as const
  }
}

interface AdminDashboardProps {
  activeView: string
}

export function AdminDashboard({ activeView }: AdminDashboardProps) {
  const { user, logout } = useAuthStore()
  const { toast } = useToast()
  const [currentTab, setCurrentTab] = useState<string>(
    activeView === 'manage-users' ? 'users' : activeView === 'courses' ? 'courses' : 'overview'
  )

  // Users state
  const [users, setUsers] = useState<UserResponse[]>([])
  const [usersLoading, setUsersLoading] = useState(false)
  const [roleFilter, setRoleFilter] = useState<string>('ALL')
  const [deleteTarget, setDeleteTarget] = useState<UserResponse | null>(null)
  const [deleting, setDeleting] = useState(false)

  // Stats state
  const [totalCourses, setTotalCourses] = useState(0)
  const [statsLoading, setStatsLoading] = useState(true)

  const fetchUsers = useCallback(async () => {
    if (!user?.token) return
    setUsersLoading(true)
    try {
      const roles: Role[] = roleFilter === 'ALL'
        ? ['INSTRUCTOR', 'STUDENT', 'ADMIN']
        : [roleFilter as Role]
      const data = await adminApi.getUsers(roles, user.token)
      setUsers(Array.isArray(data) ? data : [])
    } catch (error) {
      if (error instanceof ApiError && error.status === 401) {
        logout()
      } else {
        toast({ title: 'Error', description: 'Failed to fetch users', variant: 'destructive' })
      }
    } finally {
      setUsersLoading(false)
    }
  }, [user?.token, roleFilter, logout, toast])

  const fetchStats = useCallback(async () => {
    if (!user?.token) return
    setStatsLoading(true)
    try {
      const data = await studentApi.getCourses({ page: 0, size: 1 }, user.token)
      setTotalCourses(data.totalElements || 0)
    } catch {
      setTotalCourses(0)
    } finally {
      setStatsLoading(false)
    }
  }, [user?.token])

  useEffect(() => {
    if (user?.token) {
      fetchUsers()
      fetchStats()
    }
  }, [user?.token, fetchUsers, fetchStats])

  useEffect(() => {
    if (activeView === 'manage-users') setCurrentTab('users')
    else if (activeView === 'courses') setCurrentTab('courses')
    else setCurrentTab('overview')
  }, [activeView])

  const handleDeleteUser = async () => {
    if (!deleteTarget || !user?.token) return
    setDeleting(true)
    try {
      await adminApi.deleteUser(deleteTarget.UserId, user.token)
      toast({
        title: 'User Deleted',
        description: `User "${deleteTarget.UserName}" has been deleted.`,
      })
      setDeleteTarget(null)
      fetchUsers()
    } catch (error) {
      const message = error instanceof ApiError ? error.message : 'Delete failed.'
      toast({ title: 'Delete Failed', description: message, variant: 'destructive' })
    } finally {
      setDeleting(false)
    }
  }

  const studentCount = users.filter((u) => u.role === 'STUDENT').length
  const instructorCount = users.filter((u) => u.role === 'INSTRUCTOR').length
  const adminCount = users.filter((u) => u.role === 'ADMIN').length

  return (
    <div className="p-4 md:p-6 space-y-6 max-w-7xl mx-auto">
      {/* Welcome header */}
      <div>
        <h1 className="text-2xl md:text-3xl font-bold">Admin Dashboard</h1>
        <p className="text-muted-foreground mt-1">System administration overview, {user?.fullName}</p>
      </div>

      <Tabs value={currentTab} onValueChange={setCurrentTab}>
        <TabsList className="grid w-full grid-cols-3">
          <TabsTrigger value="overview" className="gap-2">
            <LayoutDashboard className="h-4 w-4 hidden sm:block" />
            Overview
          </TabsTrigger>
          <TabsTrigger value="users" className="gap-2">
            <UserCog className="h-4 w-4 hidden sm:block" />
            Users
          </TabsTrigger>
          <TabsTrigger value="courses" className="gap-2">
            <BookOpen className="h-4 w-4 hidden sm:block" />
            Courses
          </TabsTrigger>
        </TabsList>

        {/* Overview Tab */}
        <TabsContent value="overview" className="space-y-6 mt-6">
          <div className="grid grid-cols-2 sm:grid-cols-4 gap-4">
            <Card>
              <CardContent className="p-6">
                <div className="flex items-center gap-3">
                  <div className="rounded-lg bg-primary/10 p-2.5">
                    <Users className="h-5 w-5 text-primary" />
                  </div>
                  <div>
                    <p className="text-xs text-muted-foreground">Total Users</p>
                    <p className="text-xl font-bold">{users.length}</p>
                  </div>
                </div>
              </CardContent>
            </Card>
            <Card>
              <CardContent className="p-6">
                <div className="flex items-center gap-3">
                  <div className="rounded-lg bg-green-500/10 p-2.5">
                    <GraduationCap className="h-5 w-5 text-green-600" />
                  </div>
                  <div>
                    <p className="text-xs text-muted-foreground">Students</p>
                    <p className="text-xl font-bold">{studentCount}</p>
                  </div>
                </div>
              </CardContent>
            </Card>
            <Card>
              <CardContent className="p-6">
                <div className="flex items-center gap-3">
                  <div className="rounded-lg bg-yellow-500/10 p-2.5">
                    <User className="h-5 w-5 text-yellow-600" />
                  </div>
                  <div>
                    <p className="text-xs text-muted-foreground">Instructors</p>
                    <p className="text-xl font-bold">{instructorCount}</p>
                  </div>
                </div>
              </CardContent>
            </Card>
            <Card>
              <CardContent className="p-6">
                <div className="flex items-center gap-3">
                  <div className="rounded-lg bg-primary/10 p-2.5">
                    <BookOpen className="h-5 w-5 text-primary" />
                  </div>
                  <div>
                    <p className="text-xs text-muted-foreground">Courses</p>
                    <p className="text-xl font-bold">{totalCourses}</p>
                  </div>
                </div>
              </CardContent>
            </Card>
          </div>

          {/* Quick user list */}
          <Card>
            <CardHeader className="pb-3">
              <div className="flex items-center justify-between">
                <CardTitle className="text-lg flex items-center gap-2">
                  <Users className="h-5 w-5" />
                  Recent Users
                </CardTitle>
                <Button variant="outline" size="sm" onClick={() => setCurrentTab('users')}>
                  Manage Users
                </Button>
              </div>
            </CardHeader>
            <CardContent>
              {usersLoading ? (
                <div className="space-y-3">
                  {Array.from({ length: 3 }).map((_, i) => (
                    <Skeleton key={i} className="h-12 w-full" />
                  ))}
                </div>
              ) : users.length === 0 ? (
                <div className="text-center py-8">
                  <Users className="h-10 w-10 text-muted-foreground/50 mx-auto mb-3" />
                  <p className="text-muted-foreground">No users found</p>
                </div>
              ) : (
                <div className="space-y-3">
                  {users.slice(0, 5).map((u) => (
                    <div key={u.UserId} className="flex items-center justify-between p-3 rounded-lg border">
                      <div className="flex items-center gap-3">
                        <div className="rounded-full bg-primary/10 p-2">
                          <User className="h-4 w-4 text-primary" />
                        </div>
                        <div>
                          <p className="font-medium text-sm">{u.UserName}</p>
                          <p className="text-xs text-muted-foreground">{u.UserEmail}</p>
                        </div>
                      </div>
                      <Badge variant={getRoleBadgeVariant(u.role)}>{u.role}</Badge>
                    </div>
                  ))}
                </div>
              )}
            </CardContent>
          </Card>
        </TabsContent>

        {/* Users Tab */}
        <TabsContent value="users" className="space-y-4 mt-6">
          {/* Filter */}
          <Card>
            <CardContent className="p-4">
              <div className="flex flex-col sm:flex-row gap-3 items-center">
                <div className="flex items-center gap-2 flex-1">
                  <Shield className="h-4 w-4 text-muted-foreground" />
                  <span className="text-sm font-medium">Filter by role:</span>
                  <Select value={roleFilter} onValueChange={setRoleFilter}>
                    <SelectTrigger className="w-[160px]">
                      <SelectValue placeholder="All roles" />
                    </SelectTrigger>
                    <SelectContent>
                      <SelectItem value="ALL">All Roles</SelectItem>
                      <SelectItem value="STUDENT">Student</SelectItem>
                      <SelectItem value="INSTRUCTOR">Instructor</SelectItem>
                      <SelectItem value="ADMIN">Admin</SelectItem>
                    </SelectContent>
                  </Select>
                </div>
                <Button variant="ghost" size="sm" onClick={fetchUsers} disabled={usersLoading}>
                  <RefreshCw className={`h-4 w-4 mr-1 ${usersLoading ? 'animate-spin' : ''}`} />
                  Refresh
                </Button>
              </div>
            </CardContent>
          </Card>

          <div className="flex items-center justify-between">
            <p className="text-sm text-muted-foreground">
              {users.length} user{users.length !== 1 ? 's' : ''} found
            </p>
          </div>

          <Card>
            <CardContent className="p-0">
              {usersLoading ? (
                <div className="p-6 space-y-3">
                  {Array.from({ length: 5 }).map((_, i) => (
                    <Skeleton key={i} className="h-12 w-full" />
                  ))}
                </div>
              ) : users.length === 0 ? (
                <div className="flex flex-col items-center justify-center py-12">
                  <Users className="h-12 w-12 text-muted-foreground/50 mb-4" />
                  <h3 className="text-lg font-medium">No users found</h3>
                  <p className="text-sm text-muted-foreground mt-1">No users match the selected filter</p>
                </div>
              ) : (
                <>
                  <div className="hidden md:block">
                    <Table>
                      <TableHeader>
                        <TableRow>
                          <TableHead>ID</TableHead>
                          <TableHead>Name</TableHead>
                          <TableHead>Email</TableHead>
                          <TableHead>Role</TableHead>
                          <TableHead>Created</TableHead>
                          <TableHead className="text-right">Actions</TableHead>
                        </TableRow>
                      </TableHeader>
                      <TableBody>
                        {users.map((u) => (
                          <TableRow key={u.UserId}>
                            <TableCell className="font-mono text-sm">{u.UserId}</TableCell>
                            <TableCell className="font-medium">
                              <div className="flex items-center gap-2">
                                <UserCog className="h-4 w-4 text-muted-foreground" />
                                {u.UserName}
                              </div>
                            </TableCell>
                            <TableCell>
                              <div className="flex items-center gap-1">
                                <Mail className="h-3 w-3 text-muted-foreground" />
                                {u.UserEmail}
                              </div>
                            </TableCell>
                            <TableCell>
                              <Badge variant={getRoleBadgeVariant(u.role)}>{u.role}</Badge>
                            </TableCell>
                            <TableCell>
                              <span className="text-muted-foreground text-sm flex items-center gap-1">
                                <Calendar className="h-3 w-3" />
                                {formatDate(u.CreatedAt)}
                              </span>
                            </TableCell>
                            <TableCell className="text-right">
                              <Button
                                variant="ghost"
                                size="sm"
                                onClick={() => setDeleteTarget(u)}
                                className="text-destructive hover:text-destructive"
                                disabled={u.UserId === user?.logInId}
                              >
                                <Trash2 className="h-4 w-4 mr-1" />
                                Delete
                              </Button>
                            </TableCell>
                          </TableRow>
                        ))}
                      </TableBody>
                    </Table>
                  </div>
                  <div className="md:hidden space-y-3 p-4">
                    {users.map((u) => (
                      <Card key={u.UserId} className="p-4">
                        <div className="space-y-2">
                          <div className="flex items-start justify-between">
                            <div>
                              <h3 className="font-medium flex items-center gap-2">
                                <UserCog className="h-4 w-4 text-muted-foreground" />
                                {u.UserName}
                              </h3>
                              <p className="text-sm text-muted-foreground flex items-center gap-1 mt-1">
                                <Mail className="h-3 w-3" />
                                {u.UserEmail}
                              </p>
                            </div>
                            <Badge variant={getRoleBadgeVariant(u.role)}>{u.role}</Badge>
                          </div>
                          <div className="flex items-center justify-between">
                            <span className="text-xs text-muted-foreground flex items-center gap-1">
                              <Calendar className="h-3 w-3" />
                              {formatDate(u.CreatedAt)}
                            </span>
                            <Button
                              variant="ghost"
                              size="sm"
                              onClick={() => setDeleteTarget(u)}
                              className="text-destructive hover:text-destructive h-8"
                              disabled={u.UserId === user?.logInId}
                            >
                              <Trash2 className="h-4 w-4 mr-1" />
                              Delete
                            </Button>
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

        {/* Courses Tab */}
        <TabsContent value="courses" className="mt-6">
          <AdminCourseViewer />
        </TabsContent>
      </Tabs>

      {/* Delete Confirmation */}
      <AlertDialog open={!!deleteTarget} onOpenChange={(open) => !open && setDeleteTarget(null)}>
        <AlertDialogContent>
          <AlertDialogHeader>
            <AlertDialogTitle>Delete User</AlertDialogTitle>
            <AlertDialogDescription>
              Are you sure you want to delete user &quot;{deleteTarget?.UserName}&quot;? This action cannot be undone.
            </AlertDialogDescription>
          </AlertDialogHeader>
          <AlertDialogFooter>
            <AlertDialogCancel disabled={deleting}>Cancel</AlertDialogCancel>
            <AlertDialogAction
              onClick={handleDeleteUser}
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

// Admin course viewer component (read-only for admins)
function AdminCourseViewer() {
  const { user, logout } = useAuthStore()
  const { toast } = useToast()
  const [courses, setCourses] = useState<CourseResponse[]>([])
  const [loading, setLoading] = useState(true)
  const [searchTitle, setSearchTitle] = useState('')
  const [page, setPage] = useState(0)
  const [totalPages, setTotalPages] = useState(0)
  const [totalElements, setTotalElements] = useState(0)

  const fetchCourses = useCallback(async () => {
    if (!user?.token) return
    setLoading(true)
    try {
      const data = await studentApi.getCourses({
        courseTitle: searchTitle || undefined,
        page,
        size: 10,
        sort: 'createdAt,desc',
      }, user.token)
      setCourses(data.content || [])
      setTotalPages(data.totalPages || 0)
      setTotalElements(data.totalElements || 0)
    } catch (error) {
      if (error instanceof ApiError && error.status === 401) {
        logout()
      } else {
        toast({ title: 'Error', description: 'Failed to fetch courses', variant: 'destructive' })
      }
    } finally {
      setLoading(false)
    }
  }, [user?.token, searchTitle, page, logout, toast])

  useEffect(() => {
    fetchCourses()
  }, [fetchCourses])

  return (
    <div className="space-y-4">
      <Card>
        <CardContent className="p-4">
          <div className="flex flex-col sm:flex-row gap-3">
            <div className="relative flex-1">
              <Search className="absolute left-3 top-3 h-4 w-4 text-muted-foreground" />
              <Input
                placeholder="Search courses..."
                value={searchTitle}
                onChange={(e) => setSearchTitle(e.target.value)}
                onKeyDown={(e) => e.key === 'Enter' && fetchCourses()}
                className="pl-10"
              />
            </div>
            <div className="flex gap-2">
              <Button onClick={() => { setPage(0); fetchCourses() }}>
                <Search className="h-4 w-4 mr-2" /> Search
              </Button>
              <Button variant="outline" onClick={() => { setSearchTitle(''); setPage(0) }}>
                Clear
              </Button>
            </div>
          </div>
        </CardContent>
      </Card>

      <p className="text-sm text-muted-foreground">{totalElements} course{totalElements !== 1 ? 's' : ''}</p>

      <Card>
        <CardContent className="p-0">
          {loading ? (
            <div className="p-6 space-y-3">
              {Array.from({ length: 5 }).map((_, i) => (
                <Skeleton key={i} className="h-12 w-full" />
              ))}
            </div>
          ) : courses.length === 0 ? (
            <div className="flex flex-col items-center justify-center py-12">
              <BookOpen className="h-12 w-12 text-muted-foreground/50 mb-4" />
              <h3 className="text-lg font-medium">No courses found</h3>
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
                      </TableRow>
                    ))}
                  </TableBody>
                </Table>
              </div>
              <div className="md:hidden space-y-3 p-4">
                {courses.map((course) => (
                  <Card key={course.courseId} className="p-4">
                    <div className="space-y-2">
                      <h3 className="font-medium flex items-center gap-2">
                        <BookOpen className="h-4 w-4 text-muted-foreground" />
                        {course.courseTitle}
                      </h3>
                      <div className="flex flex-wrap gap-2 text-sm text-muted-foreground">
                        <span className="flex items-center gap-1"><User className="h-3 w-3" />{course.instructorName}</span>
                        <Badge variant="outline" className="gap-1 text-xs"><Clock className="h-3 w-3" />{formatDuration(course.courseDuration)}</Badge>
                      </div>
                    </div>
                  </Card>
                ))}
              </div>
            </>
          )}
        </CardContent>
      </Card>

      {totalPages > 1 && (
        <div className="flex items-center justify-between">
          <p className="text-sm text-muted-foreground">Page {page + 1} of {totalPages}</p>
          <div className="flex gap-2">
            <Button variant="outline" size="sm" onClick={() => setPage((p) => Math.max(0, p - 1))} disabled={page === 0 || loading}>
              <ChevronLeft className="h-4 w-4 mr-1" /> Previous
            </Button>
            <Button variant="outline" size="sm" onClick={() => setPage((p) => Math.min(totalPages - 1, p + 1))} disabled={page >= totalPages - 1 || loading}>
              Next <ChevronRight className="h-4 w-4 ml-1" />
            </Button>
          </div>
        </div>
      )}
    </div>
  )
}
