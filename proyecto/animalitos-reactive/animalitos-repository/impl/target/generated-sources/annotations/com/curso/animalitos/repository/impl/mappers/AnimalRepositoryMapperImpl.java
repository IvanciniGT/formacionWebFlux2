package com.curso.animalitos.repository.impl.mappers;

import com.curso.animalitos.domain.Animal;
import com.curso.animalitos.repository.impl.entities.AnimalEntity;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-04-28T17:46:56+0200",
    comments = "version: 1.6.3, compiler: javac, environment: Java 25.0.2 (Homebrew)"
)
@Component
public class AnimalRepositoryMapperImpl implements AnimalRepositoryMapper {

    @Override
    public Animal entityToDomain(AnimalEntity entity) {
        if ( entity == null ) {
            return null;
        }

        String id = null;
        String nombre = null;
        String especie = null;
        Integer edad = null;

        id = entity.getPublicId();
        nombre = entity.getNombre();
        especie = entity.getEspecie();
        edad = entity.getEdad();

        Animal animal = new Animal( id, nombre, especie, edad );

        return animal;
    }

    @Override
    public AnimalEntity domainToEntity(Animal animal) {
        if ( animal == null ) {
            return null;
        }

        AnimalEntity animalEntity = new AnimalEntity();

        animalEntity.setNombre( animal.nombre() );
        animalEntity.setEspecie( animal.especie() );
        if ( animal.edad() != null ) {
            animalEntity.setEdad( animal.edad() );
        }

        return animalEntity;
    }

    @Override
    public void aplicarModificaciones(Animal datos, AnimalEntity target) {
        if ( datos == null ) {
            return;
        }

        target.setEspecie( datos.especie() );
        if ( datos.edad() != null ) {
            target.setEdad( datos.edad() );
        }
    }
}
