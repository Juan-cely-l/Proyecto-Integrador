package edu.eci.proyecto.exception;

import jakarta.validation.constraints.Email;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.net.URI;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 1. Manejo cuando un recurso no existe -> 404 NOT FOUND
    @ExceptionHandler(UserNotFoundException.class)
    public ProblemDetail handleUserNotFound(UserNotFoundException e){
        ProblemDetail problem= ProblemDetail
                .forStatusAndDetail(HttpStatus.NOT_FOUND,e.getMessage());
        problem.setTitle("User not found");
        problem.setType(  URI.create("https://api.proyecto.eci.edu/errors/not-found"));
        problem.setProperty("timestamp", Instant.now());
        return problem;
    }

    // 2. Manejo cuando falla @Valid en un DTO -> 400 BAD REQUEST con lista de campos malos
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidationErrors(MethodArgumentNotValidException e){
        ProblemDetail problem=ProblemDetail.
                forStatusAndDetail(HttpStatus.BAD_REQUEST,"Invalid Data Send");
        problem.setTitle("Validation Error");
        problem.setType(URI.create("https://api.proyecto.eci.edu/errors/bad-request"));
        Map<String,String>fieldErrors=new HashMap<>();
        for(FieldError error:e.getBindingResult().getFieldErrors()){
                fieldErrors.put(error.getField(),error.getDefaultMessage());
        }
        problem.setProperty("Invalid Fields",fieldErrors);
        problem.setProperty("timestamp", Instant.now());
        return problem;
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ProblemDetail handleBadCredentials(BadCredentialsException e){
        ProblemDetail problem =ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED,"Invalid Email or Password");
        problem.setTitle("Bad Credentials");
        problem.setType(URI.create("https://api.proyecto.eci.edu/errors/authentication"));
        problem.setProperty("timestamp",Instant.now());
        return problem;
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ProblemDetail handleTypeMismatch(MethodArgumentTypeMismatchException e){
        ProblemDetail problem =ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST,"Invalid Parameter Format");
        problem.setTitle("Type Mismatch");
        problem.setType(URI.create("https://api.proyecto.eci.edu/errors/bad-request"));
        problem.setProperty("timestamp", Instant.now());
        problem.setProperty("parameter", e.getName());
        return problem;
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ProblemDetail handleMalformedJson(HttpMessageNotReadableException e){
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST,"Request body contains invalid JSON");
        problem.setTitle("Malformed JSON");
        problem.setType(URI.create("https://api.proyecto.eci.edu/errors/bad-request"));
        problem.setProperty("timestamp", Instant.now());
        return problem;
    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ProblemDetail handleEmailAlreadyExists(EmailAlreadyExistsException e){
        ProblemDetail problem =ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT,e.getMessage());
        problem.setTitle("Email Already Exists");
        problem.setType(URI.create("https://api.proyecto.eci.edu/errors/conflict"));
        problem.setProperty("timestamp", Instant.now());
        return problem;
    }
}
