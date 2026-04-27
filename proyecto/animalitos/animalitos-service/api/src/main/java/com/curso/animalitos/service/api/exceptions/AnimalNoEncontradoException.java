package com.curso.animalitos.service.api.exceptions;

public class AnimalNoEncontradoException extends AnimalitosServiceException {
    public AnimalNoEncontradoException(String id) {
        super("Animal no encontrado: " + id, TipoDeError.ANIMAL_NO_ENCONTRADO);
    }
}
