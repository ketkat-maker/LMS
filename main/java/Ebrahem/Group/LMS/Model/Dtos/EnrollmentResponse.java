package Ebrahem.Group.LMS.Model.Dtos;

import Ebrahem.Group.LMS.Model.Enums.Status;

import java.time.LocalDateTime;

public record EnrollmentResponse(
        String courseName,
        String StudentName,
        Status status,
        LocalDateTime enrollAt,
        float progress
) {

}
