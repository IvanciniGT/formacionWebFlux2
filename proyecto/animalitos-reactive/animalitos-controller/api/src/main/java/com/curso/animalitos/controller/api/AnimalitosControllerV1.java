package com.curso.animalitos.controller.api;

import com.curso.animalitos.controller.api.models.AnimalRestDTO;
import com.curso.animalitos.controller.api.models.CrearAnimalRequestDTO;
import com.curso.animalitos.controller.api.models.ErrorResponseDTO;
import com.curso.animalitos.controller.api.models.ModificarAnimalRequestDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Contrato REST v1 reactivo para Animales. Es el contrato PUBLICO de la
 * capa de controlador: se documenta con OpenAPI (springdoc) y se publica
 * como artefacto reutilizable.
 *
 * Tipos reactivos:
 *  - Mono<ResponseEntity<...>> para respuestas individuales (permite
 *    decidir el status code).
 *  - Flux<AnimalRestDTO>       para listados (status 200 implicito).
 */
@Tag(name = "Animalitos", description = "API CRUD de animalitos. Identificadores publicos en formato UUID.")
@RequestMapping("/api/v1/animalitos")
public interface AnimalitosControllerV1 {

    @Operation(
            summary = "Recuperar un animal por id",
            description = "Devuelve el animal cuyo identificador publico (UUID) coincide con el proporcionado."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Animal encontrado",
                    content = @Content(schema = @Schema(implementation = AnimalRestDTO.class))),
            @ApiResponse(responseCode = "404", description = "No existe animal con ese id",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @GetMapping("/{id}")
    Mono<ResponseEntity<AnimalRestDTO>> getAnimal(
            @Parameter(description = "Identificador publico (UUID) del animal", example = "5b3f1d2a-7c0d-4d8a-9b8e-2c6c1f0aafee")
            @PathVariable String id);

    @Operation(
            summary = "Listar todos los animales",
            description = "Devuelve la coleccion completa. Si no hay animales devuelve lista vacia."
    )
    @ApiResponse(responseCode = "200", description = "Coleccion de animales (puede ser vacia)")
    @GetMapping
    Flux<AnimalRestDTO> getAllAnimales();

    @Operation(
            summary = "Crear un animal",
            description = "Crea un animal nuevo. El nombre debe ser unico (case-insensitive)."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Animal creado",
                    content = @Content(schema = @Schema(implementation = AnimalRestDTO.class))),
            @ApiResponse(responseCode = "400", description = "Datos invalidos (validacion)",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "409", description = "Ya existe un animal con ese nombre",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @PostMapping
    Mono<ResponseEntity<AnimalRestDTO>> createAnimal(@RequestBody @Valid CrearAnimalRequestDTO datos);

    @Operation(
            summary = "Modificar un animal",
            description = "Modifica especie y edad. El nombre NO se puede modificar."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Animal modificado",
                    content = @Content(schema = @Schema(implementation = AnimalRestDTO.class))),
            @ApiResponse(responseCode = "400", description = "Datos invalidos (validacion)",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "No existe animal con ese id",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @PutMapping("/{id}")
    Mono<ResponseEntity<AnimalRestDTO>> updateAnimal(
            @Parameter(description = "Identificador publico (UUID) del animal a modificar")
            @PathVariable String id,
            @RequestBody @Valid ModificarAnimalRequestDTO datos);

    @Operation(
            summary = "Eliminar un animal",
            description = "Elimina el animal por su id publico y devuelve los datos del animal eliminado."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Animal eliminado",
                    content = @Content(schema = @Schema(implementation = AnimalRestDTO.class))),
            @ApiResponse(responseCode = "404", description = "No existe animal con ese id",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @DeleteMapping("/{id}")
    Mono<ResponseEntity<AnimalRestDTO>> deleteAnimal(
            @Parameter(description = "Identificador publico (UUID) del animal a eliminar")
            @PathVariable String id);
}
