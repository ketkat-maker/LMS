package Ebrahem.Group.LMS.Repositories;

import Ebrahem.Group.LMS.Model.Entity.Token;
import Ebrahem.Group.LMS.Model.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TokenRepository extends JpaRepository<Token, String> {
    List<Token> findAllByUserAndRevokedFalseAndExpiredFalse(User user);

    @Modifying
    @Query("DELETE FROM Token t WHERE t.user.id = :userId")
    void deleteAllByUserId(@Param("userId") String userId);


}
