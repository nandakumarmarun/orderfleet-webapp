package com.orderfleet.webapp.web.rest.Features.KilometerCalculations.Exception;

import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

public class ErrorReponse extends Throwable{
    private String message;
    private HttpStatus status;
    private String path;
    private LocalDateTime timestamp;

    public ErrorReponse(String message, HttpStatus status, String path) {
        this.message = message;
        this.status = status;
        this.path = path;
        this.timestamp=LocalDateTime.now();
    }



}
