package Ebrahem.Group.LMS.Service.Impl;

import Ebrahem.Group.LMS.Model.Dtos.CourseRequest;
import Ebrahem.Group.LMS.Model.Dtos.CourseResponse;
import Ebrahem.Group.LMS.Model.Entity.Course;
import Ebrahem.Group.LMS.Model.Entity.User;
import Ebrahem.Group.LMS.Repositories.CourseRepository;
import Ebrahem.Group.LMS.Repositories.UserRepository;
import Ebrahem.Group.LMS.Service.InstructorFunctionalityService;
import Ebrahem.Group.LMS.Service.Specification.CourseSpecification;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.UUID;

import static Ebrahem.Group.LMS.Model.Enums.Role.INSTRUCTOR;

@Service
@RequiredArgsConstructor
public class InstructorFunctionalityServiceImpl implements InstructorFunctionalityService {
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final CourseSpecification specification;

    @PreAuthorize("hasRole('INSTRUCTOR')")
    @Override
    public CourseResponse CreateCourse(CourseRequest course) {
        Course created = createCourse(course);
        return buildResponse(created);
    }

    @PreAuthorize("hasRole('INSTRUCTOR')")
    @Override
    public CourseResponse deleteCourse(UUID courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new EntityNotFoundException("Course not found by Id: " + courseId));

        courseRepository.delete(course);
        return buildResponse(course);
    }

    @PreAuthorize("hasRole('INSTRUCTOR')")
    @Override
    public CourseResponse updateCourse(UUID courseId, CourseRequest course) {
        Course courseById = courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("Course not found by this ID: " + courseId));

        courseById.setCourseTitle(course.courseName());
        courseById.setCourseDuration(course.courseDuration());

        Course updatedCourse = courseRepository.save(courseById);
        return buildResponse(updatedCourse);
    }

    private Course createCourse(CourseRequest course) {
        User instructor = userRepository.findByUserNameAndRole(course.instructorName(), INSTRUCTOR)
                .orElseThrow(() -> new IllegalArgumentException("Instructor not found"));

        if (courseRepository.existsByCourseTitle(course.courseName()))
            throw new IllegalArgumentException("Course already exists: " + course.courseName());

        Course created = new Course(course.courseName(), course.courseDuration(), instructor);
        return courseRepository.save(created);
    }

    // helper method to create response
    private CourseResponse buildResponse(Course course) {
        return new CourseResponse(
                course.getInstructor().getUserId(),
                course.getCourseId(),
                course.getCourseTitle(),
                course.getInstructor().getUserName(),
                course.getCreatedAt(),
                course.getUpdatedAt(),
                course.getCourseDuration()
        );
    }
}
