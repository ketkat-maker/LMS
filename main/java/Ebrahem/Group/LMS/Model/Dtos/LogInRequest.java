package Ebrahem.Group.LMS.Model.Dtos;


import jakarta.validation.constraints.NotBlank;

public record LogInRequest(
        @NotBlank(message = "Email is required")
        String logInEmail,
        @NotBlank(message = "Password is required")
        String logInPassword
) {
}
