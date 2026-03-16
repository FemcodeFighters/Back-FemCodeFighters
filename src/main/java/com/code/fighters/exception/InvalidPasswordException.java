package com.code.fighters.exception;

public class InvalidPasswordException extends RuntimeException {

     public InvalidPasswordException() {
        super("La contraseña actual es incorrecta");
    }
}
