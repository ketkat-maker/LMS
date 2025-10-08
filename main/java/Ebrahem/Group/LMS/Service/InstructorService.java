package Ebrahem.Group.LMS.Service;

import Ebrahem.Group.LMS.Model.Dtos.CourseRequest;
import Ebrahem.Group.LMS.Model.Dtos.CourseResponse;
import Ebrahem.Group.LMS.Model.Entity.Course;

import java.util.UUID;
public interface InstructorService {
     CourseResponse CreateCourseResponse(CourseRequest course);
    CourseResponse deleteCourse(UUID courseId);
    CourseResponse updateCourse(UUID courseId, CourseRequest course);
}
