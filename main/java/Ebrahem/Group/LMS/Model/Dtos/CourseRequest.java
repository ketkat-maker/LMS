package Ebrahem.Group.LMS.Model.Dtos;

import jakarta.validation.constraints.NotEmpty;

public record CourseRequest(
        @NotEmpty(message = "Course name cant be empty ")
        String courseName,
        @NotEmpty(message = "Instructor name cant be empty ")
        String instructorName,
        String courseDuration
) {
}
