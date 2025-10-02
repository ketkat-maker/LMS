package Ebrahem.Group.LMS.Model.Dtos;

import Ebrahem.Group.LMS.Model.Enums.Role;

import java.time.LocalDateTime;
import java.util.UUID;

public record Student(
        UUID StudentId,
        String StudentName,
        String StudentEmail,
        LocalDateTime CreatedAt,
        Role role
) {
}
