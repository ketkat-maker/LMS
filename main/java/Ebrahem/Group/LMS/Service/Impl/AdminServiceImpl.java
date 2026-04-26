package Ebrahem.Group.LMS.Service.Impl;


import Ebrahem.Group.LMS.Model.Dtos.UserResponse;
import Ebrahem.Group.LMS.Model.Entity.User;
import Ebrahem.Group.LMS.Model.Enums.Role;
import Ebrahem.Group.LMS.Repositories.UserRepository;
import Ebrahem.Group.LMS.Service.AdminService;
import Ebrahem.Group.LMS.Service.JwtProviderService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

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
    public List<UserResponse> getUsersByRoles(List<Role> roles) {
        List<User> byRole = repository.findByRoleIn(roles);
        return toUserDtoFromEntity(byRole);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Override
    public void deleteStudentOrInstructor(UUID id) {
        User user = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("user not found"));

        repository.delete(user);
    }

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
