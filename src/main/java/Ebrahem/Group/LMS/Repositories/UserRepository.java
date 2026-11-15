package Ebrahem.Group.LMS.Repositories;

import Ebrahem.Group.LMS.Model.Entity.User;
import Ebrahem.Group.LMS.Model.Enums.Role;
import jakarta.persistence.LockModeType;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface UserRepository extends JpaRepository<User, String> {
    boolean existsByUserEmail(String email);

    Optional<User> findByUserEmail(String email);

    List<User> findByRoleIn(List<Role> roles);

    Optional<User> findByUserNameAndRole(@NotEmpty(message = "Instructor name cant be empty ") String userName, Role role);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT u From User u WHERE u.userEmail = :email ")
    Optional<User> findByUserEmailWithLock(@Param("email") String email);
}
