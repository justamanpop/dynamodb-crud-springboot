package com.example.restservice.exception;

import org.springframework.http.HttpStatus;

public class InternalErrorException extends RuntimeException {

    public InternalErrorException(String message) {
        super(message);
    }

    public HttpStatus getStatus() {
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }
}