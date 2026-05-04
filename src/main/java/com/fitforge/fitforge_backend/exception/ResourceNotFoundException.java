package com.fitforge.fitforge_backend.exception;

// Thrown when we can't find a DB record by ID
// Extends RuntimeException so we don't need to declare it in method signatures
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}