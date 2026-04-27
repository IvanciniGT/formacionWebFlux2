package com.curso.animalitos.service.api.exceptions;

public class DatosInvalidosException extends AnimalitosServiceException {
    public DatosInvalidosException(String mensaje) {
        super(mensaje, TipoDeError.DATOS_INVALIDOS);
    }
}
