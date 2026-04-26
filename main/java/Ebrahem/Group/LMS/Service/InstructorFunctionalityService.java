package Ebrahem.Group.LMS.Service;

import Ebrahem.Group.LMS.Model.Dtos.CourseFilter;
import Ebrahem.Group.LMS.Model.Dtos.CourseRequest;
import Ebrahem.Group.LMS.Model.Dtos.CourseResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface InstructorFunctionalityService {
    CourseResponse CreateCourse(CourseRequest course);

    CourseResponse deleteCourse(UUID courseId);

    CourseResponse updateCourse(UUID courseId, CourseRequest course);


}
