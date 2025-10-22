package bg.reshavalnik.app.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.RestClientException;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

/**
 * Global exception mapper that converts common authentication and validation errors
 * into concise JSON responses suitable for the frontend.
 *
 * <p>It intentionally returns 400 (Bad Request) for invalid/expired third-party tokens
 * rather than 500, and logs details on the server for observability.</p>
 */
@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler({IllegalArgumentException.class})
    public ResponseEntity<Map<String, Object>> handleIllegalArgument(IllegalArgumentException ex, HttpServletRequest request) {
        log.warn("Client error at {}: {}", request.getRequestURI(), ex.getMessage());
        return badRequest(ex.getMessage());
    }

    @ExceptionHandler({RestClientException.class})
    public ResponseEntity<Map<String, Object>> handleRestClient(RestClientException ex, HttpServletRequest request) {
        log.warn("Upstream auth provider error at {}: {}", request.getRequestURI(), ex.getMessage());
        return badRequest("Unable to validate social token");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest request) {
        log.warn("Validation failed at {}: {}", request.getRequestURI(), ex.getMessage());
        return badRequest("Validation failed");
    }

    private ResponseEntity<Map<String, Object>> badRequest(String message) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", Instant.now().toString());
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("error", "Bad Request");
        body.put("message", message);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }
}
