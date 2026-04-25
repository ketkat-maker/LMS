package Ebrahem.Group.LMS.Controller;

import Ebrahem.Group.LMS.Model.Dtos.AuthResponse;
import Ebrahem.Group.LMS.Model.Dtos.LogInRequest;
import Ebrahem.Group.LMS.Model.Dtos.SignUpRequest;
import Ebrahem.Group.LMS.Model.Dtos.resetPasswordDto;
import Ebrahem.Group.LMS.Service.AuthService;
import io.github.bucket4j.Bucket;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping(path = "/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();


    // limit rate of requests on server
    private Bucket createNewBucket(String key) {
        return
                buckets.computeIfAbsent(key, k ->
                        Bucket.builder().
                                addLimit(limit -> limit.capacity(50).
                                        refillGreedy(5, Duration.ofSeconds(1))).build());

    }

    @Operation(summary = "You already have Account ")
    @PostMapping(path = "/login")
    public ResponseEntity<AuthResponse> logIn(@Valid @RequestBody LogInRequest request) {

        String key = request.logInEmail() + "_login";
        Bucket bucket = createNewBucket(key);
        if (!bucket.tryConsume(1)) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).
                    body(new AuthResponse(null, null, null, null, null, "Too many requests. Try again later. "));
        }
        AuthResponse response = authService.LogIn(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "You don't have account yet")
    @PostMapping(path = "/sign")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody SignUpRequest request) {
        String key = request.userEmail() + "_signup";
        Bucket bucket = createNewBucket(key);
        if (bucket.tryConsume(1)) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).
                    body(new AuthResponse(null, null, null, null, null, "Too many requests. Try again later. "));
        }
        AuthResponse response = authService.getTokenFromSignUp(request);
        return new ResponseEntity<>(
                response, HttpStatus.CREATED
        );
    }

    @Operation(summary = "Reset password")
    @PostMapping(path = "/resetPassword/{newPassword}/{userEmail}")
    public ResponseEntity<AuthResponse> resetPass(@Valid @RequestBody resetPasswordDto resetPasswordDto) {
        AuthResponse resetPassword = authService.getTokenFromReset(resetPasswordDto.newPassword(), resetPasswordDto.userEmail());
        return new ResponseEntity<>(
                resetPassword,
                HttpStatus.ACCEPTED
        );
    }
}
