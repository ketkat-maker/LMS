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
        String courseDuration
) {
    public static CourseResponse fromEntity(UUID instructorId,
                                            UUID courseId,
                                            String courseTitle,
                                            String instructorName,
                                            LocalDateTime createdAt,
                                            LocalDateTime updatedAt,
                                            Duration duration) {
        return new CourseResponse(
                instructorId,
                courseId,
                courseTitle,
                instructorName,
                createdAt,
                updatedAt,
                formatDuration(duration)
        );
    }

    private static String formatDuration(Duration duration) {
        if (duration == null) return "00h 00m";
        long hours = duration.toHours();
        long minutes = duration.toMinutesPart();
        return String.format("%02dh %02dm", hours, minutes);
    }
}
