package Ebrahem.Group.LMS.Model.Dtos;

import Ebrahem.Group.LMS.Model.Enums.Role;

import java.time.LocalDateTime;
import java.util.UUID;

public record UsersDto(
        UUID userID,
        Role userRole,
        LocalDateTime createdAt,
        String token
) {
}
