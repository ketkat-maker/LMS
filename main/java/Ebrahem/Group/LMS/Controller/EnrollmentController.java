package Ebrahem.Group.LMS.Controller;

import Ebrahem.Group.LMS.Model.Dtos.EnrollmentRequest;
import Ebrahem.Group.LMS.Model.Entity.Enrollment;
import Ebrahem.Group.LMS.Service.EnrollmentService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/enrollment")
@RequiredArgsConstructor
public class EnrollmentController {
    private final EnrollmentService enrollmentService;
    @Operation(summary = "Recorded Student in  course")
    @PostMapping//("/createCourse")
    public ResponseEntity<Enrollment>enrollInCourse(@Valid @RequestBody EnrollmentRequest request){
        Enrollment enrollment = enrollmentService.enrollInCourse(request);
        return new ResponseEntity<>(enrollment, HttpStatus.CREATED);
    }
}
