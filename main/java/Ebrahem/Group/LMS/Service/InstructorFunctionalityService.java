package Ebrahem.Group.LMS.Service;

import Ebrahem.Group.LMS.Model.Dtos.CourseRequest;
import Ebrahem.Group.LMS.Model.Dtos.CourseResponse;

public interface InstructorFunctionalityService {
    CourseResponse CreateCourse(CourseRequest course);

    CourseResponse deleteCourse(String courseId);

    CourseResponse updateCourse(String courseId, CourseRequest course);


}
