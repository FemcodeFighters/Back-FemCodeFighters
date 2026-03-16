package com.code.fighters.exception;

public class PlayerNotFoundException extends RuntimeException {

    public PlayerNotFoundException(String email) {
        super("Personaje no encontrado para el usuario: " + email);
    }
}
