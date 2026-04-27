package com.curso.animalitos.controller.api.models;

import com.curso.animalitos.common.validation.EdadValida;
import com.curso.animalitos.common.validation.EspecieValida;
import com.curso.animalitos.common.validation.NombreValido;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "CrearAnimalRequest", description = "Datos para crear un animal nuevo.")
public record CrearAnimalRequestDTO(
        @Schema(description = "Nombre unico del animal (2-60 caracteres).", example = "Lucas", requiredMode = Schema.RequiredMode.REQUIRED)
        @NombreValido String nombre,


        @Schema(description = "Especie del animal.", example = "GATO", requiredMode = Schema.RequiredMode.REQUIRED,
                allowableValues = {"PERRO", "GATO", "HAMSTER", "CONEJO", "LORO", "PEZ", "TORTUGA"})
        @EspecieValida String especie,
        
        
        @Schema(description = "Edad en años (0-100).", example = "3", requiredMode = Schema.RequiredMode.REQUIRED, minimum = "0", maximum = "100")
        @EdadValida Integer edad) {
}
