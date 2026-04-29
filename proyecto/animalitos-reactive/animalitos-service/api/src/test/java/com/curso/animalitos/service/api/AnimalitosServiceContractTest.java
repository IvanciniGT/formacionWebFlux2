package com.curso.animalitos.service.api;

import com.curso.animalitos.service.api.exceptions.AnimalNoEncontradoException;
import com.curso.animalitos.service.api.exceptions.AnimalYaExisteException;
import com.curso.animalitos.service.api.models.AnimalDTO;
import com.curso.animalitos.service.api.models.CrearAnimalDTO;
import com.curso.animalitos.service.api.models.ModificarAnimalDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Contrato (caja negra) que cualquier {@link AnimalitosService} reactivo debe cumplir.
 *
 * Las clases concretas en *impl* deben extender esta clase y devolver una
 * instancia de servicio recien creada y limpia en {@link #crearServicio()}.
 *
 * Se usa {@code .block()} para materializar los flujos en cada test, en linea
 * con el contrato del repositorio. Los errores se comprueban suscribiendose
 * (block()) a un Mono que se sabe fallido.
 */
public abstract class AnimalitosServiceContractTest {

    protected AnimalitosService service;

    protected abstract AnimalitosService crearServicio();

    @BeforeEach
    void setUp() {
        this.service = crearServicio();
    }

    // -------------------- createAnimal --------------------

    @Test
    void createAnimal_devuelveAnimalConIdGenerado() {
        AnimalDTO creado = service.createAnimal(new CrearAnimalDTO("Lucas", "GATO", 3)).block();

        assertThat(creado).isNotNull();
        assertThat(creado.id()).isNotBlank();
        assertThat(creado.nombre()).isEqualTo("Lucas");
        assertThat(creado.especie()).isEqualTo("GATO");
        assertThat(creado.edad()).isEqualTo(3);
    }

    @Test
    void createAnimal_lanzaYaExiste_siNombreRepetido() {
        service.createAnimal(new CrearAnimalDTO("Lucas", "GATO", 3)).block();

        Mono<AnimalDTO> repetido = service.createAnimal(new CrearAnimalDTO("Lucas", "PERRO", 5));
        assertThatThrownBy(repetido::block)
                .isInstanceOf(AnimalYaExisteException.class);
    }

    // -------------------- getAnimal --------------------

    @Test
    void getAnimal_devuelvePresent_siExiste() {
        AnimalDTO creado = service.createAnimal(new CrearAnimalDTO("Firulais", "PERRO", 5)).block();

        AnimalDTO hallado = service.getAnimal(creado.id()).block();

        assertThat(hallado).isEqualTo(creado);
    }

    @Test
    void getAnimal_devuelveEmpty_siNoExiste() {
        assertThat(service.getAnimal("INEXISTENTE").block()).isNull();
    }

    // -------------------- getAllAnimales --------------------

    @Test
    void getAllAnimales_devuelveTodos() {
        service.createAnimal(new CrearAnimalDTO("Lucas", "GATO", 3)).block();
        service.createAnimal(new CrearAnimalDTO("Firulais", "PERRO", 5)).block();

        List<AnimalDTO> todos = service.getAllAnimales().collectList().block();

        assertThat(todos).hasSize(2)
                .extracting(AnimalDTO::nombre)
                .containsExactlyInAnyOrder("Lucas", "Firulais");
    }

    @Test
    void getAllAnimales_devuelveListaVacia_siNoHayDatos() {
        assertThat(service.getAllAnimales().collectList().block()).isEmpty();
    }

    // -------------------- updateAnimal --------------------

    @Test
    void updateAnimal_modificaEspecieYEdad() {
        AnimalDTO creado = service.createAnimal(new CrearAnimalDTO("Lucas", "GATO", 3)).block();

        AnimalDTO modificado = service.updateAnimal(creado.id(), new ModificarAnimalDTO("PERRO", 7)).block();

        assertThat(modificado.id()).isEqualTo(creado.id());
        assertThat(modificado.nombre()).isEqualTo("Lucas");
        assertThat(modificado.especie()).isEqualTo("PERRO");
        assertThat(modificado.edad()).isEqualTo(7);
    }

    @Test
    void updateAnimal_lanzaNoEncontrado_siIdNoExiste() {
        Mono<AnimalDTO> mono = service.updateAnimal("FANTASMA", new ModificarAnimalDTO("GATO", 1));
        assertThatThrownBy(mono::block)
                .isInstanceOf(AnimalNoEncontradoException.class);
    }

    // -------------------- deleteAnimal --------------------

    @Test
    void deleteAnimal_devuelvePresent_yLoQuita() {
        AnimalDTO creado = service.createAnimal(new CrearAnimalDTO("Lucas", "GATO", 3)).block();

        AnimalDTO borrado = service.deleteAnimal(creado.id()).block();

        assertThat(borrado).isEqualTo(creado);
        assertThat(service.getAnimal(creado.id()).block()).isNull();
    }

    @Test
    void deleteAnimal_devuelveEmpty_siNoExiste() {
        assertThat(service.deleteAnimal("FANTASMA").block()).isNull();
    }
}
