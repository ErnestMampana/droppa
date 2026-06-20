package com.droppa.DroppaUserService.exception;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(OtpExpiredException.class)
	public ResponseEntity<ErrorResponse> handleOtpExpired(OtpExpiredException ex) {

		ErrorResponse response = ErrorResponse.builder()
				.timestamp(LocalDateTime.now())
				.status(HttpStatus.GONE.value())
				.error(HttpStatus.GONE.getReasonPhrase())
				.message(ex.getMessage())
				.build();

		return ResponseEntity.status(HttpStatus.GONE)
				.body(response);
	}

	@ExceptionHandler(ClientException.class)
	public ResponseEntity<ErrorResponse> handleClientException(ClientException ex) {

		ErrorResponse response = ErrorResponse.builder()
				.timestamp(LocalDateTime.now())
				.status(HttpStatus.BAD_REQUEST.value())
				.error(HttpStatus.BAD_REQUEST.getReasonPhrase())
				.message(ex.getMessage())
				.build();

		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
				.body(response);
	}
	
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

	@ExceptionHandler(IncorrectPasswordException.class)
	public ResponseEntity<ErrorResponse> handleIncorrectPassword(
	        IncorrectPasswordException ex) {

	    ErrorResponse response = ErrorResponse.builder()
	            .timestamp(LocalDateTime.now())
	            .status(HttpStatus.UNAUTHORIZED.value())
	            .error(HttpStatus.UNAUTHORIZED.getReasonPhrase())
	            .message(ex.getMessage())
	            .build();

	    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
	            .body(response);
	}

}
