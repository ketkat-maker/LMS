package Ebrahem.Group.LMS.Service.Impl;

import Ebrahem.Group.LMS.Model.Dtos.CourseResponse;
import Ebrahem.Group.LMS.Model.Dtos.EnrollmentResponse;
import Ebrahem.Group.LMS.Model.Entity.Course;
import Ebrahem.Group.LMS.Model.Entity.User;
import Ebrahem.Group.LMS.Repositories.CourseRepository;
import Ebrahem.Group.LMS.Repositories.UserRepository;
import Ebrahem.Group.LMS.Service.StudentService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StudentServiceImpl implements StudentService {
    private final CourseRepository courseRepository;
    private final UserRepository studentRepository;

    @Transactional
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
    protected List<Course> returnAllCourses() {
        return courseRepository.findAll();
    }

    @PreAuthorize("hasRole('STUDENT')")
    @Override
    public List<EnrollmentResponse> getStudentEnrollments(UUID studentId) {
        Optional<User> studentEnrolments = Optional.of(studentRepository.findById(studentId).orElseThrow(
                () -> new RuntimeException("student doesn't have an id ")));
        return studentEnrolments.stream()
                .map(student -> new EnrollmentResponse(
                        student.getCourses().getFirst().getCourseTitle(),
                        student.getUserName(),
                        student.getEnrollments().getFirst().getStatus(),
                        student.getEnrollments().getFirst().getEnrollAt(),
                        student.getEnrollments().getFirst().getProgress()
                )).toList();
    }
}
