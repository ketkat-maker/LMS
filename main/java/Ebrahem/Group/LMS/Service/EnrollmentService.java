package Ebrahem.Group.LMS.Service;

import Ebrahem.Group.LMS.Model.Dtos.EnrollmentRequest;
import Ebrahem.Group.LMS.Model.Entity.Enrollment;


public interface EnrollmentService {
    Enrollment enrollInCourse(EnrollmentRequest request);
}
