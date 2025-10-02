package Ebrahem.Group.LMS.Service.Impl;

import Ebrahem.Group.LMS.Model.Dtos.CourseRequest;
import Ebrahem.Group.LMS.Model.Dtos.CourseResponse;
import Ebrahem.Group.LMS.Model.Entity.Course;
import Ebrahem.Group.LMS.Model.Entity.User;
import Ebrahem.Group.LMS.Repositories.CourseRepository;
import Ebrahem.Group.LMS.Repositories.UserRepository;
import Ebrahem.Group.LMS.Service.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import static Ebrahem.Group.LMS.Model.Enums.Role.INSTRUCTOR;

@Service
@RequiredArgsConstructor
public class CourseServiceImpl implements CourseService {
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    @PreAuthorize(value = "hasRole('INSTRUCTOR')")
    @Override
    public CourseResponse CreateCourseResponse(CourseRequest course) {
        Course created = createCourse(course);

        return new CourseResponse(
                created.getCourseId(),
                created.getCourseTitle(),
                created.getInstructor().getUserName(),
                created.getInstructor().getUserId()
        );
    }
    private Course createCourse(CourseRequest course) {
        User instructor = userRepository.findByUserName(course.instructorName())
                 .orElseThrow(() -> new IllegalArgumentException("Instructor not found"));

         if (instructor.getRole() != INSTRUCTOR) {
             throw new IllegalStateException("User is not an instructor");
         }

         Course  created = new Course(
                 course.courseName(),
                 course.courseDuration(),
                 instructor
         );
         return courseRepository.save(created);
    }

}
