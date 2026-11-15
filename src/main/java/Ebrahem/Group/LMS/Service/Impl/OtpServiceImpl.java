package Ebrahem.Group.LMS.Service.Impl;

import Ebrahem.Group.LMS.Model.Dtos.Otp;
import Ebrahem.Group.LMS.Service.OtpService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class OtpServiceImpl implements OtpService {
    private static final Logger log = LoggerFactory.getLogger(OtpServiceImpl.class);
    private final Map<String, Otp> generatedOtp = new ConcurrentHashMap<>();


    @Override
    public synchronized String generateOtp(String email) {
        SecureRandom secureRandom = new SecureRandom();
        log.info("Generating OTP for email: {}", email);
        int otp = secureRandom.nextInt(100_000, 1_000_000);
        Otp newOtp = new Otp(email,
                String.valueOf(otp)
                , java.time.LocalDateTime.now().
                plusMinutes(5),
                false);
        log.info("generated Otp: {}", newOtp);
        generatedOtp.put(email, newOtp);
        log.info("OTP generated for email: {}", email);
        return String.valueOf(otp);
    }

    @Override
    public synchronized boolean validateOtp(String email, String otp) {
        Otp storedOtp = generatedOtp.get(email);

        log.info("Validating OTP for email: {}", email);

        if (storedOtp == null) {
            log.warn("No OTP found for email: {}", email);
            return false;
        }

        if (!storedOtp.expirationTime().isAfter(LocalDateTime.now())) {
            log.warn("OTP expired for email: {}", email);
            clearOtp(email);
            throw new BadCredentialsException("OTP expired");
        }

        if (!storedOtp.otp().equals(otp)) {
            log.warn("Failed to validate OTP for email: {}", email);
            return false;
        }
        if (storedOtp.isUsed()) {
            log.warn("OTP already used for email: {}", email);
            clearOtp(email);
            return false;
        }

        Otp usedOtp = new Otp(
                storedOtp.email(),
                storedOtp.otp(),
                storedOtp.expirationTime(),
                true
        );

        generatedOtp.put(email, usedOtp);

        log.info("OTP validated successfully for email: {}", email);
        return true;
    }

    @Override
    public synchronized void clearOtp(String email) {
        generatedOtp.remove(email);
    }

}



