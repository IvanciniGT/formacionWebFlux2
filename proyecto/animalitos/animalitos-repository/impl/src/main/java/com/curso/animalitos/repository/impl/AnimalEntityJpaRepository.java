package com.curso.animalitos.repository.impl;

import com.curso.animalitos.repository.impl.entities.AnimalEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

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
public interface AnimalEntityJpaRepository extends JpaRepository<AnimalEntity, Long> {

    // Buscamos por el id publico (UUID) que es el que se expone hacia fuera.
    Optional<AnimalEntity> findByPublicId(String publicId);

    // podré pedirle métodos adicionales:
    List<AnimalEntity> findByEspecie(String especie);
    //. Y Además ordenados por edad:
    List<AnimalEntity> findByEspecieOrderByEdadAsc(String especie);
}
