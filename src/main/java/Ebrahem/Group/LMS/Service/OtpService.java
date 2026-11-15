package Ebrahem.Group.LMS.Service;


public interface OtpService {
    String generateOtp(String email);

    boolean validateOtp(String email, String otp);

    void clearOtp(String email);
}
