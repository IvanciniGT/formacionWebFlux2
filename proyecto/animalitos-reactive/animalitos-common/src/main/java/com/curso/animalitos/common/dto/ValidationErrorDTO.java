package com.curso.animalitos.common.dto;

import java.util.List;

/**
 * Estructura comun de error para respuestas de la API y reportes de validacion.
 */
public record ValidationErrorDTO(String campo, String mensaje) {

    public static ValidationErrorDTO of(String campo, String mensaje) {
        return new ValidationErrorDTO(campo, mensaje);
    }

    public static List<ValidationErrorDTO> single(String campo, String mensaje) {
        return List.of(of(campo, mensaje));
    }
}
