package Ebrahem.Group.LMS.Model.Dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.UUID;


public record EnrollmentRequest(
        @NotBlank
        @NotEmpty(message = "Course name cannot be empty")
        String courseName,
        @NotBlank
        @NotEmpty(message = "Student name cannot be empty")
        String studentName,
        UUID studentId,
        UUID courseID

) {
}
