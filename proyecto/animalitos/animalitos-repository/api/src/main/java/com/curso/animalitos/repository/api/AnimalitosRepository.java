package com.curso.animalitos.repository.api;

import com.curso.animalitos.domain.Animal;

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

    Optional<Animal> findById(String id);

    List<Animal> findAll();

    /**
     * Crea un nuevo animal. El campo {@code id} del parametro se ignora;
     * la persistencia lo genera y lo devuelve relleno en el resultado.
     */
    Animal create(Animal datos);

    /**
     * Modifica especie y edad del animal con el id indicado. El nombre
     * NO se modifica (politica de la capa de persistencia).
     */
    Animal update(String id, Animal datos);

    Optional<Animal> deleteById(String id);
}
