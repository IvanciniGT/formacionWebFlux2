package com.curso.animalitos.service.api;

import com.curso.animalitos.service.api.models.AnimalDTO;
import com.curso.animalitos.service.api.models.CrearAnimalDTO;
import com.curso.animalitos.service.api.models.ModificarAnimalDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Contrato reactivo de la capa de servicio.
 *
 * Senales reactivas:
 *  - getAnimal:        Mono<AnimalDTO> vacio si no existe (Mono.empty()).
 *  - getAllAnimales:   Flux<AnimalDTO> (vacio si no hay datos).
 *  - createAnimal:     Mono<AnimalDTO> con el animal creado.
 *  - updateAnimal:     Mono<AnimalDTO> con el animal modificado.
 *  - deleteAnimal:     Mono<AnimalDTO> con el animal borrado, o vacio si no existe.
 *
 * Errores que se materializan en el Mono/Flux (Mono.error / Flux.error):
 *  - AnimalNoEncontradoException     en update si no existe el id
 *  - AnimalYaExisteException         en create si el nombre ya esta usado
 *  - DatosInvalidosException         en create/update si los datos no son validos
 *  - RepositorioInaccesibleException si el repositorio falla por motivos tecnicos
 */
public interface AnimalitosService {

    Mono<AnimalDTO> getAnimal(@NotBlank String id);

    Flux<AnimalDTO> getAllAnimales();

    Mono<AnimalDTO> createAnimal(@Valid CrearAnimalDTO datos);

    Mono<AnimalDTO> updateAnimal(@NotBlank String id, @Valid ModificarAnimalDTO datos);

    Mono<AnimalDTO> deleteAnimal(@NotBlank String id);
}
