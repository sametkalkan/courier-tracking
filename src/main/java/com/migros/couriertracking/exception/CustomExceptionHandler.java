package com.migros.couriertracking.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
public class CustomExceptionHandler extends ResponseEntityExceptionHandler {
	private static final Logger LOG = LoggerFactory.getLogger(CustomExceptionHandler.class);

	@ExceptionHandler(Exception.class)
	public final ResponseEntity<Object> handleAllExceptions(Exception ex, WebRequest request) {
		List<String> details = new ArrayList<>();
		details.add(ex.getLocalizedMessage());
		ErrorResponse error = new ErrorResponse(LocalDateTime.now(), "Server Error: " + details);
		LOG.error("Exception occurred: {}", error);
		return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler(RecordNotFoundException.class)
	public ResponseEntity<?> handleRecordNotFoundException(RecordNotFoundException ex, WebRequest request) {
		ErrorResponse error = new ErrorResponse(LocalDateTime.now(), ex.getLocalizedMessage());
		error.setStatus(HttpStatus.NOT_FOUND);
		LOG.error("RecordNotFoundException occurred: {}", error);
		return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
	}
}