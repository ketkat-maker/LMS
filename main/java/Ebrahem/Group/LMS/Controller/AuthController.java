package Ebrahem.Group.LMS.Controller;

import Ebrahem.Group.LMS.Model.Dtos.AuthResponse;
import Ebrahem.Group.LMS.Model.Dtos.LogInRequest;
import Ebrahem.Group.LMS.Model.Dtos.ResetPasswordDto;
import Ebrahem.Group.LMS.Model.Dtos.SignUpRequest;
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
    public ResponseEntity<AuthResponse> logIn(@Valid @RequestBody LogInRequest request,
                                              HttpServletRequest httpRequest
    ) {
        String key = request.logInEmail() + "_" + httpRequest.getRemoteAddr() + "_login";
        Bucket bucket = rateLimiterService.resolveBucket(key);
        if (!bucket.tryConsume(1)) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).
                    body(new AuthResponse(null, null, null, null, "Too many requests. Try again later. "));
        }
        AuthResponse response = authService.LogIn(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "You don't have account yet")
    @PostMapping(path = "/sign")
    public ResponseEntity<AuthResponse> register(
            @Valid @RequestBody SignUpRequest request,
            HttpServletRequest httpRequest
    ) {
        String key = request.userEmail() + "_" + httpRequest.getRemoteAddr() + "_signup";

        Bucket bucket = rateLimiterService.resolveBucket(key);
        if (!bucket.tryConsume(1)) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).
                    body(new AuthResponse(null, null, null, null, "Too many requests. Try again later. "));
        }
        AuthResponse response = authService.getTokenFromSignUp(request);
        return new ResponseEntity<>(
                response, HttpStatus.CREATED
        );
    }

    @Operation(summary = "Reset password")
    @PostMapping(path = "/resetPassword")
    public ResponseEntity<AuthResponse> resetPass(
            @Valid @RequestBody ResetPasswordDto resetPasswordDto
            , HttpServletRequest httpRequest) {
        String key = resetPasswordDto.userEmail() + "_" + httpRequest.getRemoteAddr() + "_reset";
        Bucket bucket = rateLimiterService.resolveBucket(key);
        if (!bucket.tryConsume(1)) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .body(new AuthResponse(null, null, null, null, "Too many requests. Try again later."));
        }
        AuthResponse resetPassword = authService.getTokenFromReset(resetPasswordDto.newPassword(), resetPasswordDto.userEmail());
        return new ResponseEntity<>(
                resetPassword,
                HttpStatus.ACCEPTED
        );
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<String> logout(@PathVariable String userId) {

        authService.LogOut(userId);
        return new ResponseEntity<>("User Logout successfully ", HttpStatus.OK);
    }
}
