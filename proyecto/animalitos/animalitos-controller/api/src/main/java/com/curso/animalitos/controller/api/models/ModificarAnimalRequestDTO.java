package com.curso.animalitos.controller.api.models;

import com.curso.animalitos.common.validation.EdadValida;
import com.curso.animalitos.common.validation.EspecieValida;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "ModificarAnimalRequest", description = "Datos para modificar un animal. El nombre NO se puede cambiar.")
public record ModificarAnimalRequestDTO(
        @Schema(description = "Nueva especie del animal.", example = "PERRO", requiredMode = Schema.RequiredMode.REQUIRED,
                allowableValues = {"PERRO", "GATO", "HAMSTER", "CONEJO", "LORO", "PEZ", "TORTUGA"})
        @EspecieValida String especie,
        @Schema(description = "Nueva edad en años (0-100).", example = "7", requiredMode = Schema.RequiredMode.REQUIRED, minimum = "0", maximum = "100")
        @EdadValida Integer edad) {
}
