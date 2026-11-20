package Ebrahem.Group.LMS.Model.Dtos;

import jakarta.validation.constraints.NotBlank;

public record RefreshTokenRequest(@NotBlank(message = "Refresh token is required")
                                  String refreshToken) {

}
