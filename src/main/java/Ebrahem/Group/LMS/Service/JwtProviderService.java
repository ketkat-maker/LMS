package Ebrahem.Group.LMS.Service;

import org.springframework.security.core.userdetails.UserDetails;

public interface JwtProviderService {
    String generateAccessToken(UserDetails details);

    String generateRefreshToken(UserDetails details);

    String extractUsername(String token);

    String extractTokenId(String token);

    boolean isTokenValid(String token, UserDetails userDetails);


    String extractTokenType(String requestRefreshToken);
}