package com.curso.animalitos.service.impl.mappers;

import com.curso.animalitos.domain.Animal;
import com.curso.animalitos.service.api.models.AnimalDTO;
import com.curso.animalitos.service.api.models.CrearAnimalDTO;
import com.curso.animalitos.service.api.models.ModificarAnimalDTO;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-04-28T17:46:58+0200",
    comments = "version: 1.6.3, compiler: javac, environment: Java 25.0.2 (Homebrew)"
)
@Component
public class AnimalServiceMapperImpl implements AnimalServiceMapper {

    @Override
    public AnimalDTO domainToService(Animal animal) {
        if ( animal == null ) {
            return null;
        }

        String id = null;
        String nombre = null;
        String especie = null;
        int edad = 0;

        id = animal.id();
        nombre = animal.nombre();
        especie = animal.especie();
        if ( animal.edad() != null ) {
            edad = animal.edad();
        }

        AnimalDTO animalDTO = new AnimalDTO( id, nombre, especie, edad );

        return animalDTO;
    }

    @Override
    public Animal serviceCrearToDomain(CrearAnimalDTO dto) {
        if ( dto == null ) {
            return null;
        }

        String nombre = null;
        String especie = null;
        Integer edad = null;

        nombre = dto.nombre();
        especie = dto.especie();
        edad = dto.edad();

        String id = null;

        Animal animal = new Animal( id, nombre, especie, edad );

        return animal;
    }

    @Override
    public Animal serviceModificarToDomain(ModificarAnimalDTO dto) {
        if ( dto == null ) {
            return null;
        }

        String especie = null;
        Integer edad = null;

        especie = dto.especie();
        edad = dto.edad();

        String id = null;
        String nombre = null;

        Animal animal = new Animal( id, nombre, especie, edad );

        return animal;
    }
}
