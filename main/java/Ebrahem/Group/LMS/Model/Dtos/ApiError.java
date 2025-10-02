package Ebrahem.Group.LMS.Model.Dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ApiError {
    private String message;
    private int status;
    private String path;
    private LocalDateTime timestamp;
}
