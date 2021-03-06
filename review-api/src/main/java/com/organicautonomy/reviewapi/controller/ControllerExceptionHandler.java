package com.organicautonomy.reviewapi.controller;

import com.organicautonomy.reviewapi.exception.ErrorDetails;
import com.organicautonomy.reviewapi.exception.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestControllerAdvice
public class ControllerExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Error> handleResourceNotFoundException(ResourceNotFoundException e, WebRequest request) {
        ErrorDetails errorDetails = new ErrorDetails(new Date(), e.getMessage(), request.getDescription(false));
        return new ResponseEntity(errorDetails, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Error> handleIllegalArgumentException(IllegalArgumentException e, WebRequest request) {
        ErrorDetails errorDetails = new ErrorDetails(new Date(), e.getMessage(), request.getDescription(false));
        return new ResponseEntity(errorDetails, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Error> handleValidationException(MethodArgumentNotValidException e, WebRequest request) {
        Date date = new Date();
        BindingResult result  = e.getBindingResult();
        List<FieldError> fieldErrors = e.getFieldErrors();
        List<ErrorDetails> errorDetails = new ArrayList<>();

        for (FieldError fieldError : fieldErrors) {
            ErrorDetails error = new ErrorDetails(date, fieldError.toString(), request.getDescription(false));
            errorDetails.add(error);
        }

        return new ResponseEntity(errorDetails, HttpStatus.UNPROCESSABLE_ENTITY);
    }
}
