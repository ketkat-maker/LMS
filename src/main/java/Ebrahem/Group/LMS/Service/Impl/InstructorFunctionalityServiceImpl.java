package Ebrahem.Group.LMS.Service.Impl;

import Ebrahem.Group.LMS.Excption.DuplicateEntityException;
import Ebrahem.Group.LMS.Model.Dtos.CourseRequest;
import Ebrahem.Group.LMS.Model.Dtos.CourseResponse;
import Ebrahem.Group.LMS.Model.Entity.Course;
import Ebrahem.Group.LMS.Model.Entity.User;
import Ebrahem.Group.LMS.Repositories.CourseRepository;
import Ebrahem.Group.LMS.Service.InstructorFunctionalityService;
import Ebrahem.Group.LMS.Service.Specification.CourseSpecification;
import Ebrahem.Group.LMS.Utilitys.PublicUtility;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InstructorFunctionalityServiceImpl implements InstructorFunctionalityService {
    private final CourseRepository courseRepository;
    private final CourseSpecification specification;

    @PreAuthorize("hasRole('INSTRUCTOR')")
    @Override
    public CourseResponse CreateCourse(CourseRequest course) {

        User instructorName = PublicUtility.getCurrentUser();
        if (courseRepository.existsByCourseTitle(course.courseName()))
            throw new DuplicateEntityException("Course already exists: " + course.courseName());

        Course created = new Course(course.courseName(), course.courseDuration(), instructorName);
        return buildResponse(courseRepository.save(created));
    }

    @Transactional
    @PreAuthorize("hasRole('INSTRUCTOR')")
    @Override
    public void deleteCourse(String courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new EntityNotFoundException("Course not found by Id: " + courseId));
        User instructorName = PublicUtility.getCurrentUser();
        if (!course.getInstructor().equals(instructorName)) {
            throw new AccessDeniedException("You are not authorized to modify this course");
        }
        courseRepository.delete(course);
    }

    @Transactional
    @PreAuthorize("hasRole('INSTRUCTOR')")
    @Override
    public CourseResponse updateCourse(String courseId, CourseRequest course) {
        Course courseById = courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("Course not found by this ID: " + courseId));
        User instructorName = PublicUtility.getCurrentUser();
        if (!courseById.getInstructor().equals(instructorName)) {
            throw new AccessDeniedException("You are not authorized to modify this course");
        }
        courseById.setCourseTitle(course.courseName());
        courseById.setCourseDuration(course.courseDuration());

        Course updatedCourse = courseRepository.save(courseById);
        return buildResponse(updatedCourse);
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
