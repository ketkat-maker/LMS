package Ebrahem.Group.LMS.Service;

import Ebrahem.Group.LMS.Model.Dtos.EnrollmentRequest;
import Ebrahem.Group.LMS.Model.Dtos.EnrollmentResponse;


public interface EnrollmentService {
    EnrollmentResponse enrollInCourse(EnrollmentRequest request);


}
