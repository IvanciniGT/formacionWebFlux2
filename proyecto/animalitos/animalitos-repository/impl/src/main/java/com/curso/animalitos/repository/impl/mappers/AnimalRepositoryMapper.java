package com.curso.animalitos.repository.impl.mappers;

import com.curso.animalitos.domain.Animal;
import com.curso.animalitos.repository.impl.entities.AnimalEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

/**
 * Mapper entre el modelo de DOMINIO ({@link Animal}) y el modelo de
 * persistencia ({@link AnimalEntity}). El dominio no sabe nada de JPA;
 * la traduccion ocurre exclusivamente aqui.
 */
@Mapper(componentModel = "spring")
public interface AnimalRepositoryMapper {

    // Hacia fuera el "id" del dominio es el publicId (UUID) del entity.
    @Mapping(source = "publicId", target = "id")
    Animal entityToDomain(AnimalEntity entity);

    // Al crear: ni id (PK autogenerada) ni publicId (default field) se rellenan
    // desde el dominio: la BD asigna la PK y el entity inicializa el publicId.
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "publicId", ignore = true)
    AnimalEntity domainToEntity(Animal animal);

    // En la modificacion solo se aplican especie y edad; ni id, ni publicId, ni nombre.
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "publicId", ignore = true)
    @Mapping(target = "nombre", ignore = true)
    void aplicarModificaciones(Animal datos, @MappingTarget AnimalEntity target);
}
