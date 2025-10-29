package Ebrahem.Group.LMS.Service.Impl;

import Ebrahem.Group.LMS.Controller.Excption.DuplicateEntityException;
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

@Service
@RequiredArgsConstructor
public class EnrollmentServiceImpl implements EnrollmentService {
    private final EnrollmentRepository enrollmentRepository;
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    @PreAuthorize("hasRole('INSTRUCTOR') or hasRole('STUDENT')")
    @Override
    public Enrollment enrollInCourse(EnrollmentRequest request) {
        String studentName = request.studentName();
        String courseName = request.courseName();

        User existUser = userRepository.findByUserName(studentName).
                orElseThrow(()->new IllegalArgumentException("User does not exist by this name: "+ studentName));
        Course existsCourse = courseRepository.findByCourseTitle(courseName).
                orElseThrow(()->new IllegalArgumentException("course does not exist by this name: "+ courseName));
        boolean alreadyEnrolled = enrollmentRepository.existsByUserAndCourse(
                existUser,
                existsCourse
        );
        if(alreadyEnrolled){
            throw new DuplicateEntityException("Student Already enroll by this name: "+studentName);
        }
        Enrollment savedEnroll = new Enrollment(existUser
                , existsCourse
                , Status.ACTIVE
                , 0.0F
                , LocalDateTime.now());
        return enrollmentRepository.save(savedEnroll);
    }

}
