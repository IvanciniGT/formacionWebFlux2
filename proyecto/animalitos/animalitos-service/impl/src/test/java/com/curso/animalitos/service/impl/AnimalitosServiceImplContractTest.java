package com.curso.animalitos.service.impl;

import com.curso.animalitos.domain.Animal;
import com.curso.animalitos.repository.api.AnimalitosRepository;
import com.curso.animalitos.repository.api.exceptions.RepositorioException;
import com.curso.animalitos.service.api.AnimalitosService;
import com.curso.animalitos.service.api.AnimalitosServiceContractTest;
import com.curso.animalitos.service.impl.mappers.AnimalServiceMapper;
import org.mapstruct.factory.Mappers;
import org.mockito.Mockito;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

/**
 * Ejecuta el contrato del servicio (caja negra) usando Mockito para construir
 * un repositorio "stateful" en memoria. No es una clase fake manual: cada
 * comportamiento esta declarado con thenAnswer().
 */
class AnimalitosServiceImplContractTest extends AnimalitosServiceContractTest {

    @Override
    protected AnimalitosService crearServicio() {
        AnimalitosRepository repoMock = construirRepositorioMockito();
        AnimalServiceMapper mapper = Mappers.getMapper(AnimalServiceMapper.class);
        return new AnimalitosServiceImpl(repoMock, mapper);
    }

    /** Construye un mock Mockito de AnimalitosRepository con estado en memoria. */
    private static AnimalitosRepository construirRepositorioMockito() {
        Map<String, Animal> store = new LinkedHashMap<>();
        AnimalitosRepository mock = Mockito.mock(AnimalitosRepository.class);

        Mockito.when(mock.findById(anyString())).thenAnswer(inv -> {
            String id = inv.getArgument(0);
            return Optional.ofNullable(store.get(id));
        });

        Mockito.when(mock.findAll()).thenAnswer(inv -> List.copyOf(store.values()));

        Mockito.when(mock.create(any(Animal.class))).thenAnswer(inv -> {
            Animal datos = inv.getArgument(0);
            String id = UUID.randomUUID().toString();
            Animal creado = new Animal(id, datos.nombre(), datos.especie(), datos.edad());
            store.put(id, creado);
            return creado;
        });

        Mockito.when(mock.update(anyString(), any(Animal.class))).thenAnswer(inv -> {
            String id = inv.getArgument(0);
            Animal datos = inv.getArgument(1);
            Animal actual = store.get(id);
            if (actual == null) {
                throw new RepositorioException("No existe " + id, RepositorioException.TipoDeError.ANIMAL_NO_ENCONTRADO);
            }
            Animal modificado = new Animal(actual.id(), actual.nombre(), datos.especie(), datos.edad());
            store.put(id, modificado);
            return modificado;
        });

        Mockito.when(mock.deleteById(anyString())).thenAnswer(inv -> {
            String id = inv.getArgument(0);
            return Optional.ofNullable(store.remove(id));
        });

        return mock;
    }
}
