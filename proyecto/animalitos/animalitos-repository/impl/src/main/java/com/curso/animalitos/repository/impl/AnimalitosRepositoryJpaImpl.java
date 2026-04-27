package com.curso.animalitos.repository.impl;

import com.curso.animalitos.domain.Animal;
import com.curso.animalitos.repository.api.AnimalitosRepository;
import com.curso.animalitos.repository.api.exceptions.RepositorioException;
import com.curso.animalitos.repository.impl.entities.AnimalEntity;
import com.curso.animalitos.repository.impl.mappers.AnimalRepositoryMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * Implementacion JPA del repositorio.
 * Inyeccion por constructor (dia2): nada de @Autowired en campos.
 *
 * Hacia fuera exponemos un identificador publico (UUID en formato String).
 * Internamente la PK es un Long autogenerado por la base de datos.
 */
@Component
public class AnimalitosRepositoryJpaImpl implements AnimalitosRepository {

    private final AnimalEntityJpaRepository jpa;
    private final AnimalRepositoryMapper mapper;

    public AnimalitosRepositoryJpaImpl(AnimalEntityJpaRepository jpa, AnimalRepositoryMapper mapper) {
        this.jpa = jpa;
        this.mapper = mapper;
    }

    @Override
    public Optional<Animal> findById(String id) {
        return jpa.findByPublicId(id).map(mapper::entityToDomain);
    }

    @Override
    public List<Animal> findAll() {
        return jpa.findAll().stream().map(mapper::entityToDomain).toList();
    }

    @Override
    public Animal create(Animal datos) {
        AnimalEntity entity = mapper.domainToEntity(datos);
        // El publicId ya viene inicializado por defecto (UUID) en el entity.
        // El id (PK Long) lo genera la base de datos.
        AnimalEntity guardado = jpa.save(entity);
        return mapper.entityToDomain(guardado);
    }

    @Override
    public Animal update(String id, Animal datos) {
        AnimalEntity entity = jpa.findByPublicId(id)
                .orElseThrow(() -> new RepositorioException(
                        "No existe animal con id " + id,
                        RepositorioException.TipoDeError.ANIMAL_NO_ENCONTRADO));
        mapper.aplicarModificaciones(datos, entity);
        return mapper.entityToDomain(jpa.save(entity));
    }

    @Override
    public Optional<Animal> deleteById(String id) {
        Optional<AnimalEntity> entity = jpa.findByPublicId(id);
        entity.ifPresent(jpa::delete);
        return entity.map(mapper::entityToDomain);
    }
}
