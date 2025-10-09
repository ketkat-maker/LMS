package Ebrahem.Group.LMS.Mapper;
import Ebrahem.Group.LMS.Model.Dtos.UserResponse;
import Ebrahem.Group.LMS.Model.Entity.User;

import java.util.List;

public interface UserMapper {
    List<UserResponse> toUserDtoFromEntity(List<User> student);

}
