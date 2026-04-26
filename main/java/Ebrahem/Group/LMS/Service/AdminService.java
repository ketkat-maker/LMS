package Ebrahem.Group.LMS.Service;

import Ebrahem.Group.LMS.Model.Dtos.UserResponse;
import Ebrahem.Group.LMS.Model.Dtos.UsersDto;
import Ebrahem.Group.LMS.Model.Enums.Role;

import java.util.List;
import java.util.UUID;


public interface AdminService {
    public List<UserResponse> getUsersByRoles(List<Role> roles);
    void deleteStudentOrInstructor(UUID id);



}
