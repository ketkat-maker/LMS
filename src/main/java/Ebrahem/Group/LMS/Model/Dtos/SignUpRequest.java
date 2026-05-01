package Ebrahem.Group.LMS.Model.Dtos;

import Ebrahem.Group.LMS.Model.Enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record SignUpRequest(
        @NotBlank(message = "Email is required")
        @Email(message = "Email must be valid")
        @Size(min = 5, max = 100)
        String userEmail,
        @Size(min = 4, message = "First name must be at least 8 characters")
        @NotBlank
        String firstName,
        @Size(min = 4, message = "Last name must be at least 8 characters")
        @NotBlank
        String lastName,
        Role role,
        @NotBlank
        @Size(min = 8, message = "Password must be at least 8 characters")
        @Pattern(regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d).*$",
                message = "Password must contain uppercase, lowercase, and number")
        String password
) {
}
