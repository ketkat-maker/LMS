package Ebrahem.Group.LMS.Model.Dtos;

import Ebrahem.Group.LMS.Model.Enums.Role;

public record SignUpRequest(
        String userEmail,
        String firstName,
        String lastName,
        Role role,
        String password
) {
}
