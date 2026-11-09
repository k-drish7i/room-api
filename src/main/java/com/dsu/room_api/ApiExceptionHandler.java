package com.dsu.room_api;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.List;

@RestControllerAdvice
public class ApiExceptionHandler {

    private ResponseEntity<ApiError> respond(HttpStatus status, String message, HttpServletRequest request, List<ApiError.FieldMessage> fields) {
        ApiError error = ApiError.of(status, message, request.getRequestURI(), fields);
        return ResponseEntity.status(status).body(error);
    }

    // 1. Bean Validation failed (@Valid on request body) -> 400
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> invalidFields(MethodArgumentNotValidException e, HttpServletRequest request) {
        List<ApiError.FieldMessage> fields = e.getBindingResult().getFieldErrors().stream()
                .map(f -> new ApiError.FieldMessage(f.getField(), f.getDefaultMessage()))
                .toList();
        return respond(HttpStatus.BAD_REQUEST, "request has invalid fields", request, fields);
    }

    // 2. Custom 404
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ApiError> notFound(NotFoundException e, HttpServletRequest request) {
        return respond(HttpStatus.NOT_FOUND, e.getMessage(), request, List.of());
    }

    // 3. Custom 409 & Database Foreign Key block
    @ExceptionHandler({ConflictException.class, DataIntegrityViolationException.class})
    public ResponseEntity<ApiError> conflict(Exception e, HttpServletRequest request) {
        String msg = e.getMessage();
        if (e instanceof DataIntegrityViolationException) {
            msg = "cannot delete room with active reservations";
        }
        return respond(HttpStatus.CONFLICT, msg, request, List.of());
    }

    // 4. IllegalArgumentException -> 400 (e.g., startTime must be before endTime)
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> illegalArgument(IllegalArgumentException e, HttpServletRequest request) {
        return respond(HttpStatus.BAD_REQUEST, e.getMessage(), request, List.of());
    }

    // 5. Unreadable JSON body (Request 40) -> 400
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiError> notReadable(HttpMessageNotReadableException e, HttpServletRequest request) {
        return respond(HttpStatus.BAD_REQUEST, "not valid JSON", request, List.of());
    }

    // 6. Bad URL path variable type (Request 41) -> 400
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiError> typeMismatch(MethodArgumentTypeMismatchException e, HttpServletRequest request) {
        return respond(HttpStatus.BAD_REQUEST, "'" + e.getName() + "' has the wrong type", request, List.of());
    }

    // 7. Safety net for unknown URLs and unhandled exceptions (Request 33)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> other(Exception e, HttpServletRequest request) {
        if (e instanceof ErrorResponse spring) {
            HttpStatus status = HttpStatus.valueOf(spring.getStatusCode().value());
            String detail = spring.getBody().getDetail();
            return respond(status, detail != null ? detail : status.getReasonPhrase(), request, List.of());
        }
        return respond(HttpStatus.INTERNAL_SERVER_ERROR, "unexpected server error", request, List.of());
    }
}