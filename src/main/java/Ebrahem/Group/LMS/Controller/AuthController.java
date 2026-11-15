package Ebrahem.Group.LMS.Controller;

import Ebrahem.Group.LMS.Model.Dtos.*;
import Ebrahem.Group.LMS.Service.AuthService;
import Ebrahem.Group.LMS.Service.RateLimit.RateLimiterService;
import io.github.bucket4j.Bucket;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final RateLimiterService rateLimiterService;

    @Operation(summary = "You already have Account ")
    @PostMapping(path = "/login")
    public ResponseEntity<String> logIn(@Valid @RequestBody LogInRequest request,
                                        HttpServletRequest httpRequest
    ) {
        String key = request.logInEmail() + "_" + httpRequest.getRemoteAddr() + "_login";
        Bucket bucket = rateLimiterService.resolveBucket(key);
        if (!bucket.tryConsume(1)) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).
                    body("Too many requests. Try again later. ");
        }
        String response = authService.logIn(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping(path = "/verifyLoginOtp")
    public ResponseEntity<AuthResponse> verifyLoginOtp(@RequestBody ReceiveEmailResponse receiveEmailResponse) {
        AuthResponse authResponse = authService.verifyLogInOtp(receiveEmailResponse.UserEmail(), receiveEmailResponse.Otp());
        return ResponseEntity.status(HttpStatus.OK).body(authResponse);
    }

    @Operation(summary = "You don't have account yet")
    @PostMapping(path = "/sign-up/via-email")
    public ResponseEntity<String> signUpVerifyEmail(
            @Valid @RequestParam String emailRequest,
            HttpServletRequest httpRequest
    ) {
        String key = emailRequest + "_" + httpRequest.getRemoteAddr() + "_signup";

        Bucket bucket = rateLimiterService.resolveBucket(key);
        if (!bucket.tryConsume(1)) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).
                    body("Too many requests. Try again later. ");
        }
        String response = authService.signUpViaEmail(emailRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping(path = "/sign-up/verifyOtp")
    public ResponseEntity<String> verifySignUpOtp(@RequestBody ReceiveEmailResponse receiveEmailResponse) {
        String authResponse = authService.verifySignUpOtp(receiveEmailResponse);
        return ResponseEntity.status(HttpStatus.OK).body(authResponse);
    }

    @PostMapping(path = "/sign-up/complete")
    public ResponseEntity<AuthResponse> completeSignUp(@RequestBody SignUpRequest signUpRequest) {
        AuthResponse completeSignUp = authService.completeSignUp(signUpRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(completeSignUp);

    }


    @Operation(summary = "Reset password")
    @PostMapping(path = "/resetPassword")
    public ResponseEntity<AuthResponse> resetPassword(
            @Valid @RequestBody ResetPasswordDto resetPasswordDto
            , HttpServletRequest httpRequest) {
        String key = resetPasswordDto.userEmail() + "_" + httpRequest.getRemoteAddr() + "_reset";
        Bucket bucket = rateLimiterService.resolveBucket(key);
        if (!bucket.tryConsume(1)) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .body(new AuthResponse(null, null, null, null, null, "Too many requests. Try again later."));
        }
        AuthResponse resetPassword = authService.getTokenFromReset(resetPasswordDto.newPassword(), resetPasswordDto.userEmail());
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(resetPassword);
    }

    @DeleteMapping("/logout")
    public ResponseEntity<String> logout() {

        authService.logOut();
        return ResponseEntity.status(HttpStatus.OK).body("User Logout successfully ");
    }
}
