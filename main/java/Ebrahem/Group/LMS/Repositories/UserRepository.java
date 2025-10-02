package Ebrahem.Group.LMS.Repositories;

import Ebrahem.Group.LMS.Model.Entity.User;
import Ebrahem.Group.LMS.Model.Enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository< User, UUID> {
    boolean existsByUserEmail(String email);
    Optional<User> findByUserEmail(String email );
    List<User> findByRoleIn(List<Role> roles);
    Optional<User> findByUserName(String name);

}
