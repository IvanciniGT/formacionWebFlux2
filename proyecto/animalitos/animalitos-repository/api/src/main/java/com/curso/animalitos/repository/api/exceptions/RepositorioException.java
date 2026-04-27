package com.curso.animalitos.repository.api.exceptions;

import lombok.Getter;

/**
 * Excepcion de la capa de persistencia.
 * Encapsula tanto errores tecnicos como semanticos (no encontrado, ya existe).
 */
@Getter
public class RepositorioException extends RuntimeException {

    public enum TipoDeError {
        ANIMAL_NO_ENCONTRADO,
        ANIMAL_YA_EXISTE,
        ERROR_TECNICO
    }

    private final TipoDeError tipoDeError;

    public RepositorioException(String message, TipoDeError tipoDeError) {
        super(message);
        this.tipoDeError = tipoDeError;
    }

    public RepositorioException(String message, Throwable cause, TipoDeError tipoDeError) {
        super(message, cause);
        this.tipoDeError = tipoDeError;
    }
}
