package Ebrahem.Group.LMS.Service;

import Ebrahem.Group.LMS.Model.Dtos.EnrollmentRequest;
import Ebrahem.Group.LMS.Model.Entity.Enrollment;

import java.util.UUID;
public interface EnrollmentService {
    Enrollment enrollInCourse(EnrollmentRequest request);
}
