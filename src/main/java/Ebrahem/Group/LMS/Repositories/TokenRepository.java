package Ebrahem.Group.LMS.Repositories;

import Ebrahem.Group.LMS.Model.Entity.RefreshToken;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface TokenRepository extends JpaRepository<RefreshToken, String> {

    @Transactional
    @Modifying
    @Query("DELETE FROM RefreshToken t WHERE t.user.userId = :userId")
    void deleteAllByUserId(@Param("userId") String userId);

    @Modifying
    @Query("DELETE FROM RefreshToken t WHERE t.expiryDate < :now OR t.revoked = true")
    void deleteAllExpiredOrRevoked(@Param("now") LocalDateTime now);

    Optional<RefreshToken> findByToken(String requestRefreshToken);
}
