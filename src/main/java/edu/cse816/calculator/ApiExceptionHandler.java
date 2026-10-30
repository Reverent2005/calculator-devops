package edu.cse816.calculator;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
public class ApiExceptionHandler {
    @ExceptionHandler(ArithmeticException.class)
    public ResponseEntity<ApiError> divisionByZero(ArithmeticException ex) {
        return error(HttpStatus.BAD_REQUEST, "Invalid calculation", ex.getMessage());
    }

    @ExceptionHandler({MissingServletRequestParameterException.class, MethodArgumentTypeMismatchException.class})
    public ResponseEntity<ApiError> invalidParameter(Exception ex) {
        return error(HttpStatus.BAD_REQUEST, "Invalid request", "Parameters 'a' and 'b' must be valid numeric values");
    }

    private ResponseEntity<ApiError> error(HttpStatus status, String error, String message) {
        return ResponseEntity.status(status).body(new ApiError(status.value(), error, message));
    }
}
