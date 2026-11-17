package Ebrahem.Group.LMS.Repositories;

import Ebrahem.Group.LMS.Model.Entity.Token;
import Ebrahem.Group.LMS.Model.Entity.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TokenRepository extends JpaRepository<Token, String> {
    List<Token> findAllByUserAndRevokedFalseAndExpiredFalse(User user);

    @Transactional
    @Modifying
    @Query("DELETE FROM Token t WHERE t.user.userId = :userId")
    void deleteAllByUserId(@Param("userId") String userId);


}
