package com.curso.animalitos.controller.impl;

import com.curso.animalitos.controller.api.AnimalitosControllerV1ContractTest;
import com.curso.animalitos.controller.impl.advice.AnimalitosControllerExceptionHandler;
import com.curso.animalitos.controller.impl.mappers.AnimalControllerMapper;
import com.curso.animalitos.service.api.AnimalitosService;
import com.curso.animalitos.service.api.exceptions.AnimalNoEncontradoException;
import com.curso.animalitos.service.api.exceptions.AnimalYaExisteException;
import com.curso.animalitos.service.api.models.AnimalDTO;
import com.curso.animalitos.service.api.models.CrearAnimalDTO;
import com.curso.animalitos.service.api.models.ModificarAnimalDTO;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

/**
 * Ejecuta el contrato REST del controller (caja negra) sobre la implementacion real,
 * con un AnimalitosService mockeado de forma "stateful" via Mockito.
 */
@WebMvcTest(AnimalitosRestControllerV1Impl.class)
@Import({AnimalitosControllerExceptionHandler.class,
        AnimalitosRestControllerV1ImplContractTest.ServiceMockConfig.class})
class AnimalitosRestControllerV1ImplContractTest extends AnimalitosControllerV1ContractTest {

    @Autowired
    private MockMvc mvc;

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
    protected MockMvc mockMvc() {
        return mvc;
    }

    /** Inyecta un AnimalitosService mockeado con estado en memoria. */
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
                    Optional.ofNullable(store.get((String) inv.getArgument(0))));

            Mockito.when(mock.getAllAnimales()).thenAnswer(inv -> List.copyOf(store.values()));

            Mockito.when(mock.createAnimal(any(CrearAnimalDTO.class))).thenAnswer(inv -> {
                CrearAnimalDTO datos = inv.getArgument(0);
                boolean yaExiste = store.values().stream()
                        .anyMatch(a -> a.nombre().equalsIgnoreCase(datos.nombre()));
                if (yaExiste) throw new AnimalYaExisteException(datos.nombre());
                String id = UUID.randomUUID().toString();
                AnimalDTO creado = new AnimalDTO(id, datos.nombre(), datos.especie(), datos.edad());
                store.put(id, creado);
                return creado;
            });

            Mockito.when(mock.updateAnimal(anyString(), any(ModificarAnimalDTO.class))).thenAnswer(inv -> {
                String id = inv.getArgument(0);
                ModificarAnimalDTO datos = inv.getArgument(1);
                AnimalDTO actual = store.get(id);
                if (actual == null) throw new AnimalNoEncontradoException(id);
                AnimalDTO modificado = new AnimalDTO(actual.id(), actual.nombre(), datos.especie(), datos.edad());
                store.put(id, modificado);
                return modificado;
            });

            Mockito.when(mock.deleteAnimal(anyString())).thenAnswer(inv ->
                    Optional.ofNullable(store.remove((String) inv.getArgument(0))));

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
