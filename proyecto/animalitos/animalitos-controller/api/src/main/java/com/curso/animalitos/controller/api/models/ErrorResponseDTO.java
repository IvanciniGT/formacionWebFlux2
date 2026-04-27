package com.curso.animalitos.controller.api.models;

import com.curso.animalitos.common.dto.ValidationErrorDTO;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.List;

@Schema(name = "ErrorResponse", description = "Estructura uniforme de error devuelta por la API.")
public record ErrorResponseDTO(
        @Schema(description = "Instante en que se produjo el error (ISO-8601).", example = "2026-04-27T16:00:00Z")
        Instant timestamp,
        @Schema(description = "Codigo HTTP.", example = "404")
        int status,
        @Schema(description = "Identificador del tipo de error.", example = "ANIMAL_NO_ENCONTRADO")
        String error,
        @Schema(description = "Mensaje legible para el cliente.", example = "No existe animal con id ...")
        String mensaje,
        @Schema(description = "Detalles de validacion campo a campo (puede ser vacio).")
        List<ValidationErrorDTO> detalles) {

    public static ErrorResponseDTO of(int status, String error, String mensaje) {
        return new ErrorResponseDTO(Instant.now(), status, error, mensaje, List.of());
    }

    public static ErrorResponseDTO of(int status, String error, String mensaje, List<ValidationErrorDTO> detalles) {
        return new ErrorResponseDTO(Instant.now(), status, error, mensaje, detalles);
    }
}
