package Ebrahem.Group.LMS.Service.Impl;


import Ebrahem.Group.LMS.Model.Dtos.UserResponse;
import Ebrahem.Group.LMS.Model.Dtos.UsersDto;
import Ebrahem.Group.LMS.Model.Entity.User;
import Ebrahem.Group.LMS.Model.Enums.Role;
import Ebrahem.Group.LMS.Repositories.UserRepository;
import Ebrahem.Group.LMS.Security.UserSecurity;
import Ebrahem.Group.LMS.Service.AdminService;
import Ebrahem.Group.LMS.Service.JwtProviderService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {
    private final JwtProviderService jwtProviderService;
    private final UserRepository repository;

    @PreAuthorize("hasRole('ADMIN')")
    @Override
    @Transactional
    public List<UserResponse> getAllStudentAndInstructor() {
        List<Role> roles = Arrays.asList(Role.STUDENT, Role.INSTRUCTOR);
        List<User> byRole = repository.findByRoleIn(roles);
        return toUserDtoFromEntity(byRole);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Override
    public void deleteStudentOrInstructor(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("ID must by not Null");
        }
        repository.deleteById(id);
    }
    @PreAuthorize("hasRole('ADMIN')")
    @Override
    public List<UsersDto> getAllByAdmin() {
        List<User>users=repository.findAll();
        return users.stream().
                map(user -> new
                        UsersDto(user.getUserId()
                        , user.getRole(),
                        user.getCreatedAt()
                        , getToken(user)))
                .toList();
    }
    private String getToken(User user) {
        UserSecurity userDetails = new UserSecurity(user);
        return jwtProviderService.generateToken(userDetails);
    }
    private List<UserResponse> toUserDtoFromEntity(List<User> users) {
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
