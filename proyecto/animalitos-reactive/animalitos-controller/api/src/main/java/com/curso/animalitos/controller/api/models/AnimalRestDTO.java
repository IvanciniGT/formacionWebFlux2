package com.curso.animalitos.controller.api.models;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "Animal", description = "Animal tal y como lo expone la API.")
public record AnimalRestDTO(
        @Schema(description = "Identificador publico (UUID).", example = "5b3f1d2a-7c0d-4d8a-9b8e-2c6c1f0aafee")
        String id,
        @Schema(description = "Nombre del animal.", example = "Lucas")
        String nombre,
        @Schema(description = "Especie del animal.", example = "GATO",
                allowableValues = {"PERRO", "GATO", "HAMSTER", "CONEJO", "LORO", "PEZ", "TORTUGA"})
        String especie,
        @Schema(description = "Edad en años.", example = "3", minimum = "0", maximum = "100")
        int edad) {
}
