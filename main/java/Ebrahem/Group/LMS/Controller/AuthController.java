package Ebrahem.Group.LMS.Controller;

import Ebrahem.Group.LMS.Model.Dtos.AuthResponse;
import Ebrahem.Group.LMS.Model.Dtos.LogInRequest;
import Ebrahem.Group.LMS.Model.Dtos.SignUpRequest;
import Ebrahem.Group.LMS.Service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
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

    @Operation(summary = "You already have Account ")
    @PostMapping(path = "/login")
    public ResponseEntity<AuthResponse> logIn(@Valid @RequestBody LogInRequest request) {
        AuthResponse response = authService.LogIn(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "You don't have account yet")
    @PostMapping(path = "/sign")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody SignUpRequest request) {
        AuthResponse response = authService.getTokenFromSignUp(request);
        return new ResponseEntity<>(
                response, HttpStatus.CREATED
        );
    }
    @Operation(summary = "Reset password")
    @PostMapping(path = "/resetPassword/{newPassword}/{userEmail}")
    public  ResponseEntity<AuthResponse>resetPass(@Valid @PathVariable String newPassword,@PathVariable String userEmail){
        AuthResponse resetPassword = authService.getTokenFromReset(newPassword, userEmail);
        return  new ResponseEntity<>(
                resetPassword,
          HttpStatus.ACCEPTED
        );
    }
}
