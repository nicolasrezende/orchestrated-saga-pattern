package br.com.orchestrated.pattern.orchestratorservice.exception;

public class ValidationException extends RuntimeException {

    public ValidationException(String message) {
        super(message);
    }
}
