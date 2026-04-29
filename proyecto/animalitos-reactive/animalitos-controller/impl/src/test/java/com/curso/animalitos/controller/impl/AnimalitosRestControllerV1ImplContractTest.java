package com.curso.animalitos.controller.impl;

import com.curso.animalitos.controller.api.AnimalitosControllerV1ContractTest;
import com.curso.animalitos.controller.impl.advice.AnimalitosControllerExceptionHandler;
import com.curso.animalitos.service.api.AnimalitosService;
import com.curso.animalitos.service.api.exceptions.AnimalNoEncontradoException;
import com.curso.animalitos.service.api.exceptions.AnimalYaExisteException;
import com.curso.animalitos.service.api.models.AnimalDTO;
import com.curso.animalitos.service.api.models.CrearAnimalDTO;
import com.curso.animalitos.service.api.models.ModificarAnimalDTO;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

/**
 * Ejecuta el contrato REST del controller (caja negra) sobre la implementacion
 * REACTIVA, con un AnimalitosService mockeado de forma "stateful" via Mockito
 * que devuelve Mono/Flux.
 */
@WebFluxTest(AnimalitosRestControllerV1Impl.class)
@Import({AnimalitosControllerExceptionHandler.class,
        AnimalitosRestControllerV1ImplContractTest.ServiceMockConfig.class})
class AnimalitosRestControllerV1ImplContractTest extends AnimalitosControllerV1ContractTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private AnimalitosService service;

    @BeforeEach
    void resetState() {
        // El bean del servicio mock guarda el estado entre tests si no lo reseteamos.
        AnimalitosService fresh = ServiceMockConfig.statefulServiceMock();
        Mockito.reset(service);
        ServiceMockConfig.delegar(service, fresh);
    }

    @Override
    protected WebTestClient webTestClient() {
        return webTestClient;
    }

    /** Inyecta un AnimalitosService reactivo mockeado con estado en memoria. */
    @TestConfiguration
    static class ServiceMockConfig {
        @Bean
        AnimalitosService animalitosService() {
            return statefulServiceMock();
        }

        static AnimalitosService statefulServiceMock() {
            Map<String, AnimalDTO> store = new LinkedHashMap<>();
            AnimalitosService mock = Mockito.mock(AnimalitosService.class);

            Mockito.when(mock.getAnimal(anyString())).thenAnswer(inv ->
                    Mono.defer(() -> {
                        AnimalDTO a = store.get((String) inv.getArgument(0));
                        return a == null ? Mono.empty() : Mono.just(a);
                    }));

            Mockito.when(mock.getAllAnimales()).thenAnswer(inv ->
                    Flux.defer(() -> Flux.fromIterable(store.values())));

            Mockito.when(mock.createAnimal(any(CrearAnimalDTO.class))).thenAnswer(inv -> {
                CrearAnimalDTO datos = inv.getArgument(0);
                return Mono.defer(() -> {
                    boolean yaExiste = store.values().stream()
                            .anyMatch(a -> a.nombre().equalsIgnoreCase(datos.nombre()));
                    if (yaExiste) {
                        return Mono.error(new AnimalYaExisteException(datos.nombre()));
                    }
                    String id = UUID.randomUUID().toString();
                    AnimalDTO creado = new AnimalDTO(id, datos.nombre(), datos.especie(), datos.edad());
                    store.put(id, creado);
                    return Mono.just(creado);
                });
            });

            Mockito.when(mock.updateAnimal(anyString(), any(ModificarAnimalDTO.class))).thenAnswer(inv -> {
                String id = inv.getArgument(0);
                ModificarAnimalDTO datos = inv.getArgument(1);
                return Mono.defer(() -> {
                    AnimalDTO actual = store.get(id);
                    if (actual == null) {
                        return Mono.error(new AnimalNoEncontradoException(id));
                    }
                    AnimalDTO modificado = new AnimalDTO(actual.id(), actual.nombre(), datos.especie(), datos.edad());
                    store.put(id, modificado);
                    return Mono.just(modificado);
                });
            });

            Mockito.when(mock.deleteAnimal(anyString())).thenAnswer(inv ->
                    Mono.defer(() -> {
                        AnimalDTO eliminado = store.remove((String) inv.getArgument(0));
                        return eliminado == null ? Mono.empty() : Mono.just(eliminado);
                    }));

            return mock;
        }

        /** Re-cablea el mock existente con el comportamiento de uno nuevo. */
        static void delegar(AnimalitosService destino, AnimalitosService fuente) {
            Mockito.when(destino.getAnimal(anyString())).thenAnswer(inv -> fuente.getAnimal(inv.getArgument(0)));
            Mockito.when(destino.getAllAnimales()).thenAnswer(inv -> fuente.getAllAnimales());
            Mockito.when(destino.createAnimal(any(CrearAnimalDTO.class))).thenAnswer(inv -> fuente.createAnimal(inv.getArgument(0)));
            Mockito.when(destino.updateAnimal(anyString(), any(ModificarAnimalDTO.class)))
                    .thenAnswer(inv -> fuente.updateAnimal(inv.getArgument(0), inv.getArgument(1)));
            Mockito.when(destino.deleteAnimal(anyString())).thenAnswer(inv -> fuente.deleteAnimal(inv.getArgument(0)));
        }
    }
}
