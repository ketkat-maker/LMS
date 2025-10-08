package Ebrahem.Group.LMS.Service;

import org.springframework.security.core.userdetails.UserDetails;

public interface JwtProviderService {
//    UserDetails authenticate(String email,String password);
    String generateToken(UserDetails details);
//    UserDetails validToken(String token);
}