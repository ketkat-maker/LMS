package Ebrahem.Group.LMS.Model.Dtos;

import Ebrahem.Group.LMS.Model.Enums.Role;

import java.time.LocalDateTime;
import java.util.UUID;

public record UserResponse(
        UUID UserId,
        String UserName,
        String UserEmail,
        LocalDateTime CreatedAt,
        Role role
) {
}
