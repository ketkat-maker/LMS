package Ebrahem.Group.LMS.Service.Impl;


import Ebrahem.Group.LMS.Excption.UnauthorizedActionException;
import Ebrahem.Group.LMS.Model.Dtos.*;
import Ebrahem.Group.LMS.Model.Entity.Token;
import Ebrahem.Group.LMS.Model.Entity.User;
import Ebrahem.Group.LMS.Model.Enums.OtpStatus;
import Ebrahem.Group.LMS.Model.Enums.Role;
import Ebrahem.Group.LMS.Repositories.TokenRepository;
import Ebrahem.Group.LMS.Repositories.UserRepository;
import Ebrahem.Group.LMS.Security.UserSecurity;
import Ebrahem.Group.LMS.Service.AuthService;
import Ebrahem.Group.LMS.Service.JwtProviderService;
import Ebrahem.Group.LMS.Service.MailService;
import Ebrahem.Group.LMS.Service.OtpService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProviderService jwtProviderService;
    private final TokenRepository tokenRepository;
    private final OtpService otpService;
    private final MailService mailService;

    @Override
    public String logIn(LogInRequest request) {
        log.info("Attempting to log in user with email: {}", request.logInEmail());
        String email = request.logInEmail();
        User user = userRepository.findByUserEmail(email)
                .orElseThrow(() -> new BadCredentialsException("User doesn't exist by this email: " + email));
        if (!passwordEncoder.matches(request.logInPassword(), user.getUserPassword())) {
            throw new BadCredentialsException("Invalid email ro password");
        }
        String generateOtp = otpService.generateOtp(email);
        log.info("Generated OTP for user with email: {}", email);
        mailService.sendEmail(new EmailServiceDto(
                email,
                "OTP Verification",
                "Your OTP is: " + generateOtp
        ));
        log.info("OTP sent to user with email: {}", email);
        return "OTP sent to your email. Please check your inbox.";
    }

    public AuthResponse verifyLogInOtp(String email, String otp) {
        log.info("Verifying login OTP for user with email: {}", email);
        if (email == null || otp == null) {
            throw new BadCredentialsException("Invalid email or OTP");
        }
        boolean isValidOtp = otpService.validateOtp(email, otp);
        if (!isValidOtp) {
            throw new BadCredentialsException("Invalid OTP");
        }
        User user = userRepository.findByUserEmail(email).orElseThrow();
        log.info("Login OTP verified for user with email: {}", email);
        String token = generateTokenAndSaved(user);
        return new AuthResponse(
                user.getUserId(),
                user.getUserName(),
                user.getUserEmail(),
                user.getRole(),
                token,
                "user log in successful");
    }


    @Override
    public String signUpViaEmail(String userEmail) {
        boolean userExists = userRepository.existsByUserEmail(userEmail);
        if (userExists) {
            throw new BadCredentialsException("User already exists by this email: " + userEmail);
        }

        userRepository.save(new User(
                "pending user",
                userEmail,
                "test user",
                Role.STUDENT,
                OtpStatus.PENDING,
                LocalDateTime.now()

        ));

        String generateOtp = otpService.generateOtp(userEmail);
        mailService.sendEmail(new EmailServiceDto(
                userEmail,
                "OTP Verification",
                "Your OTP is: " + generateOtp
        ));
        return "OTP sent to your email. Please check your inbox.";
    }

    @Transactional
    @Override
    public String verifySignUpOtp(ReceiveEmailResponse receiveEmailResponse) {
        String email = receiveEmailResponse.UserEmail();
        String otp = receiveEmailResponse.Otp();
        log.info("Verifying sign-up OTP for user with email: {}", email);

        if (email == null || otp == null) {
            throw new BadCredentialsException("Invalid email or OTP");
        }
        User user = userRepository.findByUserEmail(email).
                orElseThrow(() -> new BadCredentialsException("not found user by this email: " + email));

        boolean isValidOtp = otpService.validateOtp(email, otp);
        if (!isValidOtp) {
            throw new BadCredentialsException("Invalid OTP");
        }
        log.info("Opt before update:{}", user.getOtpStatus());
        user.setOtpStatus(OtpStatus.TRUEOTP);
        user.setVerifiedAt(LocalDateTime.now());
        log.info("Otp after update:{}", user.getOtpStatus());
        log.info("Sign-up OTP verified for user with email: {}", email);
        return "OTP verified successfully. You can now complete the sign-up process.";

    }

    @Transactional
    @Override
    public AuthResponse completeSignUp(SignUpRequest signUpRequest) {
        String userEmail = signUpRequest.userEmail();
        User user = userRepository.findByUserEmailWithLock(userEmail).orElseThrow(
                () -> new EntityNotFoundException("User isn't exists by this email: " + userEmail));
        log.info("otp status from complete signup :{}", user.getOtpStatus());
        if (user.getOtpStatus() != (OtpStatus.TRUEOTP) || LocalDateTime.now().isAfter(user.getVerifiedAt().plusMinutes(10))) {
            throw new BadCredentialsException("Invalid user authentication. Please verify your email first.");
        }


        user.setOtpStatus(OtpStatus.COMPLETE);
        user.setUserPassword(passwordEncoder.encode(signUpRequest.password()));
        user.setUserName(signUpRequest.firstName() + " " + signUpRequest.lastName());
        user.setRole(signUpRequest.role());

        String token = generateTokenAndSaved(user);
        return new AuthResponse(
                user.getUserId(),
                user.getUserName(),
                userEmail,
                user.getRole(),
                token,
                "user sign up successful");

    }


    @Override
    public AuthResponse getTokenFromReset(String newPassword, String userEmail) {
        User user = resetPassword(newPassword, userEmail);
        return new AuthResponse(
                user.getUserId(),
                user.getUserName(),
                user.getUserEmail(),
                user.getRole(),
                generateTokenAndSaved(user)
                ,
                null
        );
    }

    @Override
    public void logOut() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || "anonymousUser".equals(authentication.getPrincipal())) {
            throw new UnauthorizedActionException("You must be logged in with a valid Bearer token to log out (Token is missing, expired, or revoked).");
        }
        if (!(authentication.getPrincipal() instanceof UserSecurity principal)) {
            throw new UnauthorizedActionException("Invalid authentication principal type: " + authentication.getPrincipal().getClass().getSimpleName());
        }
        String userId = principal.getUserId();
        tokenRepository.deleteAllByUserId(userId);
    }

    private User resetPassword(String newPassword, String userEmail) {
        
        User user = userRepository.findByUserEmail(userEmail).orElseThrow(() ->
                new EntityNotFoundException("User isn't exists by this email: " + userEmail));
        if (passwordEncoder.matches(newPassword, user.getUserPassword())) {
            throw new IllegalArgumentException("New password cant equal old password");
        }
        user.setUserPassword(passwordEncoder.encode(newPassword));
        return userRepository.save(user);
    }

    private String generateTokenAndSaved(User user) {
        revokeAllUserTokens(user);

        UserDetails userDetails = new UserSecurity(user);
        String jwt = jwtProviderService.generateToken(userDetails);
        String tokenId = jwtProviderService.extractTokenId(jwt);
        Token token = Token.builder()
                .id(tokenId)
                .token(jwt)
                .user(user)
                .revoked(false)
                .expired(false)
                .build();
        tokenRepository.save(token);

        return jwt;
    }

    private void revokeAllUserTokens(User user) {
        List<Token> validTokens = tokenRepository
                .findAllByUserAndRevokedFalseAndExpiredFalse(user);
        validTokens.forEach(t -> {
            t.setRevoked(true);
            t.setExpired(true);
        });
        tokenRepository.saveAll(validTokens);
    }

}




