package Ebrahem.Group.LMS.Service;

import Ebrahem.Group.LMS.Model.Dtos.AuthResponse;
import Ebrahem.Group.LMS.Model.Dtos.LogInRequest;
import Ebrahem.Group.LMS.Model.Dtos.SignUpRequest;

import java.util.UUID;

public interface AuthService {
    AuthResponse LogIn(LogInRequest user);

    AuthResponse getTokenFromSignUp(SignUpRequest signUpRequest);

    AuthResponse getTokenFromReset(String newPassword, String userEmail);

    void LogOut(UUID userId);
}
