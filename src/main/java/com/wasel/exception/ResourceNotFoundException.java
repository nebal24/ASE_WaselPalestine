package com.wasel.exception;

/**
 * Custom exception thrown when a requested resource is not found in the database
 * Extends RuntimeException so it can be used without explicit try-catch
 * Automatically triggers 404 Not Found response when handled by exception handler
 */
public class ResourceNotFoundException extends RuntimeException {

    /**
     * Constructs a new ResourceNotFoundException with the specified detail message
     *
     * @param message the detail message explaining which resource wasn't found
     *
     */
    public ResourceNotFoundException(String message) {
        super(message);
    }
}