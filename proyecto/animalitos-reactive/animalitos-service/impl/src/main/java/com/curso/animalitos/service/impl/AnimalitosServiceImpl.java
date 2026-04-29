package com.curso.animalitos.service.impl;

import com.curso.animalitos.repository.api.AnimalitosRepository;
import com.curso.animalitos.repository.api.exceptions.RepositorioException;
import com.curso.animalitos.service.api.AnimalitosService;
import com.curso.animalitos.service.api.exceptions.AnimalNoEncontradoException;
import com.curso.animalitos.service.api.exceptions.AnimalYaExisteException;
import com.curso.animalitos.service.api.exceptions.RepositorioInaccesibleException;
import com.curso.animalitos.service.api.models.AnimalDTO;
import com.curso.animalitos.service.api.models.CrearAnimalDTO;
import com.curso.animalitos.service.api.models.ModificarAnimalDTO;
import com.curso.animalitos.service.impl.mappers.AnimalServiceMapper;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Implementacion reactiva de la capa de servicio.
 * Inyeccion por constructor (dia2).
 *
 * {@code @Validated} activa method-level validation: las anotaciones
 * {@code @Valid}/{@code @NotBlank}/etc del contrato del servicio se
 * comprueban con un proxy AOP y disparan {@code ConstraintViolationException}
 * si los datos no son validos, sin necesidad de pasar por la capa controlador.
 *
 * Las excepciones tecnicas del repositorio ({@link RepositorioException}) se
 * traducen, dentro del flujo reactivo (con {@code onErrorMap}), a las
 * excepciones publicas del contrato del servicio.
 */
@Service
@Validated
public class AnimalitosServiceImpl implements AnimalitosService {

    private final AnimalitosRepository repository;
    private final AnimalServiceMapper mapper;

    public AnimalitosServiceImpl(AnimalitosRepository repository, AnimalServiceMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Mono<AnimalDTO> getAnimal(String id) {
        return repository.findById(id)
                         .map(mapper::domainToService)
                         .onErrorMap(RepositorioException.class, ex -> traducir(ex, id));
    }

    @Override
    public Flux<AnimalDTO> getAllAnimales() {
        return repository.findAll()
                         .map(mapper::domainToService)
                         .onErrorMap(RepositorioException.class, ex -> traducir(ex, null));
    }

    @Override
    public Mono<AnimalDTO> createAnimal(CrearAnimalDTO datos) {
        // Comprueba unicidad por nombre (politica de la capa de servicio).
        return repository.findAll()
                         .filter(a -> a.nombre().equalsIgnoreCase(datos.nombre()))
                         .hasElements()
                         .flatMap(yaExiste -> yaExiste
                                 ? Mono.<AnimalDTO>error(new AnimalYaExisteException(datos.nombre()))
                                 : repository.create(mapper.serviceCrearToDomain(datos))
                                             .map(mapper::domainToService))
                         .onErrorMap(RepositorioException.class, ex -> traducir(ex, null));
    }

    @Override
    public Mono<AnimalDTO> updateAnimal(String id, ModificarAnimalDTO datos) {
        return repository.update(id, mapper.serviceModificarToDomain(datos))
                         .map(mapper::domainToService)
                         .onErrorMap(RepositorioException.class, ex -> traducir(ex, id));
    }

    @Override
    public Mono<AnimalDTO> deleteAnimal(String id) {
        return repository.deleteById(id)
                         .map(mapper::domainToService)
                         .onErrorMap(RepositorioException.class, ex -> traducir(ex, id));
    }

    private RuntimeException traducir(RepositorioException ex, String idContexto) {
        return switch (ex.getTipoDeError()) {
            case ANIMAL_NO_ENCONTRADO -> new AnimalNoEncontradoException(idContexto != null ? idContexto : "");
            case ANIMAL_YA_EXISTE -> new AnimalYaExisteException("");
            case ERROR_TECNICO -> new RepositorioInaccesibleException("Error tecnico de repositorio", ex);
        };
    }
}
