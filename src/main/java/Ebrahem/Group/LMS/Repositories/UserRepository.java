package Ebrahem.Group.LMS.Repositories;

import Ebrahem.Group.LMS.Model.Entity.User;
import Ebrahem.Group.LMS.Model.Enums.Role;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import java.util.List;
import java.util.Optional;


@Repository
public interface UserRepository extends JpaRepository<User, String> {
    boolean existsByUserEmail(String email);

    Optional<User> findByUserEmail(String email);

    List<User> findByRoleIn(List<Role> roles);

    Optional<User> findByUserName(String name);

    User findByUserId(String name);

    Optional<User> findByUserNameAndRole(@NotEmpty(message = "Instructor name cant be empty ") String userName, Role role);
}
