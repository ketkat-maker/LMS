package Ebrahem.Group.LMS.Model.Dtos;

import Ebrahem.Group.LMS.Model.Enums.Role;

import java.time.LocalDateTime;

public record UsersDto(
        String userID,
        Role userRole,
        LocalDateTime createdAt,
        String token
) {
}
