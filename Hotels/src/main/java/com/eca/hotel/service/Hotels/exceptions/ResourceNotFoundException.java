package com.eca.hotel.service.Hotels.exceptions;

public class ResourceNotFoundException extends RuntimeException{

    public ResourceNotFoundException() {
        super("Resource not found!");
    }

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
