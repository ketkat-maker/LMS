package Ebrahem.Group.LMS.Service;

import Ebrahem.Group.LMS.Model.Dtos.CourseRequest;
import Ebrahem.Group.LMS.Model.Dtos.CourseResponse;


public interface CourseService {
     CourseResponse CreateCourseResponse(CourseRequest coursez);
}
