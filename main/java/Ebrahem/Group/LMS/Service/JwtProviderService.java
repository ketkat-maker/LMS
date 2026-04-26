package Ebrahem.Group.LMS.Service;

import org.springframework.security.core.userdetails.UserDetails;

import java.util.UUID;

public interface JwtProviderService {
    String generateToken(UserDetails details, UUID id);

    String extractUsername(String token);

    String extractTokenId(String token);

    boolean isTokenValid(String token, UserDetails userDetails);


}