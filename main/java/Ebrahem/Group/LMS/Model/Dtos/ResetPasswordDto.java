package Ebrahem.Group.LMS.Model.Dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ResetPasswordDto(
        @NotBlank @Email
        String userEmail,
        @NotBlank
        @Size(min = 8)
        @Pattern(regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d).*$")
        String newPassword
) {
}
