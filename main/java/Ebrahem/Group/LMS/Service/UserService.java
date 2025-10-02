package Ebrahem.Group.LMS.Service;

import Ebrahem.Group.LMS.Model.Dtos.Student;

import java.util.List;
import java.util.UUID;


public interface UserService {
    List<Student> getAllStudentAndInstructor() ;
    void deleteStudentOrInstructor(UUID id);


}
