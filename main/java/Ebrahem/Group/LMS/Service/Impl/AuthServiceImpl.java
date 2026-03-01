package Ebrahem.Group.LMS.Service.Impl;


import Ebrahem.Group.LMS.Model.Dtos.AuthResponse;
import Ebrahem.Group.LMS.Model.Dtos.LogInRequest;
import Ebrahem.Group.LMS.Model.Dtos.SignUpRequest;
import Ebrahem.Group.LMS.Model.Entity.User;
import Ebrahem.Group.LMS.Repositories.UserRepository;
import Ebrahem.Group.LMS.Security.UserSecurity;
import Ebrahem.Group.LMS.Service.JwtProviderService;
import Ebrahem.Group.LMS.Service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProviderService jwtProviderService;


    @Override
    public AuthResponse LogIn(LogInRequest request) {
        String email = request.logInEmail();
        User user = repository.findByUserEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User doesn't exist by this email: " + email));

        if (!passwordEncoder.matches(request.logInPassword(), user.getUserPassword())) {
            throw new IllegalArgumentException("Invalid password");
        }

        String token = getToken(user);

        return new AuthResponse(
                user.getUserId(),
                user.getUserName(),
                user.getUserEmail(),
                user.getRole(),
                token,null);
    }

    private String getToken(User user) {
        UserSecurity userDetails = new UserSecurity(user);
        return jwtProviderService.generateToken(userDetails);
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
                getToken(signUp),null
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
                getToken(user),
                null
        );
    }

    private User resetPassword(String newPassword,String userEmail) {
        User user = repository.findByUserEmail(userEmail).orElseThrow(()->
                new RuntimeException("User isn't exists by this email: "+userEmail));
        if (passwordEncoder.matches(newPassword,user.getUserPassword() )) {
            throw new IllegalArgumentException("New password cant equal old password");
        }
        user.setUserPassword(passwordEncoder.encode(newPassword));
        return repository.save(user);
    }


    private User toEntityFromSignUp(SignUpRequest signUpRequest) {
        return new User(
                passwordEncoder.encode(signUpRequest.password()),
                signUpRequest.role()
                , signUpRequest.userEmail()
                , signUpRequest.firstName() + " " + signUpRequest.lastName());
    }
}




