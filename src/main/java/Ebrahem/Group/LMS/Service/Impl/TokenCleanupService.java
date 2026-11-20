package Ebrahem.Group.LMS.Service.Impl;

import Ebrahem.Group.LMS.Repositories.TokenRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class TokenCleanupService {

    private final TokenRepository tokenRepository;

    @Scheduled(cron = "0 0 3 * * ?")
    @Transactional
    public void cleanupExpiredTokens() {
        tokenRepository.deleteAllExpiredOrRevoked(LocalDateTime.now());
    }
}