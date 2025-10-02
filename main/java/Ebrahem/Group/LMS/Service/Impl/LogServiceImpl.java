package Ebrahem.Group.LMS.Service.Impl;

import Ebrahem.Group.LMS.Mapper.LogMapper;
import Ebrahem.Group.LMS.Model.Dtos.AuthResponse;
import Ebrahem.Group.LMS.Model.Dtos.LogInRequest;
import Ebrahem.Group.LMS.Model.Dtos.SignUpRequest;
import Ebrahem.Group.LMS.Model.Entity.User;
import Ebrahem.Group.LMS.Repositories.UserRepository;
import Ebrahem.Group.LMS.Security.LMSUserSecurity;
import Ebrahem.Group.LMS.Service.AuthenticateService;
import Ebrahem.Group.LMS.Service.LogService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LogServiceImpl implements LogService {
    private final LogMapper mapper;
    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticateService authenticateService;


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
                token);
    }

    private String getToken(User user) {
        LMSUserSecurity userDetails = new LMSUserSecurity(user);
        return authenticateService.generateToken(userDetails);
    }

    private User SignUp(SignUpRequest signUpRequest) {
        boolean existsByUserEmail = repository.existsByUserEmail(signUpRequest.userEmail());

        if(existsByUserEmail){
            throw new IllegalArgumentException("User already exist");
        }
        User entityFromSignUp = mapper.toEntityFromSignUp(signUpRequest);

        return repository.save(entityFromSignUp);
    }
    @Override
public AuthResponse getTokenFromSingUp(SignUpRequest signUpRequest){

    User signUp = SignUp(signUpRequest);
    return new AuthResponse(
            signUp.getUserId(),
            signUp.getUserName(),
            signUp.getUserEmail(),
            signUp.getRole(),
            getToken(signUp)
    );
}
}




