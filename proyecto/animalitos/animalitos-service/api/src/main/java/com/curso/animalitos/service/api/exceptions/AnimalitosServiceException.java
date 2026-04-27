package com.curso.animalitos.service.api.exceptions;

import lombok.Getter;

/**
 * Excepcion base de la capa de servicio. Cada subclase representa una situacion semantica.
 */
@Getter
public abstract class AnimalitosServiceException extends RuntimeException {

    public enum TipoDeError {
        ANIMAL_NO_ENCONTRADO,
        ANIMAL_YA_EXISTE,
        DATOS_INVALIDOS,
        REPOSITORIO_INACCESIBLE,
        ERROR_DESCONOCIDO
    }

    private final TipoDeError tipoDeError;

    protected AnimalitosServiceException(String message, TipoDeError tipoDeError) {
        super(message);
        this.tipoDeError = tipoDeError;
    }

    protected AnimalitosServiceException(String message, Throwable cause, TipoDeError tipoDeError) {
        super(message, cause);
        this.tipoDeError = tipoDeError;
    }
}
