package Ebrahem.Group.LMS.Service.Impl;

import Ebrahem.Group.LMS.Excption.DuplicateEntityException;
import Ebrahem.Group.LMS.Model.Dtos.EnrollmentRequest;
import Ebrahem.Group.LMS.Model.Dtos.EnrollmentResponse;
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
    public EnrollmentResponse enrollInCourse(EnrollmentRequest request) {
        UUID studentId = request.studentId();
        UUID courseID = request.courseID();
        String studentName = request.studentName();

        User existUser = userRepository.findById(studentId).
                orElseThrow(() -> new IllegalArgumentException("User does not exist by this name: " + studentName));
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
        enrollmentRepository.save(savedEnroll);

        return new EnrollmentResponse(
                existsCourse.getCourseTitle(),
                studentName,
                Status.ACTIVE,
                LocalDateTime.now(),
                0.0F
        );
    }

//    @PreAuthorize("hasRole('STUDENT')")
//    @Override
//    public List<EnrollmentResponse> getStudentEnrollments(UUID studentId) {
//        if (studentId == null || enrollmentRepository.existsByUser(studentId)) {
//            throw new RuntimeException("student doesn't have an id ");
//        }
//        List<Enrollment> studentEnrolments = enrollmentRepository.findByUser(studentId);
//        return studentEnrolments.stream()
//                .map(enrolment -> new EnrollmentResponse(
//                        enrolment.getCourse().getCourseTitle(),
//                        enrolment.getUser().getUserName(),
//                        enrolment.getStatus(),
//                        enrolment.getEnrollAt(),
//                        enrolment.getProgress()
//                )).toList();
//    }
}
