package com.example.restservice.model;

import org.springframework.http.HttpStatus;

public class ErrorResponseModel {
    public HttpStatus status;
    public String error;

    public ErrorResponseModel(HttpStatus status, String error) {
        this.status = status;
        this.error = error;
    }
}
