package Ebrahem.Group.LMS.Service.Impl;

import Ebrahem.Group.LMS.Excption.DuplicateEntityException;
import Ebrahem.Group.LMS.Excption.UnauthorizedActionException;
import Ebrahem.Group.LMS.Model.Dtos.EnrollmentRequest;
import Ebrahem.Group.LMS.Model.Dtos.EnrollmentResponse;
import Ebrahem.Group.LMS.Model.Entity.Course;
import Ebrahem.Group.LMS.Model.Entity.Enrollment;
import Ebrahem.Group.LMS.Model.Entity.User;
import Ebrahem.Group.LMS.Model.Enums.Status;
import Ebrahem.Group.LMS.Repositories.CourseRepository;
import Ebrahem.Group.LMS.Repositories.EnrollmentRepository;
import Ebrahem.Group.LMS.Service.EnrollmentService;
import Ebrahem.Group.LMS.Utilitys.PublicUtility;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class EnrollmentServiceImpl implements EnrollmentService {
    private final EnrollmentRepository enrollmentRepository;
    private final CourseRepository courseRepository;

    @PreAuthorize("hasRole('STUDENT')")
    @Override
    @Transactional
    public EnrollmentResponse enrollInCourse(EnrollmentRequest request) {


        User currentUser = PublicUtility.getCurrentUser();
        if (!currentUser.getUserId().equals(request.studentId())) {
            throw new UnauthorizedActionException("You are not authorized to enroll in this course");
        }
        Course existsCourse = courseRepository.findById(request.courseID()).
                orElseThrow(() -> new IllegalArgumentException("course does not exist by this id: " + request.courseID()));
        boolean alreadyEnrolled = enrollmentRepository.existsByUserAndCourse(
                currentUser,
                existsCourse
        );
        if (alreadyEnrolled) {
            throw new DuplicateEntityException("Student Already enroll by this name: " + currentUser.getUserName());
        }
        Enrollment savedEnroll = new Enrollment(currentUser
                , existsCourse
                , Status.ACTIVE
                , 0.0F
                , LocalDateTime.now());
        enrollmentRepository.save(savedEnroll);

        return new EnrollmentResponse(
                existsCourse.getCourseTitle(),
                currentUser.getUserName(),
                savedEnroll.getStatus(),
                savedEnroll.getEnrollAt(),
                savedEnroll.getProgress()
        );
    }
}
