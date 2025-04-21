package bg.reshavalnik.app.controller;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handleIllegalArgument_ShouldReturnBadRequestWithExceptionMessage() {
        String errorMessage = "Test exception message";
        IllegalArgumentException exception = new IllegalArgumentException(errorMessage);

        ResponseEntity<?> response = handler.handleIllegalArgument(exception);

        assertEquals(
                HttpStatus.BAD_REQUEST,
                response.getStatusCode(),
                "Очаква се статус 400 BAD_REQUEST");
        assertEquals(
                errorMessage,
                response.getBody(),
                "Очаква се тялото на отговора да е съобщението на изключението");
    }
}
