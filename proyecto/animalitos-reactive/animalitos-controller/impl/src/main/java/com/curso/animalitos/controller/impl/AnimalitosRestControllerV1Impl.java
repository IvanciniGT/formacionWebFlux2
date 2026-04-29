package com.curso.animalitos.controller.impl;

import com.curso.animalitos.controller.api.AnimalitosControllerV1;
import com.curso.animalitos.controller.api.models.AnimalRestDTO;
import com.curso.animalitos.controller.api.models.CrearAnimalRequestDTO;
import com.curso.animalitos.controller.api.models.ModificarAnimalRequestDTO;
import com.curso.animalitos.controller.impl.advice.AnimalitosControllerExceptionHandler;
import com.curso.animalitos.controller.impl.mappers.AnimalControllerMapper;
import com.curso.animalitos.service.api.AnimalitosService;
import com.curso.animalitos.service.api.exceptions.AnimalNoEncontradoException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Implementacion REST reactiva (WebFlux). Inyeccion por constructor.
 *
 * Las excepciones de negocio (que viajan por el flujo reactivo como
 * senales de error) se mapean a HTTP en {@link AnimalitosControllerExceptionHandler},
 * que sigue siendo {@code @RestControllerAdvice} (compatible con WebFlux).
 *
 * Convencion para "no existe":
 *  - El servicio reactivo devuelve {@code Mono.empty()} en getAnimal/deleteAnimal
 *    cuando el id no existe.
 *  - Aqui lo convertimos en 404 lanzando {@link AnimalNoEncontradoException}
 *    via {@code switchIfEmpty}, que el advice traduce.
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
    public Mono<ResponseEntity<AnimalRestDTO>> getAnimal(String id) {
        return service.getAnimal(id)
                      .map(mapper::serviceToRest)
                      .map(ResponseEntity::ok)
                      .switchIfEmpty(Mono.error(() -> new AnimalNoEncontradoException(id)));
    }

    @Override
    public Flux<AnimalRestDTO> getAllAnimales() {
        return service.getAllAnimales().map(mapper::serviceToRest);
    }

    @Override
    public Mono<ResponseEntity<AnimalRestDTO>> createAnimal(CrearAnimalRequestDTO datos) {
        return service.createAnimal(mapper.restToCrearService(datos))
                      .map(mapper::serviceToRest)
                      .map(creado -> ResponseEntity.status(HttpStatus.CREATED).body(creado));
    }

    @Override
    public Mono<ResponseEntity<AnimalRestDTO>> updateAnimal(String id, ModificarAnimalRequestDTO datos) {
        return service.updateAnimal(id, mapper.restToModificarService(datos))
                      .map(mapper::serviceToRest)
                      .map(ResponseEntity::ok);
    }

    @Override
    public Mono<ResponseEntity<AnimalRestDTO>> deleteAnimal(String id) {
        return service.deleteAnimal(id)
                      .map(mapper::serviceToRest)
                      .map(ResponseEntity::ok)
                      .switchIfEmpty(Mono.error(() -> new AnimalNoEncontradoException(id)));
    }
}
