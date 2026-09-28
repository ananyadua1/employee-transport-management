package com.ananya.transport.exception;

import org.springframework.http.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.*;

@RestControllerAdvice
public class GlobalExceptionHandler {
    record ErrorResponse(LocalDateTime timestamp, int status, String error, Object details) {}
    @ExceptionHandler(ResourceNotFoundException.class)
    ResponseEntity<ErrorResponse> notFound(ResourceNotFoundException e) { return build(HttpStatus.NOT_FOUND,e.getMessage()); }
    @ExceptionHandler({BusinessException.class, IllegalArgumentException.class})
    ResponseEntity<ErrorResponse> badRequest(RuntimeException e) { return build(HttpStatus.BAD_REQUEST,e.getMessage()); }
    @ExceptionHandler(AccessDeniedException.class)
    ResponseEntity<ErrorResponse> forbidden(AccessDeniedException e) { return build(HttpStatus.FORBIDDEN,"Access denied"); }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<ErrorResponse> validation(MethodArgumentNotValidException e) {
        Map<String,String> errors=new LinkedHashMap<>();
        e.getBindingResult().getFieldErrors().forEach(x->errors.put(x.getField(),x.getDefaultMessage()));
        return new ResponseEntity<>(new ErrorResponse(LocalDateTime.now(),400,"Validation failed",errors),HttpStatus.BAD_REQUEST);
    }
    private ResponseEntity<ErrorResponse> build(HttpStatus s,String message) { return new ResponseEntity<>(new ErrorResponse(LocalDateTime.now(),s.value(),s.getReasonPhrase(),message),s); }
}
