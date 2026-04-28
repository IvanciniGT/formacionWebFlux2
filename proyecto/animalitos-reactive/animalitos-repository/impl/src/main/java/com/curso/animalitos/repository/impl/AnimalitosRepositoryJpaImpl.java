package com.curso.animalitos.repository.impl;

import com.curso.animalitos.domain.Animal;
import com.curso.animalitos.repository.api.AnimalitosRepository;
import com.curso.animalitos.repository.api.exceptions.RepositorioException;
import com.curso.animalitos.repository.impl.entities.AnimalEntity;
import com.curso.animalitos.repository.impl.mappers.AnimalRepositoryMapper;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;


import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Implementacion JPA del repositorio.
 * Inyeccion por constructor (dia2): nada de @Autowired en campos.
 *
 * Hacia fuera exponemos un identificador publico (UUID en formato String).
 * Internamente la PK es un Long autogenerado por la base de datos.
 *
 * {@code @Validated} activa method-level validation: las anotaciones
 * {@code @Valid Animal} / {@code @NotBlank String id} del contrato se
 * comprueban via proxy AOP. Asi este repo es una barrera adicional aunque
 * un futuro caller se salte la capa de servicio.
 */
@Component
@Validated
public class AnimalitosRepositoryJpaImpl implements AnimalitosRepository {

    private final AnimalEntityCrudRepository internalRepository;
    private final AnimalRepositoryMapper mapper;

    public AnimalitosRepositoryJpaImpl(AnimalEntityCrudRepository internalRepository, AnimalRepositoryMapper mapper) {
        this.internalRepository = internalRepository;
        this.mapper = mapper;
    }

    @Override
    public Mono<Animal> findById(String id) {
        return internalRepository.findByPublicId(id)
                                 .map(mapper::entityToDomain);
    }

    @Override
    public Flux<Animal> findAll() {
        return internalRepository.findAll()
                                 .map(mapper::entityToDomain);
    }

    @Override
    public Mono<Animal> create(Animal datos) {
        AnimalEntity entity = mapper.domainToEntity(datos);
        return internalRepository.save(entity)
                                 .map(mapper::entityToDomain);
    }

    @Override
    public Mono<Animal> update(String id, Animal nuevosDatos) {
        return internalRepository.findByPublicId(id)          // Potencial AnimalEntity

                                .switchIfEmpty(Mono.error(() -> new RepositorioException(    // Si no viene.. cuando sea que venga, que se cambie por un error
                                                                     "No existe animal con id " + id,
                                                                    RepositorioException.TipoDeError.ANIMAL_NO_ENCONTRADO)))
                                .map(animalPersistido -> {                                                   // Si viene:
                                                                mapper.aplicarModificaciones(nuevosDatos, animalPersistido);                     // Le moddifico los datos.
                                                                return animalPersistido;
                                                       })
                                .flatMap(internalRepository::save)
                                .map(mapper::entityToDomain);

                                // reactive STREAMS
                                // STREAM: Flujo  de procesamiento de datos . 
                                //.        Esto es purita programación funcional MAP-REDUCE
    }

    @Override
    public Mono<Animal> deleteById(String id) {
        return internalRepository.findByPublicId(id)
                                 .flatMap(e -> internalRepository.delete(e).thenReturn(mapper.entityToDomain(e)));
    }
}
