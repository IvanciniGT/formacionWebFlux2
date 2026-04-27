package com.curso.animalitos.controller.impl;

import com.curso.animalitos.controller.api.AnimalitosControllerV1;
import com.curso.animalitos.controller.api.models.AnimalRestDTO;
import com.curso.animalitos.controller.api.models.CrearAnimalRequestDTO;
import com.curso.animalitos.controller.api.models.ModificarAnimalRequestDTO;
import com.curso.animalitos.controller.impl.mappers.AnimalControllerMapper;
import com.curso.animalitos.service.api.AnimalitosService;
import com.curso.animalitos.service.api.exceptions.AnimalNoEncontradoException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Implementacion REST. Inyeccion por constructor.
 * Las excepciones de negocio se mapean a HTTP en {@link AnimalitosControllerExceptionHandler}.
 */
@RestController
public class AnimalitosRestControllerV1Impl implements AnimalitosControllerV1 {

    private final AnimalitosService service;
    private final AnimalControllerMapper mapper;

    public AnimalitosRestControllerV1Impl(AnimalitosService service, AnimalControllerMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @Override
    public ResponseEntity<AnimalRestDTO> getAnimal(String id) {
        return service.getAnimal(id)
                .map(mapper::serviceToRest)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new AnimalNoEncontradoException(id));
    }

    @Override
    public ResponseEntity<List<AnimalRestDTO>> getAllAnimales() {
        return ResponseEntity.ok(service.getAllAnimales().stream().map(mapper::serviceToRest).toList());
    }

    @Override
    public ResponseEntity<AnimalRestDTO> createAnimal(CrearAnimalRequestDTO datos) {
        AnimalRestDTO creado = mapper.serviceToRest(service.createAnimal(mapper.restToCrearService(datos)));
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    @Override
    public ResponseEntity<AnimalRestDTO> updateAnimal(String id, ModificarAnimalRequestDTO datos) {
        return ResponseEntity.ok(mapper.serviceToRest(
                service.updateAnimal(id, mapper.restToModificarService(datos))));
    }

    @Override
    public ResponseEntity<AnimalRestDTO> deleteAnimal(String id) {
        return service.deleteAnimal(id)
                .map(mapper::serviceToRest)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new AnimalNoEncontradoException(id));
    }
}
