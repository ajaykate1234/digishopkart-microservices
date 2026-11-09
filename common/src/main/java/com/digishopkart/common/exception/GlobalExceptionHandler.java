package com.digishopkart.common.exception;

import com.digishopkart.common.api.ApiError;
import com.digishopkart.common.api.FieldErrorDetail;
import jakarta.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

  private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

  @ExceptionHandler(AuthenticationException.class)
  public ResponseEntity<ApiError> authentication(AuthenticationException exception, HttpServletRequest request) {
    String message = exception instanceof DisabledException
        ? "This account is blocked."
        : "Email or password is wrong.";
    return error(HttpStatus.UNAUTHORIZED, message, request, List.of());
  }

  @ExceptionHandler(AccessDeniedException.class)
  public ResponseEntity<ApiError> denied(AccessDeniedException exception, HttpServletRequest request) {
    return error(HttpStatus.FORBIDDEN, "Your role cannot access this action.", request, List.of());
  }

  @ExceptionHandler(NotFoundException.class)
  public ResponseEntity<ApiError> notFound(NotFoundException exception, HttpServletRequest request) {
    return error(HttpStatus.NOT_FOUND, exception.getMessage(), request, List.of());
  }

  @ExceptionHandler(ConflictException.class)
  public ResponseEntity<ApiError> conflict(ConflictException exception, HttpServletRequest request) {
    return error(HttpStatus.CONFLICT, exception.getMessage(), request, List.of());
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ApiError> invalid(MethodArgumentNotValidException exception, HttpServletRequest request) {
    List<FieldErrorDetail> fields = exception.getBindingResult().getFieldErrors().stream()
        .map(field -> new FieldErrorDetail(field.getField(), field.getDefaultMessage() == null ? "Invalid value" : field.getDefaultMessage()))
        .toList();
    return error(HttpStatus.BAD_REQUEST, "Check the highlighted fields.", request, fields);
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<ApiError> unreadable(HttpMessageNotReadableException exception, HttpServletRequest request) {
    return error(HttpStatus.BAD_REQUEST, "The request body is not valid JSON.", request, List.of());
  }

  @ExceptionHandler(DataIntegrityViolationException.class)
  public ResponseEntity<ApiError> integrity(DataIntegrityViolationException exception, HttpServletRequest request) {
    return error(HttpStatus.CONFLICT, "That value is already in use.", request, List.of());
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ApiError> unexpected(Exception exception, HttpServletRequest request) {
    log.error("Unhandled error on {}", request.getRequestURI(), exception);
    return error(HttpStatus.INTERNAL_SERVER_ERROR, "Something went wrong.", request, List.of());
  }

  private ResponseEntity<ApiError> error(HttpStatus status, String message, HttpServletRequest request, List<FieldErrorDetail> fields) {
    return ResponseEntity.status(status).body(new ApiError(
        Instant.now(), status.value(), status.getReasonPhrase(), message, request.getRequestURI(), fields));
  }
}
