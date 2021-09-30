package com.migros.couriertracking.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Record Not found")
public class RecordNotFoundException extends RuntimeException {

	public RecordNotFoundException(String exception) {
		super(exception);
	}
}
