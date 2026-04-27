package com.curso.animalitos.service.api;

import com.curso.animalitos.service.api.exceptions.AnimalNoEncontradoException;
import com.curso.animalitos.service.api.exceptions.AnimalYaExisteException;
import com.curso.animalitos.service.api.models.AnimalDTO;
import com.curso.animalitos.service.api.models.CrearAnimalDTO;
import com.curso.animalitos.service.api.models.ModificarAnimalDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Contrato (caja negra) que cualquier {@link AnimalitosService} debe cumplir.
 *
 * Las clases concretas en *impl* deben extender esta clase y devolver una
 * instancia de servicio recien creada y limpia en {@link #crearServicio()}.
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
        AnimalDTO creado = service.createAnimal(new CrearAnimalDTO("Lucas", "GATO", 3));

        assertThat(creado.id()).isNotBlank();
        assertThat(creado.nombre()).isEqualTo("Lucas");
        assertThat(creado.especie()).isEqualTo("GATO");
        assertThat(creado.edad()).isEqualTo(3);
    }

    @Test
    void createAnimal_lanzaYaExiste_siNombreRepetido() {
        service.createAnimal(new CrearAnimalDTO("Lucas", "GATO", 3));

        assertThatThrownBy(() -> service.createAnimal(new CrearAnimalDTO("Lucas", "PERRO", 5)))
                .isInstanceOf(AnimalYaExisteException.class);
    }

    // -------------------- getAnimal --------------------

    @Test
    void getAnimal_devuelvePresent_siExiste() {
        AnimalDTO creado = service.createAnimal(new CrearAnimalDTO("Firulais", "PERRO", 5));

        Optional<AnimalDTO> hallado = service.getAnimal(creado.id());

        assertThat(hallado).isPresent().contains(creado);
    }

    @Test
    void getAnimal_devuelveEmpty_siNoExiste() {
        assertThat(service.getAnimal("INEXISTENTE")).isEmpty();
    }

    // -------------------- getAllAnimales --------------------

    @Test
    void getAllAnimales_devuelveTodos() {
        service.createAnimal(new CrearAnimalDTO("Lucas", "GATO", 3));
        service.createAnimal(new CrearAnimalDTO("Firulais", "PERRO", 5));

        List<AnimalDTO> todos = service.getAllAnimales();

        assertThat(todos).hasSize(2)
                .extracting(AnimalDTO::nombre)
                .containsExactlyInAnyOrder("Lucas", "Firulais");
    }

    @Test
    void getAllAnimales_devuelveListaVacia_siNoHayDatos() {
        assertThat(service.getAllAnimales()).isEmpty();
    }

    // -------------------- updateAnimal --------------------

    @Test
    void updateAnimal_modificaEspecieYEdad() {
        AnimalDTO creado = service.createAnimal(new CrearAnimalDTO("Lucas", "GATO", 3));

        AnimalDTO modificado = service.updateAnimal(creado.id(), new ModificarAnimalDTO("PERRO", 7));

        assertThat(modificado.id()).isEqualTo(creado.id());
        assertThat(modificado.nombre()).isEqualTo("Lucas");
        assertThat(modificado.especie()).isEqualTo("PERRO");
        assertThat(modificado.edad()).isEqualTo(7);
    }

    @Test
    void updateAnimal_lanzaNoEncontrado_siIdNoExiste() {
        assertThatThrownBy(() -> service.updateAnimal("FANTASMA", new ModificarAnimalDTO("GATO", 1)))
                .isInstanceOf(AnimalNoEncontradoException.class);
    }

    // -------------------- deleteAnimal --------------------

    @Test
    void deleteAnimal_devuelvePresent_yLoQuita() {
        AnimalDTO creado = service.createAnimal(new CrearAnimalDTO("Lucas", "GATO", 3));

        Optional<AnimalDTO> borrado = service.deleteAnimal(creado.id());

        assertThat(borrado).isPresent().contains(creado);
        assertThat(service.getAnimal(creado.id())).isEmpty();
    }

    @Test
    void deleteAnimal_devuelveEmpty_siNoExiste() {
        assertThat(service.deleteAnimal("FANTASMA")).isEmpty();
    }
}
