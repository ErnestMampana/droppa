package com.droppa.DroppaBookingService.exceptions;

import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(UserNotFoundException.class)
	public ResponseEntity<ErrorResponse> handleUserNotFound(UserNotFoundException ex) {
		return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage());
	}

	@ExceptionHandler({BookingException.class, PromoCodeException.class})
	public ResponseEntity<ErrorResponse> handleBusinessException(RuntimeException ex) {
		return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
	}

	@ExceptionHandler(BookingAccessDeniedException.class)
	public ResponseEntity<ErrorResponse> handleBookingAccessDenied(BookingAccessDeniedException ex) {
		return buildResponse(HttpStatus.FORBIDDEN, ex.getMessage());
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex) {
		String message = ex.getBindingResult()
				.getFieldErrors()
				.stream()
				.map(error -> error.getField() + ": " + error.getDefaultMessage())
				.collect(Collectors.joining("; "));

		if (message.isBlank()) {
			message = "Request validation failed";
		}

		return buildResponse(HttpStatus.BAD_REQUEST, message);
	}

	@ExceptionHandler({
			HttpMessageNotReadableException.class,
			MissingRequestHeaderException.class,
			MissingServletRequestParameterException.class,
			MethodArgumentTypeMismatchException.class,
			IllegalArgumentException.class
	})
	public ResponseEntity<ErrorResponse> handleBadRequest(Exception ex) {
		return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
	}

	@ExceptionHandler(NoHandlerFoundException.class)
	public ResponseEntity<ErrorResponse> handleNoHandlerFound(NoHandlerFoundException ex) {
		String message = "No endpoint found for " + ex.getHttpMethod() + " " + ex.getRequestURL();
		return buildResponse(HttpStatus.NOT_FOUND, message);
	}

	@ExceptionHandler(HttpRequestMethodNotSupportedException.class)
	public ResponseEntity<ErrorResponse> handleMethodNotSupported(HttpRequestMethodNotSupportedException ex) {
		return buildResponse(HttpStatus.METHOD_NOT_ALLOWED, ex.getMessage());
	}

	@ExceptionHandler(HttpMediaTypeNotSupportedException.class)
	public ResponseEntity<ErrorResponse> handleUnsupportedMediaType(HttpMediaTypeNotSupportedException ex) {
		return buildResponse(HttpStatus.UNSUPPORTED_MEDIA_TYPE, ex.getMessage());
	}

	@ExceptionHandler(DataIntegrityViolationException.class)
	public ResponseEntity<ErrorResponse> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
		log.warn("Database constraint violation", ex);
		return buildResponse(HttpStatus.CONFLICT, "Request conflicts with existing data");
	}

	@ExceptionHandler(FeignException.class)
	public ResponseEntity<ErrorResponse> handleFeignException(FeignException ex) {
		HttpStatus status = mapFeignStatus(ex.status());
		log.warn("Downstream service request failed with status {}", ex.status(), ex);
		return buildResponse(status, "Downstream service request failed");
	}

	@ExceptionHandler(ResponseStatusException.class)
	public ResponseEntity<ErrorResponse> handleResponseStatusException(ResponseStatusException ex) {
		return buildResponse(ex.getStatusCode(), ex.getReason());
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorResponse> handleUnhandledException(Exception ex) {
		log.error("Unhandled exception", ex);
		return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred");
	}

	private ResponseEntity<ErrorResponse> buildResponse(HttpStatusCode statusCode, String message) {
		HttpStatus status = HttpStatus.resolve(statusCode.value());
		String error = status != null ? status.getReasonPhrase() : "HTTP " + statusCode.value();

		ErrorResponse response = ErrorResponse.builder()
				.timestamp(LocalDateTime.now())
				.status(statusCode.value())
				.error(error)
				.message(hasText(message) ? message : error)
				.build();

		return ResponseEntity.status(statusCode).body(response);
	}

	private HttpStatus mapFeignStatus(int status) {
		return switch (status) {
			case 400 -> HttpStatus.BAD_REQUEST;
			case 401 -> HttpStatus.UNAUTHORIZED;
			case 403 -> HttpStatus.FORBIDDEN;
			case 404 -> HttpStatus.NOT_FOUND;
			case 409 -> HttpStatus.CONFLICT;
			default -> HttpStatus.BAD_GATEWAY;
		};
	}

	private boolean hasText(String value) {
		return value != null && !value.isBlank();
	}

}
