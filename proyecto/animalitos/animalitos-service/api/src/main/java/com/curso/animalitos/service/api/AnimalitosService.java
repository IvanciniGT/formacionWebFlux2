package com.curso.animalitos.service.api;

import com.curso.animalitos.service.api.models.AnimalDTO;
import com.curso.animalitos.service.api.models.CrearAnimalDTO;
import com.curso.animalitos.service.api.models.ModificarAnimalDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

import java.util.List;
import java.util.Optional;

/**
 * Contrato de la capa de servicio.
 *
 * Lanza:
 *  - AnimalNoEncontradoException     en update si no existe el id
 *  - AnimalYaExisteException         en create si el nombre ya esta usado
 *  - DatosInvalidosException         en create/update si los datos no son validos
 *  - RepositorioInaccesibleException si el repositorio falla por motivos tecnicos
 */
public interface AnimalitosService {

    Optional<AnimalDTO> getAnimal(@NotBlank String id);

    List<AnimalDTO> getAllAnimales();

    AnimalDTO createAnimal(@Valid CrearAnimalDTO datos);

    AnimalDTO updateAnimal(@NotBlank String id, @Valid ModificarAnimalDTO datos);

    Optional<AnimalDTO> deleteAnimal(@NotBlank String id);
}
