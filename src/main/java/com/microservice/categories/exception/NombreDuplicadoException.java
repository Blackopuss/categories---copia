package com.microservice.categories.exception;

public class NombreDuplicadoException extends RuntimeException {

    public NombreDuplicadoException(String mensaje) {
        super(mensaje);
    }
}
