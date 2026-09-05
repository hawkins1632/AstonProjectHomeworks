package org.example.exception;

public class UserNotFoundException extends RuntimeException{
    public UserNotFoundException(Long id) {
        super(String.format("Failed to find user with id: %d", id));
    }
}
