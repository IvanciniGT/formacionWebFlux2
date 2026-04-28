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

import java.util.List;
import java.util.Optional;

/**
 * Implementacion de la capa de servicio.
 * Inyeccion por constructor (dia2).
 *
 * {@code @Validated} activa method-level validation: las anotaciones
 * {@code @Valid}/{@code @NotBlank}/etc del contrato del servicio se
 * comprueban con un proxy AOP y disparan {@code ConstraintViolationException}
 * si los datos no son validos, sin necesidad de pasar por la capa controlador.
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
    public Optional<AnimalDTO> getAnimal(String id) {
        try {
            return repository.findById(id).map(mapper::domainToService);
        } catch (RepositorioException ex) {
            throw traducir(ex);
        }
    }

    @Override
    public List<AnimalDTO> getAllAnimales() {
        try {
            return repository.findAll().stream().map(mapper::domainToService).toList();
        } catch (RepositorioException ex) {
            throw traducir(ex);
        }
    }

    @Override
    public AnimalDTO createAnimal(CrearAnimalDTO datos) {
        // Comprueba unicidad por nombre (politica de la capa de servicio).
        boolean yaExiste = repository.findAll().stream()
                .anyMatch(a -> a.nombre().equalsIgnoreCase(datos.nombre()));
        if (yaExiste) {
            throw new AnimalYaExisteException(datos.nombre());
        }
        try {
            return mapper.domainToService(repository.create(mapper.serviceCrearToDomain(datos)));
        } catch (RepositorioException ex) {
            throw traducir(ex);
        }
    }

    @Override
    public AnimalDTO updateAnimal(String id, ModificarAnimalDTO datos) {
        try {
            return mapper.domainToService(repository.update(id, mapper.serviceModificarToDomain(datos)));
        } catch (RepositorioException ex) {
            throw traducir(ex, id);
        }
    }

    @Override
    public Optional<AnimalDTO> deleteAnimal(String id) {
        try {
            return repository.deleteById(id).map(mapper::domainToService);
        } catch (RepositorioException ex) {
            throw traducir(ex);
        }
    }

    private RuntimeException traducir(RepositorioException ex) {
        return traducir(ex, null);
    }

    private RuntimeException traducir(RepositorioException ex, String idContexto) {
        return switch (ex.getTipoDeError()) {
            case ANIMAL_NO_ENCONTRADO -> new AnimalNoEncontradoException(idContexto != null ? idContexto : "");
            case ANIMAL_YA_EXISTE -> new AnimalYaExisteException("");
            case ERROR_TECNICO -> new RepositorioInaccesibleException("Error tecnico de repositorio", ex);
        };
    }
}
