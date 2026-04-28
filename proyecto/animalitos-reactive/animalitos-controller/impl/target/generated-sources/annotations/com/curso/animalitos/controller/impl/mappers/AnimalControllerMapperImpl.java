package com.curso.animalitos.controller.impl.mappers;

import com.curso.animalitos.controller.api.models.AnimalRestDTO;
import com.curso.animalitos.controller.api.models.CrearAnimalRequestDTO;
import com.curso.animalitos.controller.api.models.ModificarAnimalRequestDTO;
import com.curso.animalitos.service.api.models.AnimalDTO;
import com.curso.animalitos.service.api.models.CrearAnimalDTO;
import com.curso.animalitos.service.api.models.ModificarAnimalDTO;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-04-28T17:47:49+0200",
    comments = "version: 1.6.3, compiler: javac, environment: Java 25.0.2 (Homebrew)"
)
@Component
public class AnimalControllerMapperImpl implements AnimalControllerMapper {

    @Override
    public AnimalRestDTO serviceToRest(AnimalDTO dto) {
        if ( dto == null ) {
            return null;
        }

        String id = null;
        String nombre = null;
        String especie = null;
        int edad = 0;

        id = dto.id();
        nombre = dto.nombre();
        especie = dto.especie();
        edad = dto.edad();

        AnimalRestDTO animalRestDTO = new AnimalRestDTO( id, nombre, especie, edad );

        return animalRestDTO;
    }

    @Override
    public CrearAnimalDTO restToCrearService(CrearAnimalRequestDTO dto) {
        if ( dto == null ) {
            return null;
        }

        String nombre = null;
        String especie = null;
        Integer edad = null;

        nombre = dto.nombre();
        especie = dto.especie();
        edad = dto.edad();

        CrearAnimalDTO crearAnimalDTO = new CrearAnimalDTO( nombre, especie, edad );

        return crearAnimalDTO;
    }

    @Override
    public ModificarAnimalDTO restToModificarService(ModificarAnimalRequestDTO dto) {
        if ( dto == null ) {
            return null;
        }

        String especie = null;
        Integer edad = null;

        especie = dto.especie();
        edad = dto.edad();

        ModificarAnimalDTO modificarAnimalDTO = new ModificarAnimalDTO( especie, edad );

        return modificarAnimalDTO;
    }
}
