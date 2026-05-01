'use client'

import { useState, useEffect } from 'react'
import { instructorApi, type CourseResponse, type CreateCourseRequest, type UpdateCourseRequest, ApiError } from '@/lib/api'
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
import { Loader2 } from 'lucide-react'

interface CourseFormProps {
  open: boolean
  onOpenChange: (open: boolean) => void
  course?: CourseResponse | null
  onSuccess: () => void
}

export function CourseForm({ open, onOpenChange, course, onSuccess }: CourseFormProps) {
  const { user } = useAuthStore()
  const { toast } = useToast()
  const [courseName, setCourseName] = useState('')
  const [instructorName, setInstructorName] = useState('')
  const [courseDuration, setCourseDuration] = useState('')
  const [isLoading, setIsLoading] = useState(false)
  const [errors, setErrors] = useState<{ courseName?: string; instructorName?: string; courseDuration?: string }>({})

  const isEditing = !!course

  useEffect(() => {
    if (course) {
      setCourseName(course.courseTitle || '')
      setInstructorName(course.instructorName || '')
      setCourseDuration(course.courseDuration || '')
    } else {
      setCourseName('')
      setInstructorName(user?.fullName || '')
      setCourseDuration('')
    }
  }, [course, user?.fullName, open])

  const validate = () => {
    const newErrors: typeof errors = {}
    if (!courseName.trim()) newErrors.courseName = 'Course name is required'
    if (!instructorName.trim()) newErrors.instructorName = 'Instructor name is required'
    if (!courseDuration.trim()) newErrors.courseDuration = 'Duration is required'
    setErrors(newErrors)
    return Object.keys(newErrors).length === 0
  }

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    if (!validate() || !user?.token) return

    setIsLoading(true)
    try {
      if (isEditing && course) {
        const data: UpdateCourseRequest = {
          courseName,
          instructorName,
          courseDuration,
        }
        await instructorApi.updateCourse(course.courseId, data, user.token)
        toast({
          title: 'Course Updated',
          description: `"${courseName}" has been updated successfully.`,
        })
      } else {
        const data: CreateCourseRequest = {
          courseName,
          instructorName,
          courseDuration,
        }
        await instructorApi.createCourse(data, user.token)
        toast({
          title: 'Course Created',
          description: `"${courseName}" has been created successfully.`,
        })
      }
      onOpenChange(false)
      onSuccess()
    } catch (error) {
      const message = error instanceof ApiError ? error.message : 'Operation failed. Please try again.'
      toast({
        title: isEditing ? 'Update Failed' : 'Create Failed',
        description: message,
        variant: 'destructive',
      })
    } finally {
      setIsLoading(false)
    }
  }

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent className="sm:max-w-[480px]">
        <DialogHeader>
          <DialogTitle>{isEditing ? 'Edit Course' : 'Create New Course'}</DialogTitle>
          <DialogDescription>
            {isEditing
              ? 'Update the course details below.'
              : 'Fill in the details to create a new course.'}
          </DialogDescription>
        </DialogHeader>
        <form onSubmit={handleSubmit}>
          <div className="space-y-4 py-4">
            <div className="space-y-2">
              <Label htmlFor="course-name">Course Name</Label>
              <Input
                id="course-name"
                placeholder="e.g., Introduction to Computer Science"
                value={courseName}
                onChange={(e) => setCourseName(e.target.value)}
                disabled={isLoading}
              />
              {errors.courseName && (
                <p className="text-sm text-destructive">{errors.courseName}</p>
              )}
            </div>
            <div className="space-y-2">
              <Label htmlFor="instructor-name">Instructor Name</Label>
              <Input
                id="instructor-name"
                placeholder="e.g., Dr. Smith"
                value={instructorName}
                onChange={(e) => setInstructorName(e.target.value)}
                disabled={isLoading}
              />
              {errors.instructorName && (
                <p className="text-sm text-destructive">{errors.instructorName}</p>
              )}
            </div>
            <div className="space-y-2">
              <Label htmlFor="course-duration">Course Duration</Label>
              <Input
                id="course-duration"
                placeholder="e.g., 12:00:00"
                value={courseDuration}
                onChange={(e) => setCourseDuration(e.target.value)}
                disabled={isLoading}
              />
              {errors.courseDuration && (
                <p className="text-sm text-destructive">{errors.courseDuration}</p>
              )}
              <p className="text-xs text-muted-foreground">
                Enter duration in HH:mm:ss format
              </p>
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
                  {isEditing ? 'Updating...' : 'Creating...'}
                </>
              ) : isEditing ? (
                'Update Course'
              ) : (
                'Create Course'
              )}
            </Button>
          </DialogFooter>
        </form>
      </DialogContent>
    </Dialog>
  )
}
