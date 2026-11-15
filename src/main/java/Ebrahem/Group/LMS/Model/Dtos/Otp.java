package Ebrahem.Group.LMS.Model.Dtos;

import java.time.LocalDateTime;

public record Otp(
        String email,
        String otp,
        LocalDateTime expirationTime,
        Boolean  isUsed
) {
}
