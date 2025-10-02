package Ebrahem.Group.LMS.Controller;

import Ebrahem.Group.LMS.Model.Dtos.AuthResponse;
import Ebrahem.Group.LMS.Model.Dtos.LogInRequest;
import Ebrahem.Group.LMS.Model.Dtos.SignUpRequest;
import Ebrahem.Group.LMS.Service.LogService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api/v1/auth")
@RequiredArgsConstructor
public class LogController {
    private final LogService logInService;
    @Operation(summary = "You already have Account ")
    @PostMapping(path = "/login")
    public ResponseEntity<AuthResponse> logIn(@RequestBody LogInRequest request) {
        AuthResponse response = logInService.LogIn(request);
        return new ResponseEntity<>(response, HttpStatus.ACCEPTED);
    }

    @Operation(summary = "You don't have account yet")
    @PostMapping(path="/sign")
        public ResponseEntity<AuthResponse>register(@RequestBody SignUpRequest request){
        AuthResponse response = logInService.getTokenFromSingUp(request);
            return new ResponseEntity<>(
                response,HttpStatus.CREATED
    );
}
}
