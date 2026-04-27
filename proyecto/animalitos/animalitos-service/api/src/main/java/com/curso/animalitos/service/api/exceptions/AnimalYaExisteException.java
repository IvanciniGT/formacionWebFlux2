package com.curso.animalitos.service.api.exceptions;

public class AnimalYaExisteException extends AnimalitosServiceException {
    public AnimalYaExisteException(String nombre) {
        super("Ya existe un animal con nombre: " + nombre, TipoDeError.ANIMAL_YA_EXISTE);
    }
}
