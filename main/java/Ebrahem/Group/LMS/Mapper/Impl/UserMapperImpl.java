package Ebrahem.Group.LMS.Mapper.Impl;

import Ebrahem.Group.LMS.Mapper.UserMapper;
import Ebrahem.Group.LMS.Model.Dtos.UserResponse;
import Ebrahem.Group.LMS.Model.Entity.User;
import org.springframework.stereotype.Component;

import java.util.List;
@Component
public class UserMapperImpl implements UserMapper {

    @Override
    public List<UserResponse> toUserDtoFromEntity(List<User> users) {
        return users.stream()
                .map(user -> new UserResponse(
                        user.getUserId(),
                        user.getUserName(),
                        user.getUserEmail(),
                        user.getCreatedAt(),
                        user.getRole()
                ))
                .toList();
    }

}
