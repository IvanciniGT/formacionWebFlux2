package com.curso.animalitos.repository.api;

import com.curso.animalitos.domain.Animal;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

import java.util.List;
import java.util.Optional;

/**
 * Contrato CRUD para la capa de persistencia de animales.
 *
 * Habla SOLO el lenguaje del dominio: el unico modelo que cruza la frontera
 * de este puerto es {@link Animal}. La capa de persistencia depende del
 * dominio (nunca al reves).
 *
 * Las implementaciones lanzan {@link com.curso.animalitos.repository.api.exceptions.RepositorioException}
 * cuando el animal no existe o cuando hay errores tecnicos.
 */
public interface AnimalitosRepository {

    Optional<Animal> findById(@NotBlank String id);

    List<Animal> findAll();

    /**
     * Crea un nuevo animal. El campo {@code id} del parametro se ignora;
     * la persistencia lo genera y lo devuelve relleno en el resultado.
     */
    Animal create(@Valid Animal datos);

    /**
     * Modifica especie y edad del animal con el id indicado. El nombre
     * NO se modifica (politica de la capa de persistencia), por lo que
     * el {@code Animal} entrante NO se valida con {@code @Valid} (el
     * nombre puede llegar a null por contrato).
     */
    Animal update(@NotBlank String id, Animal datos);

    Optional<Animal> deleteById(@NotBlank String id);
}
