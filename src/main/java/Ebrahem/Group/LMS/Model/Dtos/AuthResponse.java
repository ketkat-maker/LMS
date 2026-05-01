package Ebrahem.Group.LMS.Model.Dtos;

import Ebrahem.Group.LMS.Model.Enums.Role;


public record AuthResponse(
        String logInId,
        String fullName,
        String logInEmail,
        Role role,
        String token,
        String message
) {

}

