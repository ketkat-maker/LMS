package Ebrahem.Group.LMS.Service;

import Ebrahem.Group.LMS.Model.Dtos.AuthResponse;
import Ebrahem.Group.LMS.Model.Dtos.LogInRequest;
import Ebrahem.Group.LMS.Model.Dtos.SignUpRequest;

public interface AuthService {
    AuthResponse LogIn(LogInRequest user);

    AuthResponse getTokenFromSignUp(SignUpRequest signUpRequest);

    AuthResponse getTokenFromReset(String newPassword, String userEmail);

    void LogOut(String userId);
}
