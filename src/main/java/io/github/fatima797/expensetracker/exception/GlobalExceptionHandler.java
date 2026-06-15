package io.github.fatima797.expensetracker.exception;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;

import io.github.fatima797.expensetracker.dto.ErrorResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(DuplicateEmailException.class)
	public ResponseEntity<ErrorResponse> duplicateEmailExceptionHandler(DuplicateEmailException ex) {

		return buildErrorResponse(HttpStatus.CONFLICT, "email", ex.getMessage());
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorResponse> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {

		Map<String, String> errors = new LinkedHashMap<>();
		ex.getBindingResult().getFieldErrors()
				.forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));

		ErrorResponse response = new ErrorResponse(
				HttpStatus.BAD_REQUEST.value(),
				errors,
				LocalDateTime.now().toString());

		return ResponseEntity.badRequest().body(response);
	}

	@ExceptionHandler(BadCredentialsException.class)
	public ResponseEntity<ErrorResponse> handleBadCredentials(BadCredentialsException ex) {

		return buildErrorResponse(HttpStatus.UNAUTHORIZED, "credentials", "Invalid email or password");
	}

	@ExceptionHandler(UserNotFoundException.class)
	public ResponseEntity<ErrorResponse> handleUserNotFound(UserNotFoundException ex) {
		return buildErrorResponse(HttpStatus.NOT_FOUND, "user", ex.getMessage());
	}

	private ResponseEntity<ErrorResponse> buildErrorResponse(HttpStatus status, String key, String message) {
		ErrorResponse response = new ErrorResponse(
				status.value(),
				Map.of(key, message),
				LocalDateTime.now().toString());
		return ResponseEntity.status(status).body(response);
	}

	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<ErrorResponse> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
		if (ex.getCause() instanceof InvalidFormatException invalidFormatEx) {

			String fieldName = invalidFormatEx.getPath().stream()
					.map(JsonMappingException.Reference::getFieldName)
					.filter(Objects::nonNull)
					.collect(Collectors.joining("."));

			String invalidValue = String.valueOf(invalidFormatEx.getValue());
			Class<?> targetType = invalidFormatEx.getTargetType();

			String message;
			if (targetType != null && targetType.isEnum()) {
				String acceptedValues = Arrays.stream(targetType.getEnumConstants())
						.map(Object::toString)
						.collect(Collectors.joining(", "));
				message = String.format("Invalid value '%s' for field '%s'. Accepted values are: %s",
						invalidValue, fieldName, acceptedValues);
			} else {
				message = String.format("Invalid value '%s' for field '%s'", invalidValue, fieldName);
			}

			return buildErrorResponse(HttpStatus.BAD_REQUEST, fieldName.isEmpty() ? "request" : fieldName, message);
		}
		return buildErrorResponse(HttpStatus.BAD_REQUEST, "request", "Invalid or malformed request body");
	}
}
