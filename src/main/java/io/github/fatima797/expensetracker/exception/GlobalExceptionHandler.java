package io.github.fatima797.expensetracker.exception;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import io.github.fatima797.expensetracker.dto.ErrorResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(DuplicateEmailException.class)
	public ResponseEntity<ErrorResponse> duplicateEmailExceptionHandler(DuplicateEmailException ex) {

		ErrorResponse exceptionResponse = new ErrorResponse(
				HttpStatus.CONFLICT.value(),
				Map.of("email", ex.getMessage()),
				LocalDateTime.now().toString()
				);

		return ResponseEntity.status(HttpStatus.CONFLICT).body(exceptionResponse);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ResponseEntity<ErrorResponse> handleErrors(MethodArgumentNotValidException ex){
	
		Map<String, String> errors = new LinkedHashMap<>();
		ex.getBindingResult().getFieldErrors()
		.forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));

		ErrorResponse response = new ErrorResponse(
				HttpStatus.BAD_REQUEST.value(), 
				errors, 
				LocalDateTime.now().toString()
				);

		return ResponseEntity.badRequest().body(response);
	}
	
	@ExceptionHandler(BadCredentialsException.class)
	public ResponseEntity<ErrorResponse> handleBadCredentials(BadCredentialsException ex) {
		
		ErrorResponse response = new ErrorResponse(
		        HttpStatus.UNAUTHORIZED.value(),
		        Map.of("credentials", "Invalid email or password"),
		        LocalDateTime.now().toString()
		    );
		
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
	}
	
	@ExceptionHandler(UserNotFoundException.class)
	public ResponseEntity<ErrorResponse> handleUserNotFound(UserNotFoundException ex) {
	
		ErrorResponse response = new ErrorResponse(
		        HttpStatus.NOT_FOUND.value(),
		        Map.of("user", ex.getMessage()),
		        LocalDateTime.now().toString()
		    );
		    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
	}
}
