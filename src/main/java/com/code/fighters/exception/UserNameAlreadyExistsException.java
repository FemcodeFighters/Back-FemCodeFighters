package com.code.fighters.exception;

public class UserNameAlreadyExistsException extends RuntimeException {

    public UserNameAlreadyExistsException(String username) {
        super("El alias ya está en uso: " + username);
    }
}
