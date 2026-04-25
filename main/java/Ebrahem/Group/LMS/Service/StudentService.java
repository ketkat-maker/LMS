package Ebrahem.Group.LMS.Service;

import Ebrahem.Group.LMS.Model.Dtos.CourseResponse;
import Ebrahem.Group.LMS.Model.Dtos.EnrollmentResponse;

import java.util.List;
import java.util.UUID;

public interface StudentService {
    List<CourseResponse> getAllCourses();

    List<EnrollmentResponse> getStudentEnrollments(UUID studentId);
}
