package Ebrahem.Group.LMS.Service.Impl;

import Ebrahem.Group.LMS.Model.Dtos.CourseResponse;
import Ebrahem.Group.LMS.Model.Entity.Course;
import Ebrahem.Group.LMS.Repositories.CourseRepository;
import Ebrahem.Group.LMS.Service.StudentService;
import Ebrahem.Group.LMS.Util.Utility;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StudentServiceImpl implements StudentService {
    private final CourseRepository courseRepository;
    private final Utility utility;
    public List<CourseResponse> getAllCourses() {
        List<Course> courses = returnAllCourses();

                return courses.stream().map(course->
                       new CourseResponse(
                               course.getInstructor().getUserId(),
                               course.getCourseId(),
                               course.getCourseTitle(),
                               course.getInstructor().getUserName(),
                               course.getCreatedAt(),
                               course.getUpdatedAt(),
                                utility.formatDuration(course.getCourseDuration()))
                       ).toList();
    }

    private List<Course> returnAllCourses() {
        return courseRepository.findAll();
    }
}
