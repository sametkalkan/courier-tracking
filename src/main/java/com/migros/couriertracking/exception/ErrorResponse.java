package com.migros.couriertracking.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Data
public class ErrorResponse {

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss")
    private LocalDateTime timestamp;
    private HttpStatus status;
    private String errorMessage;

    public ErrorResponse(LocalDateTime timestamp, String errorMessage) {
        this.timestamp = timestamp;
        this.errorMessage = errorMessage;
    }

    @Override
    public String toString() {
        return "ErrorResponse{" +
                "timestamp=" + timestamp +
                ", status=" + status +
                ", errorMessage='" + errorMessage + '\'' +
                '}';
    }
}