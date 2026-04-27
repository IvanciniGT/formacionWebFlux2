package com.curso.animales.service.api.exceptions;

import lombok.Getter;

public class AnimalitosServiceException extends RuntimeException {

    @Getter
    private final TipoDeError tipoDeError;

    public AnimalitosServiceException(String message, Throwable cause, TipoDeError tipoDeError) {
        super(message, cause);
        this.tipoDeError = tipoDeError;
    }

    private static enum TipoDeError {
        ANIMAL_NO_ENCONTRADO,
        ANIMAL_YA_EXISTE,
        DATOS_INVALIDOS,
        ERROR_DEL_SERVIDOR,
        ERROR_DESCONOCIDO
    }
}
