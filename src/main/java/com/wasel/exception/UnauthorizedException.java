package com.wasel.exception;

/**
 * Custom exception thrown when a user attempts to access a resource
 * without having the required permissions/authorization
 * Extends RuntimeException for unchecked exception handling
 * Automatically triggers 403 Forbidden response when handled by exception handler
 */
public class UnauthorizedException extends RuntimeException {

    /**
     * Constructs a new UnauthorizedException with the specified detail message
     *
     * @param message the detail message explaining why access is denied
     */
    public UnauthorizedException(String message) {
        super(message);
    }
}