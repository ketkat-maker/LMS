package Ebrahem.Group.LMS.Controller.Excption;

import org.springframework.stereotype.Component;

@Component
public class InvalidTokenException extends RuntimeException {
    public InvalidTokenException(){}
    public InvalidTokenException(String message) {
        super(message);
    }
    public InvalidTokenException(String message, Throwable cause) {
        super(message, cause);
    }
}