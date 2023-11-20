package br.com.orchestrated.pattern.orderregisterservice.exeception;

public class ValidationException extends RuntimeException {

    public ValidationException(String message) {
        super(message);
    }
}
