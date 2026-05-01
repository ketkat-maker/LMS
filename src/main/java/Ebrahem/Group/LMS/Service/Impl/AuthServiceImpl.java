package Ebrahem.Group.LMS.Service.Impl;


import Ebrahem.Group.LMS.Model.Dtos.AuthResponse;
import Ebrahem.Group.LMS.Model.Dtos.LogInRequest;
import Ebrahem.Group.LMS.Model.Dtos.SignUpRequest;
import Ebrahem.Group.LMS.Model.Entity.Token;
import Ebrahem.Group.LMS.Model.Entity.User;
import Ebrahem.Group.LMS.Repositories.TokenRepository;
import Ebrahem.Group.LMS.Repositories.UserRepository;
import Ebrahem.Group.LMS.Security.UserSecurity;
import Ebrahem.Group.LMS.Service.AuthService;
import Ebrahem.Group.LMS.Service.JwtProviderService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProviderService jwtProviderService;
    private final TokenRepository tokenRepository;

    @Override
    public AuthResponse LogIn(LogInRequest request) {
        String email = request.logInEmail();
        User user = repository.findByUserEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User doesn't exist by this email: " + email));

        if (!passwordEncoder.matches(request.logInPassword(), user.getUserPassword())) {
            throw new BadCredentialsException("Invalid email ro password");
        }

        String token = generateTokenAndSaved(user);

        return new AuthResponse(
                user.getUserId(),
                user.getUserName(),
                user.getUserEmail(),
                user.getRole(),
                token,
                "user log in successful");
    }


    private User SignUp(SignUpRequest signUpRequest) {
        boolean existsByUserEmail = repository.existsByUserEmail(signUpRequest.userEmail());

        if (existsByUserEmail) {
            throw new IllegalArgumentException("User already exist");
        }
        User entityFromSignUp = toEntityFromSignUp(signUpRequest);

        return repository.save(entityFromSignUp);
    }

    @Override
    public AuthResponse getTokenFromSignUp(SignUpRequest signUpRequest) {

        User signUp = SignUp(signUpRequest);
        return new AuthResponse(
                signUp.getUserId(),
                signUp.getUserName(),
                signUp.getUserEmail(),
                signUp.getRole(),
                generateTokenAndSaved(signUp)
                ,
                null
        );
    }

    @Override
    public AuthResponse getTokenFromReset(String newPassword, String userEmail) {
        User user = resetPassword(newPassword, userEmail);
        return new AuthResponse(
                user.getUserId(),
                user.getUserName(),
                user.getUserEmail(),
                user.getRole(),
                generateTokenAndSaved(user)
                ,
                null
        );
    }

    @Override
    public void LogOut() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("User not logged in");
        }
        UserSecurity principal = (UserSecurity) authentication.getPrincipal();
        String userId = principal.getUserId();
        tokenRepository.deleteAllByUserId(userId);
    }

    private User resetPassword(String newPassword, String userEmail) {
        User user = repository.findByUserEmail(userEmail).orElseThrow(() ->
                new RuntimeException("User isn't exists by this email: " + userEmail));
        if (passwordEncoder.matches(newPassword, user.getUserPassword())) {
            throw new IllegalArgumentException("New password cant equal old password");
        }
        user.setUserPassword(passwordEncoder.encode(newPassword));
        return repository.save(user);
    }

    private String generateTokenAndSaved(User user) {
        revokeAllUserTokens(user);

        UserDetails userDetails = new UserSecurity(user);
        UUID tokenId = UUID.randomUUID();
        String jwt = jwtProviderService.generateToken(userDetails, tokenId);

        Token token = Token.builder()
                .id(tokenId)
                .token(jwt)
                .user(user)
                .revoked(false)
                .expired(false)
                .build();
        tokenRepository.save(token);

        return jwt;
    }

    private void revokeAllUserTokens(User user) {
        List<Token> validTokens = tokenRepository
                .findAllByUserAndRevokedFalseAndExpiredFalse(user);
        validTokens.forEach(t -> {
            t.setRevoked(true);
            t.setExpired(true);
        });
        tokenRepository.saveAll(validTokens);
    }

    private User toEntityFromSignUp(SignUpRequest signUpRequest) {
        return new User(
                passwordEncoder.encode(signUpRequest.password()),
                signUpRequest.role()
                , signUpRequest.userEmail()
                , signUpRequest.firstName() + " " + signUpRequest.lastName());
    }
}




