package com.droppa.DroppaUserService.exception;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
	
	@ExceptionHandler(UserNotFoundException.class)
	public ResponseEntity<ErrorResponse> handleUserNotFound(
	        UserNotFoundException ex) {

	    ErrorResponse response = ErrorResponse.builder()
	            .timestamp(LocalDateTime.now())
	            .status(HttpStatus.NOT_FOUND.value())
	            .error(HttpStatus.NOT_FOUND.getReasonPhrase())
	            .message(ex.getMessage())
	            .build();

	    return ResponseEntity.status(HttpStatus.NOT_FOUND)
	            .body(response);
	}

}
