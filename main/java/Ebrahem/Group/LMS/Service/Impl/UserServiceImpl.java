package Ebrahem.Group.LMS.Service.Impl;

import Ebrahem.Group.LMS.Mapper.StudentMapper;
import Ebrahem.Group.LMS.Model.Dtos.Student;
import Ebrahem.Group.LMS.Model.Dtos.UsersDto;
import Ebrahem.Group.LMS.Model.Entity.User;
import Ebrahem.Group.LMS.Model.Enums.Role;
import Ebrahem.Group.LMS.Repositories.UserRepository;
import Ebrahem.Group.LMS.Security.LMSUserSecurity;
import Ebrahem.Group.LMS.Service.AuthenticateService;
import Ebrahem.Group.LMS.Service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final AuthenticateService authenticateService;
    private final UserRepository repository;
    private final StudentMapper studentMapper;
    @PreAuthorize("hasRole('ADMIN')")
    @Override
    public List<Student> getAllStudentAndInstructor()  {
        List<Role> roles = Arrays.asList(Role.STUDENT, Role.INSTRUCTOR);
        List<User> byRole = repository.findByRoleIn(roles);
        return studentMapper.toStudentFromEntity(byRole);
    }

   @PreAuthorize("hasRole('ADMIN')")
    @Override
    public void deleteStudentOrInstructor(UUID id) {
        if(id == null){
            throw new IllegalArgumentException("ID must by not Null");
        }

        repository.deleteById(id);

    }
    @PreAuthorize("hasRole('ADMIN')")
    @Override
    public List<UsersDto> getAllByAdmin() {
        List<User> users = getUserList();

        return users.stream().
                map(user -> new
                        UsersDto(user.getUserId()
                        ,user.getRole(),
                        user.getCreatedAt()
                        ,getToken(user)))
                .toList();
    }
    private String getToken(User user) {
        LMSUserSecurity userDetails = new LMSUserSecurity(user);
        return authenticateService.generateToken(userDetails);
    }
    private List<User> getUserList() {
        if(!repository.findByRole(Role.ADMIN).equals("ADMIN")){
            throw new RuntimeException("You are not allowed to get all users ");
        }
        return repository.findAll();
    }


}
