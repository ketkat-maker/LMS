package Ebrahem.Group.LMS.Model.Dtos;

import java.time.LocalDateTime;

public record CourseResponse(
        String instructorId,
        String courseId,
        String courseTitle,
        String instructorName,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        LocalDateTime courseDuration
) {
    public static CourseResponse fromEntity(String instructorId,
                                            String courseId,
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
