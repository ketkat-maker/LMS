package Ebrahem.Group.LMS.Service;

import Ebrahem.Group.LMS.Model.Dtos.Student;
import Ebrahem.Group.LMS.Model.Dtos.UsersDto;
import Ebrahem.Group.LMS.Model.Entity.User;

import java.util.List;
import java.util.UUID;


public interface UserService {
    List<Student> getAllStudentAndInstructor() ;
    void deleteStudentOrInstructor(UUID id);
   List<UsersDto> getAllByAdmin();

}
