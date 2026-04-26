package Ebrahem.Group.LMS.Service;

import Ebrahem.Group.LMS.Model.Dtos.CourseFilter;
import Ebrahem.Group.LMS.Model.Dtos.CourseResponse;
import Ebrahem.Group.LMS.Model.Dtos.EnrollmentResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface StudentService {
    List<CourseResponse> getAllCourses();

    List<EnrollmentResponse> getStudentEnrollments(UUID studentId);

    Page<CourseResponse> getCourses(CourseFilter filter, Pageable pageable);
}
