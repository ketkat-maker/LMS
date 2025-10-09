package Ebrahem.Group.LMS.Service;

import Ebrahem.Group.LMS.Model.Dtos.UserResponse;
import Ebrahem.Group.LMS.Model.Dtos.UsersDto;

import java.util.List;
import java.util.UUID;


public interface AdminService {
    List<UserResponse> getAllStudentAndInstructor() ;
    void deleteStudentOrInstructor(UUID id);
   List<UsersDto> getAllByAdmin();

}
