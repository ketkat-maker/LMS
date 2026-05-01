import { useState, useEffect } from 'react';
import { studentApi, enrollmentApi } from '@/lib/api';
import { useAuth } from '@/contexts/AuthContext';
import Navbar from '@/components/Navbar';
import { Button } from '@/components/ui/button';
import { Card, CardContent, CardDescription, CardFooter, CardHeader, CardTitle } from '@/components/ui/card';
import { Badge } from '@/components/ui/badge';
import { BookOpen, Clock, User, Loader2 } from 'lucide-react';
import { toast } from 'sonner';

interface Course {
  id: string;
  courseName: string;
  courseDescription: string;
  instructor: string;
  duration: string;
  enrolled?: boolean;
}

const StudentDashboard = () => {
  const [courses, setCourses] = useState<Course[]>([]);
  const [loading, setLoading] = useState(true);
  const [enrolling, setEnrolling] = useState<string | null>(null);
  const { user } = useAuth();

  useEffect(() => {
    fetchCourses();
  }, []);

  const fetchCourses = async () => {
    try {
      const response = await studentApi.getAllCourses();
      setCourses(response.data);
    } catch (error) {
      toast.error('Failed to load courses');
    } finally {
      setLoading(false);
    }
  };

  const handleEnroll = async (courseName: string) => {
    if (!user) return;
    
    setEnrolling(courseName);
    try {
      await enrollmentApi.enroll({
        courseName,
        studentName: `${user.firstName} ${user.lastName}`,
      });
      toast.success('Successfully enrolled!');
      fetchCourses();
    } catch (error) {
      toast.error('Enrollment failed');
    } finally {
      setEnrolling(null);
    }
  };

  return (
    <div className="min-h-screen">
      <Navbar />
      
      <main className="container mx-auto px-4 py-8">
        <div className="mb-8 animate-slide-up">
          <h1 className="mb-2 text-4xl font-bold">Available Courses</h1>
          <p className="text-muted-foreground">
            Explore and enroll in courses to enhance your skills
          </p>
        </div>

        {loading ? (
          <div className="flex h-64 items-center justify-center">
            <Loader2 className="h-8 w-8 animate-spin text-primary" />
          </div>
        ) : (
          <div className="grid gap-6 md:grid-cols-2 lg:grid-cols-3">
            {courses.map((course, index) => (
              <Card
                key={course.id}
                className="glass-card hover-lift animate-fade-in"
                style={{ animationDelay: `${index * 0.1}s` }}
              >
                <CardHeader>
                  <div className="mb-2 flex items-start justify-between">
                    <BookOpen className="h-8 w-8 text-primary" />
                    {course.enrolled && (
                      <Badge variant="secondary">Enrolled</Badge>
                    )}
                  </div>
                  <CardTitle className="text-xl">{course.courseName}</CardTitle>
                  <CardDescription>{course.courseDescription}</CardDescription>
                </CardHeader>
                
                <CardContent className="space-y-2">
                  <div className="flex items-center gap-2 text-sm text-muted-foreground">
                    <User className="h-4 w-4" />
                    <span>{course.instructor}</span>
                  </div>
                  <div className="flex items-center gap-2 text-sm text-muted-foreground">
                    <Clock className="h-4 w-4" />
                    <span>{course.duration}</span>
                  </div>
                </CardContent>
                
                <CardFooter>
                  <Button
                    className="w-full bg-gradient-primary hover:opacity-90"
                    onClick={() => handleEnroll(course.courseName)}
                    disabled={course.enrolled || enrolling === course.courseName}
                  >
                    {enrolling === course.courseName ? (
                      <>
                        <Loader2 className="mr-2 h-4 w-4 animate-spin" />
                        Enrolling...
                      </>
                    ) : course.enrolled ? (
                      'Already Enrolled'
                    ) : (
                      'Enroll Now'
                    )}
                  </Button>
                </CardFooter>
              </Card>
            ))}
          </div>
        )}
      </main>
    </div>
  );
};

export default StudentDashboard;
