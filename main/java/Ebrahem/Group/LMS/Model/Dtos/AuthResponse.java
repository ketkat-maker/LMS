package Ebrahem.Group.LMS.Model.Dtos;

import Ebrahem.Group.LMS.Model.Enums.Role;

import java.util.UUID;


public record AuthResponse(
        UUID logInId,
        String fallName,
        String logInEmail,
        Role role,
        String token

) {
}

