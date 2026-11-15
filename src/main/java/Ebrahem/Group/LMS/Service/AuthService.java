package Ebrahem.Group.LMS.Service;

import Ebrahem.Group.LMS.Model.Dtos.AuthResponse;
import Ebrahem.Group.LMS.Model.Dtos.LogInRequest;
import Ebrahem.Group.LMS.Model.Dtos.ReceiveEmailResponse;
import Ebrahem.Group.LMS.Model.Dtos.SignUpRequest;

public interface AuthService {
    String logIn(LogInRequest user);

    String signUpViaEmail(String email);

    AuthResponse getTokenFromReset(String newPassword, String userEmail);

    void logOut();

    AuthResponse verifyLogInOtp(String email, String otp);

    String verifySignUpOtp(ReceiveEmailResponse receiveEmailResponse);

    AuthResponse completeSignUp(SignUpRequest signUpRequest);


}
