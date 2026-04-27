package com.curso.animalitos.service.api.exceptions;

public class RepositorioInaccesibleException extends AnimalitosServiceException {
    public RepositorioInaccesibleException(String mensaje, Throwable cause) {
        super(mensaje, cause, TipoDeError.REPOSITORIO_INACCESIBLE);
    }
}
