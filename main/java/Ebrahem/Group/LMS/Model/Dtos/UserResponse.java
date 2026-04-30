package Ebrahem.Group.LMS.Model.Dtos;

import Ebrahem.Group.LMS.Model.Enums.Role;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record UserResponse(
        String UserId,
        String UserName,
        String UserEmail,
        LocalDateTime CreatedAt,
        Role role
) {
}
