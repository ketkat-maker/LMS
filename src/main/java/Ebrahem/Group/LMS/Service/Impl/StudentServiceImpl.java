package Ebrahem.Group.LMS.Service.Impl;

import Ebrahem.Group.LMS.Model.Dtos.CourseFilter;
import Ebrahem.Group.LMS.Model.Dtos.CourseResponse;
import Ebrahem.Group.LMS.Model.Dtos.EnrollmentResponse;
import Ebrahem.Group.LMS.Model.Entity.Course;
import Ebrahem.Group.LMS.Model.Entity.Enrollment;
import Ebrahem.Group.LMS.Model.Entity.User;
import Ebrahem.Group.LMS.Repositories.CourseRepository;
import Ebrahem.Group.LMS.Repositories.EnrollmentRepository;
import Ebrahem.Group.LMS.Service.Specification.CourseSpecification;
import Ebrahem.Group.LMS.Service.StudentService;
import Ebrahem.Group.LMS.Utilitys.PublicUtility;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StudentServiceImpl implements StudentService {
    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;

    @Transactional
    @Override
    public List<CourseResponse> getAllCourses() {
        List<Course> courses = courseRepository.findAll();

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

    
    @PreAuthorize("hasRole('STUDENT')")
    @Override
    public List<EnrollmentResponse> getStudentEnrollments(String studentId) {
        User student = PublicUtility.getCurrentUser();
        List<Enrollment> enrollments = enrollmentRepository.findByUser(student);

        return enrollments.stream()
                .map(e -> new EnrollmentResponse(
                        e.getCourse().getCourseTitle(),
                        e.getUser().getUserName(),
                        e.getStatus(),
                        e.getEnrollAt(),
                        e.getProgress()
                )).toList();
    }

    @Override
    public Page<CourseResponse> getCourses(CourseFilter filter, Pageable pageable) {
        return courseRepository.findAll(CourseSpecification.withFilter(filter),
                        pageable)
                .map(e -> new CourseResponse(
                        e.getInstructor().getUserId(),
                        e.getCourseId(),
                        e.getCourseTitle(),
                        e.getInstructor().getUserName(),
                        e.getCreatedAt(),
                        e.getUpdatedAt(),
                        e.getCourseDuration()));
    }
}
