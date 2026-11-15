package Ebrahem.Group.LMS.Service.Impl;


import Ebrahem.Group.LMS.Model.Dtos.UserResponse;
import Ebrahem.Group.LMS.Model.Entity.User;
import Ebrahem.Group.LMS.Model.Enums.Role;
import Ebrahem.Group.LMS.Repositories.UserRepository;
import Ebrahem.Group.LMS.Service.AdminService;
import Ebrahem.Group.LMS.Service.JwtProviderService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {
    private final JwtProviderService jwtProviderService;
    private final UserRepository repository;


    @PreAuthorize("hasRole('ADMIN')")
    @Override
    public List<UserResponse> getUsersByRoles(List<Role> roles) {
        List<User> usersByRole = repository.findByRoleIn(roles);
        return usersByRole.stream()
                .map(user -> new UserResponse(
                        user.getUserId(),
                        user.getUserName(),
                        user.getUserEmail(),
                        user.getCreatedAt(),
                        user.getRole()
                ))
                .toList();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Override
    public void deleteUser(String id) {
        User user = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("user doesn't exist by this id: " + id));

        repository.delete(user);
    }


}
