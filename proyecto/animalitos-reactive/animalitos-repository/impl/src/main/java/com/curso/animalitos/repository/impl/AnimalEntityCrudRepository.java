package com.curso.animalitos.repository.impl;

import com.curso.animalitos.repository.impl.entities.AnimalEntity;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;


/**
 * Spring Data JPA. Detalle interno de la implementacion.
 */
@Repository
// OYE: Spring, Crea tu una clase que implemente esta interfaz.
// Ponle los típicos métodos de JPA (findById, findAll, save, deleteById...) y haz que trabajen con AnimalEntity y Long (id).
// Y los que yo te haya definido extras.
// Además, como @Repository hereda de @Component:
// OYE: Spring, crea una sola intancia de esa clase que TU vas a crear.
// Y Cuando alguien me pida un AnimalEntityJpaRepository, dásela a esa persona.
public interface AnimalEntityCrudRepository extends ReactiveCrudRepository<AnimalEntity, Long> {

    // Buscamos por el id publico (UUID) que es el que se expone hacia fuera.
    Mono<AnimalEntity> findByPublicId(String publicId);

    // podré pedirle métodos adicionales:
    Flux<AnimalEntity> findByEspecie(String especie);
    //. Y Además ordenados por edad:
    Flux<AnimalEntity> findByEspecieOrderByEdadAsc(String especie);
}
