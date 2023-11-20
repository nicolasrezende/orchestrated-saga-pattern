package br.com.orchestrated.pattern.ordervalidationservice.exception;

public class ValidationException extends RuntimeException {

    public ValidationException(String message) {
        super(message);
    }
}
