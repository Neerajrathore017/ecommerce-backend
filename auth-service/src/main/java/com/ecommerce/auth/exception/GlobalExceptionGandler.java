package com.ecommerce.auth.exception;

import java.util.Date;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice 
public class GlobalExceptionGandler {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorResponse> handleUserException(CustomException ex){
        ErrorResponse errorResponse=new ErrorResponse(new Date(),ex.getMessage(),ex.getStatus().value());
        return ResponseEntity.status(ex.getStatus()).body(errorResponse);
    }
    
}
