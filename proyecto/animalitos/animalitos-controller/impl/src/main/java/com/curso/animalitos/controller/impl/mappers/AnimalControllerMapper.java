package com.curso.animalitos.controller.impl.mappers;

import com.curso.animalitos.controller.api.models.AnimalRestDTO;
import com.curso.animalitos.controller.api.models.CrearAnimalRequestDTO;
import com.curso.animalitos.controller.api.models.ModificarAnimalRequestDTO;
import com.curso.animalitos.service.api.models.AnimalDTO;
import com.curso.animalitos.service.api.models.CrearAnimalDTO;
import com.curso.animalitos.service.api.models.ModificarAnimalDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AnimalControllerMapper {

    AnimalRestDTO serviceToRest(AnimalDTO dto);

    CrearAnimalDTO restToCrearService(CrearAnimalRequestDTO dto);

    ModificarAnimalDTO restToModificarService(ModificarAnimalRequestDTO dto);
}
