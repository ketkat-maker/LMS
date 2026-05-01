import React, { useEffect, useState } from "react";
import { studentApi } from "@/api/studentApi";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";

export default function InstructorDashboard() {
  const [courses, setCourses] = useState<any[]>([]);
  const [loading, setLoading] = useState(true);

  const fetchCourses = async () => {
    try {
      const response = await studentApi.getAllCourses();
      console.log("API Response:", response); // عشان تتأكد من شكل البيانات
      setCourses(response?.data?.allCourses || []); // استخدم المفتاح الصحيح حسب الـbackend
    } catch (error) {
      console.error("Error fetching courses:", error);
      setCourses([]);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchCourses();
  }, []);

  if (loading) {
    return <p className="text-center mt-10">Loading courses...</p>;
  }

  return (
    <div className="p-6 grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4">
      {Array.isArray(courses) && courses.length > 0 ? (
        courses.map((course, index) => (
          <Card key={index} className="shadow-md hover:shadow-lg transition">
            <CardHeader>
              <CardTitle>{course.courseName}</CardTitle>
            </CardHeader>
            <CardContent>
              <p>{course.courseDescription}</p>
            </CardContent>
          </Card>
        ))
      ) : (
        <p className="text-center text-muted-foreground col-span-full">
          No courses found.
        </p>
      )}
    </div>
  );
}
