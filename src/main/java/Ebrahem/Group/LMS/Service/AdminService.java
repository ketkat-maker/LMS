package Ebrahem.Group.LMS.Service;

import Ebrahem.Group.LMS.Model.Dtos.UserResponse;
import Ebrahem.Group.LMS.Model.Enums.Role;

import java.util.List;


public interface AdminService {
    List<UserResponse> getUsersByRoles(List<Role> roles);

    void deleteStudentOrInstructor(String id);


}
