package Ebrahem.Group.LMS.Service.Impl;

import Ebrahem.Group.LMS.Model.Dtos.CourseResponse;
import Ebrahem.Group.LMS.Model.Entity.Course;
import Ebrahem.Group.LMS.Repositories.CourseRepository;
import Ebrahem.Group.LMS.Service.StudentService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StudentServiceImpl implements StudentService {
    private final CourseRepository courseRepository;

    @Override
    public List<CourseResponse> getAllCourses() {
        List<Course> courses = returnAllCourses();

        return courses.stream().map(course ->
                new CourseResponse(
                        course.getInstructor().getUserId(),
                        course.getCourseId(),
                        course.getCourseTitle(),
                        course.getInstructor().getUserName(),
                        course.getCreatedAt(),
                        course.getUpdatedAt(),
                        course.getCourseDuration())
        ).toList();
    }

    @Transactional
    private List<Course> returnAllCourses() {
        return courseRepository.findAll();
    }
}
