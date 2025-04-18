package bg.reshavalnik.app.controller;

import bg.reshavalnik.app.exceptions.exeption.*;
import bg.reshavalnik.app.security.dto.response.MessageResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.HashMap;
import java.util.Map;


@ControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        log.error("Validation failed: ", ex);
        Map<String, String> errors = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }
        return new ResponseEntity<>(errors, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(UserRegistrationException.class)
    public ResponseEntity<?> handleUserRegistrationException(UserRegistrationException ex) {
        log.error("Validation failed: ", ex);
        return new ResponseEntity<>(new MessageResponse(ex.getMessage()), HttpStatus.BAD_REQUEST);
    }

    private ResponseEntity<String> error(HttpStatus status, Exception e) {
        log.error("Validation failed: ", e);
        return ResponseEntity.status(status).body(e.getMessage());
    }

    @ExceptionHandler({UserException.class})
    private ResponseEntity<String> error(UserException e) {
        log.error("Validation failed: ", e);
        return error(HttpStatus.NOT_FOUND, e);
    }

    @ExceptionHandler({ProfileException.class})
    private ResponseEntity<String> error(ProfileException e) {
        log.error("Validation failed: ", e);
        return error(HttpStatus.NOT_FOUND, e);
    }

    @ExceptionHandler({RoleException.class})
    private ResponseEntity<String> error(RoleException e) {
        log.error("Validation failed: ", e);
        return error(HttpStatus.NOT_FOUND, e);
    }

    @ExceptionHandler({CreatePasswordException.class})
    private ResponseEntity<String> error(CreatePasswordException e) {
        log.error("Validation failed: ", e);
        return error(HttpStatus.NOT_FOUND, e);
    }

}
