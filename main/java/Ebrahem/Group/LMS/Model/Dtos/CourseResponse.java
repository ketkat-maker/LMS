package Ebrahem.Group.LMS.Model.Dtos;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

public record CourseResponse(
        UUID instructorId,
        UUID courseId,
        String courseTitle,
        String instructorName,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        LocalDateTime courseDuration
) {
    public static CourseResponse fromEntity(UUID instructorId,
                                            UUID courseId,
                                            String courseTitle,
                                            String instructorName,
                                            LocalDateTime createdAt,
                                            LocalDateTime updatedAt,
                                            LocalDateTime duration) {
        return new CourseResponse(
                instructorId,
                courseId,
                courseTitle,
                instructorName,
                createdAt,
                updatedAt,
                duration
        );
    }


}
