package com.example.restservice.exception;

import com.example.restservice.model.ErrorResponseModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice("com.example.restservice.controller.todo")
public class TodoControllerExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity handleTodoNotFound(NotFoundException ex) {
        var error = new ErrorResponseModel(ex.getStatus(),ex.getMessage());
        return new ResponseEntity(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(BadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity handleTodoBadRequest(BadRequestException ex) {
        var error = new ErrorResponseModel(ex.getStatus(),ex.getMessage());
        return new ResponseEntity(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity handleMissingParams(MissingServletRequestParameterException ex) {
        String name = ex.getParameterName();
        var error = new ErrorResponseModel(HttpStatus.BAD_REQUEST,name + " must be passed");
        return new ResponseEntity(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(org.springframework.web.method.annotation.MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity handleInvalidId(org.springframework.web.method.annotation.MethodArgumentTypeMismatchException ex) {
        String property = ex.getName();
        var error = new ErrorResponseModel(HttpStatus.BAD_REQUEST,property + " must be a valid UUID");
        return new ResponseEntity(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InternalErrorException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity handleInvalidId(InternalErrorException ex) {
        var error = new ErrorResponseModel(ex.getStatus(),ex.getMessage());
        return new ResponseEntity(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
