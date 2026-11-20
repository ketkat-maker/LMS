package Ebrahem.Group.LMS.Service;

import Ebrahem.Group.LMS.Model.Dtos.*;

public interface AuthService {
    String logIn(LogInRequest user);

    String signUpViaEmail(String email);

    AuthResponse getTokenFromReset(ResetPasswordDto resetPasswordDto);

    void logOut();

    AuthResponse verifyLogInOtp(String email, String otp);

    String verifySignUpOtp(ReceiveEmailResponse receiveEmailResponse);

    AuthResponse completeSignUp(SignUpRequest signUpRequest);


}
