package Ebrahem.Group.LMS.Model.Dtos;

import jakarta.validation.constraints.NotEmpty;

import java.time.LocalDateTime;

public record CourseRequest(
        @NotEmpty(message = "Course name cant be empty ")
        String courseName,
        LocalDateTime courseDuration
) {
}
