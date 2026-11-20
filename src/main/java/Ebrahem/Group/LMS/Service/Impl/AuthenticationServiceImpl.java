package Ebrahem.Group.LMS.Service.Impl;

import Ebrahem.Group.LMS.Excption.UnauthorizedActionException;
import Ebrahem.Group.LMS.Model.Dtos.*;
import Ebrahem.Group.LMS.Model.Entity.RefreshToken;
import Ebrahem.Group.LMS.Model.Entity.User;
import Ebrahem.Group.LMS.Model.Enums.OtpStatus;
import Ebrahem.Group.LMS.Model.Enums.Role;
import Ebrahem.Group.LMS.Repositories.TokenRepository;
import Ebrahem.Group.LMS.Repositories.UserRepository;
import Ebrahem.Group.LMS.Security.UserSecurity;
import Ebrahem.Group.LMS.Service.AuthenticationService;
import Ebrahem.Group.LMS.Service.JwtProviderService;
import Ebrahem.Group.LMS.Service.MailService;
import Ebrahem.Group.LMS.Service.OtpService;
import io.jsonwebtoken.JwtException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProviderService jwtProviderService;
    private final TokenRepository tokenRepository;
    private final OtpService otpService;
    private final MailService mailService;

    @Override
    public String logIn(LogInRequest request) {
        String email = request.logInEmail();
        User user = userRepository.findByUserEmail(email)
                .orElseThrow(() -> new BadCredentialsException("Invalid email or password"));

        if (!passwordEncoder.matches(request.logInPassword(), user.getUserPassword())) {
            throw new BadCredentialsException("Invalid email or password");
        }

        String generateOtp = otpService.generateOtp(email);
        mailService.sendEmail(new EmailServiceDto(
                email,
                "OTP Verification",
                "Your OTP is: " + generateOtp
        ));
        return "OTP sent to your email. Please check your inbox.";
    }

    @Transactional
    @Override
    public AuthResponse verifyLogInOtp(String email, String otp) {
        if (email == null || otp == null) {
            throw new BadCredentialsException("Invalid email or OTP");
        }
        boolean isValidOtp = otpService.validateOtp(email, otp);
        if (!isValidOtp) {
            throw new BadCredentialsException("Invalid OTP");
        }
        User user = userRepository.findByUserEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        return generateTokensAndSaveRefresh(user);
    }

    @Transactional
    @Override
    public String signUpViaEmail(String userEmail) {
        Optional<User> existingUser = userRepository.findByUserEmail(userEmail);

        if (existingUser.isPresent()) {
            if (existingUser.get().getOtpStatus() == OtpStatus.COMPLETE) {
                throw new BadCredentialsException("User already exists by this email: " + userEmail);
            }
        } else {
            userRepository.save(new User(
                    "pending user",
                    userEmail,
                    "test user",
                    Role.STUDENT,
                    OtpStatus.PENDING,
                    LocalDateTime.now()
            ));
        }

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

        if (email == null || otp == null) {
            throw new BadCredentialsException("Invalid email or OTP");
        }
        User user = userRepository.findByUserEmail(email)
                .orElseThrow(() -> new BadCredentialsException("Not found user by this email: " + email));

        boolean isValidOtp = otpService.validateOtp(email, otp);
        if (!isValidOtp) {
            throw new BadCredentialsException("Invalid OTP");
        }
        user.setOtpStatus(OtpStatus.TRUEOTP);
        user.setVerifiedAt(LocalDateTime.now());
        return "OTP verified successfully. You can now complete the sign-up process.";
    }

    @Transactional
    @Override
    public AuthResponse completeSignUp(SignUpRequest signUpRequest) {
        if (signUpRequest.role() == Role.ADMIN) {
            throw new UnauthorizedActionException("Cannot register as ADMIN via public sign-up.");
        }

        String userEmail = signUpRequest.userEmail();
        User user = userRepository.findByUserEmailWithLock(userEmail)
                .orElseThrow(() -> new EntityNotFoundException("User doesn't exist by this email: " + userEmail));

        log.info("otp status from complete signup :{}", user.getOtpStatus());
        if (user.getOtpStatus() != OtpStatus.TRUEOTP || LocalDateTime.now().isAfter(user.getVerifiedAt().plusMinutes(10))) {
            throw new BadCredentialsException("Invalid user authentication. Please verify your email first.");
        }

        user.setOtpStatus(OtpStatus.COMPLETE);
        user.setUserPassword(passwordEncoder.encode(signUpRequest.password()));
        user.setUserName(signUpRequest.firstName() + " " + signUpRequest.lastName());
        user.setRole(signUpRequest.role());

        return generateTokensAndSaveRefresh(user);
    }

    @Transactional
    @Override
    public AuthResponse getTokenFromReset(ResetPasswordDto resetPasswordDto) {
        UserSecurity currentUser = (UserSecurity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String currentPassword = currentUser.getPassword();
        String currentUserEmail = currentUser.getUsername();

        if (!currentUserEmail.equals(resetPasswordDto.userEmail())) {
            throw new UnauthorizedActionException("You are not authorized to reset the password for this email.");
        }
        if (passwordEncoder.matches(resetPasswordDto.newPassword(), currentPassword)) {
            throw new BadCredentialsException("New password can't equal old password");
        }

        currentUser.user().setUserPassword(passwordEncoder.encode(resetPasswordDto.newPassword()));
        User savedUser = userRepository.save(currentUser.user());

        return generateTokensAndSaveRefresh(savedUser);
    }

    @Transactional
    @Override
    public void logOut() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || "anonymousUser".equals(authentication.getPrincipal())) {
            throw new UnauthorizedActionException("You must be logged in with a valid Bearer token to log out (Token is missing, expired, or revoked).");
        }
        if (!(authentication.getPrincipal() instanceof UserSecurity principal)) {
            throw new UnauthorizedActionException("Invalid authentication principal type: " + authentication.getPrincipal().getClass().getSimpleName());
        }
        tokenRepository.deleteAllByUserId(principal.getUserId());
    }

    @Transactional(noRollbackFor = UnauthorizedActionException.class)
    @Override
    public AuthResponse refreshToken(RefreshTokenRequest request) {
        String requestRefreshToken = request.refreshToken();
        String userEmail;

        try {
            if (!"REFRESH".equals(jwtProviderService.extractTokenType(requestRefreshToken))) {
                throw new UnauthorizedActionException("Invalid token type. Refresh token required.");
            }
            userEmail = jwtProviderService.extractUsername(requestRefreshToken);
        } catch (JwtException | IllegalArgumentException e) {
            throw new UnauthorizedActionException("Invalid or expired refresh token.");
        }

        User user = userRepository.findByUserEmail(userEmail)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        UserSecurity userSecurity = new UserSecurity(user);
        if (!jwtProviderService.isTokenValid(requestRefreshToken, userSecurity)) {
            throw new UnauthorizedActionException("Refresh token is expired or invalid.");
        }

        RefreshToken storedToken = tokenRepository.findByToken(requestRefreshToken)
                .orElseThrow(() -> new UnauthorizedActionException("Refresh token not found. Please login again."));

        if (storedToken.isRevoked() || storedToken.isExpired()) {
            tokenRepository.deleteAllByUserId(user.getUserId());
            throw new UnauthorizedActionException("Refresh token was already used or revoked.");
        }

        return generateTokensAndSaveRefresh(user);
    }

    private AuthResponse generateTokensAndSaveRefresh(User user) {
        UserSecurity userSecurity = new UserSecurity(user);

        String accessToken = jwtProviderService.generateAccessToken(userSecurity);
        String refreshToken = jwtProviderService.generateRefreshToken(userSecurity);

        tokenRepository.deleteAllByUserId(user.getUserId());

        RefreshToken savedRefreshToken = RefreshToken.builder()
                .token(refreshToken)
                .user(user)
                .revoked(false)
                .expired(false)
                .expiryDate(LocalDateTime.now().plusDays(7))
                .build();
        tokenRepository.save(savedRefreshToken);

        return new AuthResponse(accessToken, refreshToken);
    }
}