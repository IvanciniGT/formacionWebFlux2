package com.curso.animalitos.service.impl.mappers;

import com.curso.animalitos.domain.Animal;
import com.curso.animalitos.service.api.models.AnimalDTO;
import com.curso.animalitos.service.api.models.CrearAnimalDTO;
import com.curso.animalitos.service.api.models.ModificarAnimalDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper entre los DTOs publicos de la capa de servicio y la entidad de
 * DOMINIO {@link Animal}, que es la moneda de cambio con la persistencia.
 */
@Mapper(componentModel = "spring")
public interface AnimalServiceMapper {

    AnimalDTO domainToService(Animal animal);

    // Crear: id null, lo asigna la persistencia.
    @Mapping(target = "id", ignore = true)
    Animal serviceCrearToDomain(CrearAnimalDTO dto);

    // Modificar: id y nombre se ignoran; solo viajan especie y edad.
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "nombre", ignore = true)
    Animal serviceModificarToDomain(ModificarAnimalDTO dto);
}
