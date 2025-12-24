package Ebrahem.Group.LMS.Service;

import Ebrahem.Group.LMS.Model.Dtos.AuthResponse;
import Ebrahem.Group.LMS.Model.Dtos.LogInRequest;
import Ebrahem.Group.LMS.Model.Dtos.SignUpRequest;
import Ebrahem.Group.LMS.Model.Entity.User;

import java.util.UUID;

public interface AuthService {
    AuthResponse LogIn(LogInRequest user);
    AuthResponse getTokenFromSignUp(SignUpRequest signUpRequest);
    AuthResponse getTokenFromReset (String newPassword , String userEmail);
}
