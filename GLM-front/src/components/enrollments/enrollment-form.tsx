'use client'

import { useState, useEffect } from 'react'
import { enrollmentApi, type CourseResponse, type CreateEnrollmentRequest, ApiError } from '@/lib/api'
import { useAuthStore } from '@/lib/auth-store'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
} from '@/components/ui/dialog'
import { useToast } from '@/hooks/use-toast'
import { Loader2, BookOpen } from 'lucide-react'

interface EnrollmentFormProps {
  open: boolean
  onOpenChange: (open: boolean) => void
  course: CourseResponse | null
  onSuccess: () => void
}

export function EnrollmentForm({ open, onOpenChange, course, onSuccess }: EnrollmentFormProps) {
  const { user } = useAuthStore()
  const { toast } = useToast()
  const [studentName, setStudentName] = useState('')
  const [isLoading, setIsLoading] = useState(false)
  const [errors, setErrors] = useState<{ studentName?: string }>({})

  useEffect(() => {
    if (open && user) {
      setStudentName(user.fullName || '')
    }
  }, [open, user])

  const validate = () => {
    const newErrors: typeof errors = {}
    if (!studentName.trim()) newErrors.studentName = 'Student name is required'
    setErrors(newErrors)
    return Object.keys(newErrors).length === 0
  }

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    if (!validate() || !user?.token || !course) return

    setIsLoading(true)
    try {
      const data: CreateEnrollmentRequest = {
        courseName: course.courseTitle,
        studentName,
        studentId: user.logInId,
        courseID: course.courseId,
      }
      await enrollmentApi.createEnrollment(data, user.token)
      toast({
        title: 'Enrolled Successfully',
        description: `You have been enrolled in "${course.courseTitle}".`,
      })
      onOpenChange(false)
      onSuccess()
    } catch (error) {
      const message = error instanceof ApiError ? error.message : 'Enrollment failed. Please try again.'
      toast({
        title: 'Enrollment Failed',
        description: message,
        variant: 'destructive',
      })
    } finally {
      setIsLoading(false)
    }
  }

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent className="sm:max-w-[420px]">
        <DialogHeader>
          <DialogTitle>Enroll in Course</DialogTitle>
          <DialogDescription>
            Confirm your enrollment in the selected course.
          </DialogDescription>
        </DialogHeader>
        <form onSubmit={handleSubmit}>
          <div className="space-y-4 py-4">
            {course && (
              <div className="rounded-lg border p-4 bg-muted/50">
                <div className="flex items-center gap-2 mb-1">
                  <BookOpen className="h-4 w-4 text-muted-foreground" />
                  <span className="font-medium">{course.courseTitle}</span>
                </div>
                <p className="text-sm text-muted-foreground">
                  Instructor: {course.instructorName}
                </p>
              </div>
            )}
            <div className="space-y-2">
              <Label htmlFor="student-name">Student Name</Label>
              <Input
                id="student-name"
                placeholder="Enter your name"
                value={studentName}
                onChange={(e) => setStudentName(e.target.value)}
                disabled={isLoading}
              />
              {errors.studentName && (
                <p className="text-sm text-destructive">{errors.studentName}</p>
              )}
            </div>
          </div>
          <DialogFooter>
            <Button
              type="button"
              variant="outline"
              onClick={() => onOpenChange(false)}
              disabled={isLoading}
            >
              Cancel
            </Button>
            <Button type="submit" disabled={isLoading}>
              {isLoading ? (
                <>
                  <Loader2 className="mr-2 h-4 w-4 animate-spin" />
                  Enrolling...
                </>
              ) : (
                'Confirm Enrollment'
              )}
            </Button>
          </DialogFooter>
        </form>
      </DialogContent>
    </Dialog>
  )
}
