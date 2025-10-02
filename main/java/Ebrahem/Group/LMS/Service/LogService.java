package Ebrahem.Group.LMS.Service;

import Ebrahem.Group.LMS.Model.Dtos.AuthResponse;
import Ebrahem.Group.LMS.Model.Dtos.LogInRequest;
import Ebrahem.Group.LMS.Model.Dtos.SignUpRequest;

public interface LogService {
    AuthResponse LogIn(LogInRequest user);
//    User SignUp(SignUpRequest signUpRequest);
    AuthResponse getTokenFromSingUp(SignUpRequest signUpRequest);

    }
