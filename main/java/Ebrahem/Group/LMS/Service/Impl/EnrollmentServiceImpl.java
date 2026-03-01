package Ebrahem.Group.LMS.Service.Impl;

import Ebrahem.Group.LMS.Excption.DuplicateEntityException;
import Ebrahem.Group.LMS.Model.Dtos.EnrollmentRequest;
import Ebrahem.Group.LMS.Model.Entity.Course;
import Ebrahem.Group.LMS.Model.Entity.Enrollment;
import Ebrahem.Group.LMS.Model.Entity.User;
import Ebrahem.Group.LMS.Model.Enums.Status;
import Ebrahem.Group.LMS.Repositories.CourseRepository;
import Ebrahem.Group.LMS.Repositories.EnrollmentRepository;
import Ebrahem.Group.LMS.Repositories.UserRepository;
import Ebrahem.Group.LMS.Service.EnrollmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EnrollmentServiceImpl implements EnrollmentService {
    private final EnrollmentRepository enrollmentRepository;
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;

    @PreAuthorize("hasRole('INSTRUCTOR') or hasRole('STUDENT')")
    @Override
    public Enrollment enrollInCourse(EnrollmentRequest request) {
        UUID studentId = request.studentId();
        UUID courseID = request.courseID();
        String studentName= request.studentName();
//        String courseName= request.courseName();

        User existUser = userRepository.findById(studentId).
                orElseThrow(() -> new IllegalArgumentException("User does not exist by this name: " + studentId));
        Course existsCourse = courseRepository.findById(courseID).
                orElseThrow(() -> new IllegalArgumentException("course does not exist by this name: " + courseID));
        boolean alreadyEnrolled = enrollmentRepository.existsByUserAndCourse(
                existUser,
                existsCourse
        );
        if (alreadyEnrolled) {
            throw new DuplicateEntityException("Student Already enroll by this name: " + studentName);
        }
        Enrollment savedEnroll = new Enrollment(existUser
                , existsCourse
                , Status.ACTIVE
                , 0.0F
                , LocalDateTime.now());
        return enrollmentRepository.save(savedEnroll);
    }
}
