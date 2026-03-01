package Ebrahem.Group.LMS.Service;

import org.springframework.security.core.userdetails.UserDetails;

public interface JwtProviderService {
    String generateToken(UserDetails details);

    String extractUsername(String token);

    boolean isTokenValid(String token, UserDetails userDetails);
}