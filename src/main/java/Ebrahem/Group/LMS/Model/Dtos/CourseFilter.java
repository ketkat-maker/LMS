package Ebrahem.Group.LMS.Model.Dtos;

import java.time.LocalDateTime;

public record CourseFilter(
        String courseTitle,
        LocalDateTime createdFrom,
        LocalDateTime createdTo,

        LocalDateTime updatedFrom,
        LocalDateTime updatedTo
) {
}
