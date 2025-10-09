package Ebrahem.Group.LMS.Controller.Excption;

import Ebrahem.Group.LMS.Model.Dtos.ApiError;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;

@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException ex)
            throws IOException, ServletException {

        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType("application/json;charset=UTF-8");

        ApiError error = ApiError.builder()
                .status(HttpStatus.FORBIDDEN.value())
                .message("You are not authorized to access this resource.")
                .path(request.getRequestURI())
                .timestamp(LocalDateTime.now(ZoneId.systemDefault()).truncatedTo(ChronoUnit.SECONDS))
                .build();

        String jsonResponse = objectMapper.writeValueAsString(error);

        response.getWriter().write(jsonResponse);
        response.getWriter().flush();
    }
}
