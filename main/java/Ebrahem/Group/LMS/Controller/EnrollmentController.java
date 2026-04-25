package Ebrahem.Group.LMS.Controller;

import Ebrahem.Group.LMS.Model.Dtos.EnrollmentRequest;
import Ebrahem.Group.LMS.Model.Dtos.EnrollmentResponse;
import Ebrahem.Group.LMS.Service.EnrollmentService;
import Ebrahem.Group.LMS.Service.StudentService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/enrollment")
@RequiredArgsConstructor
public class EnrollmentController {
    private final EnrollmentService enrollmentService;
    private final StudentService studentService;

    @Operation(summary = "Recorded Student in  course")
    @PostMapping("/createEnroll")
    public ResponseEntity<EnrollmentResponse> enrollInCourse(@Valid @RequestBody EnrollmentRequest request) {
        EnrollmentResponse enrollment = enrollmentService.enrollInCourse(request);
        return new ResponseEntity<>(enrollment, HttpStatus.OK);
    }

    @Operation(summary = "Get all Student enrollments")
    @GetMapping(path = "/{studentId}")
    public ResponseEntity<List<EnrollmentResponse>> getStudentEnrollment(@Valid @PathVariable UUID studentId) {
        List<EnrollmentResponse> studentEnrollments = studentService.getStudentEnrollments(studentId);
        return new ResponseEntity<>(studentEnrollments, HttpStatus.ACCEPTED);
    }
}
