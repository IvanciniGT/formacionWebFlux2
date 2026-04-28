package com.curso.animalitos.repository.api;

import com.curso.animalitos.domain.Animal;
import com.curso.animalitos.repository.api.exceptions.RepositorioException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Contrato (caja negra) que TODA implementacion de {@link AnimalitosRepository} debe cumplir.
 *
 * Las clases concretas en los modulos *impl* deben extender esta clase y devolver
 * una instancia recien creada en {@link #crearRepositorio()}.
 *
 * No es ejecutable por si misma (abstract), pero sus tests se heredan.
 */
public abstract class AnimalitosRepositoryContractTest {

    protected AnimalitosRepository repo;

    /** Cada implementacion devuelve una instancia limpia (vacia) del repositorio. */
    protected abstract AnimalitosRepository crearRepositorio();

    @BeforeEach
    void setUp() {
        this.repo = crearRepositorio();
    }

    // -------------------- create --------------------

    @Test
    void create_devuelveAnimalConIdGenerado() {
        // GIVEN datos de creacion validos (sin id)
        Animal datos = new Animal(null, "Lucas", "GATO", 3);

        // WHEN
        Animal creado = repo.create(datos);

        // THEN
        assertThat(creado.id()).isNotBlank();
        assertThat(creado.nombre()).isEqualTo("Lucas");
        assertThat(creado.especie()).isEqualTo("GATO");
        assertThat(creado.edad()).isEqualTo(3);
    }

    // -------------------- findById --------------------

    @Test
    void findById_devuelveEmpty_siNoExiste() {
        assertThat(repo.findById("INEXISTENTE")).isEmpty();
    }

    @Test
    void findById_devuelvePresent_siExiste() {
        // GIVEN
        Animal creado = repo.create(new Animal(null, "Firulais", "PERRO", 5));

        // WHEN
        Optional<Animal> hallado = repo.findById(creado.id());

        // THEN
        assertThat(hallado).isPresent();
        assertThat(hallado.get()).isEqualTo(creado);
    }

    // -------------------- findAll --------------------

    @Test
    void findAll_devuelveTodos() {
        // GIVEN
        repo.create(new Animal(null, "Lucas", "GATO", 3));
        repo.create(new Animal(null, "Firulais", "PERRO", 5));
        repo.create(new Animal(null, "Coco", "LORO", 8));

        // WHEN
        List<Animal> todos = repo.findAll();

        // THEN
        assertThat(todos).hasSize(3)
                .extracting(Animal::nombre)
                .containsExactlyInAnyOrder("Lucas", "Firulais", "Coco");
    }

    @Test
    void findAll_devuelveListaVacia_siNoHayDatos() {
        assertThat(repo.findAll()).isEmpty();
    }

    // -------------------- update --------------------

    @Test
    void update_modificaEspecieYEdad_yPreservaNombre() {
        // GIVEN
        Animal creado = repo.create(new Animal(null, "Lucas", "GATO", 3));

        // WHEN: el nombre del DTO de entrada se ignora; solo cambian especie y edad.
        Animal modificado = repo.update(creado.id(), new Animal(null, "IGNORADO", "PERRO", 7));

        // THEN
        assertThat(modificado.id()).isEqualTo(creado.id());
        assertThat(modificado.nombre()).isEqualTo("Lucas");
        assertThat(modificado.especie()).isEqualTo("PERRO");
        assertThat(modificado.edad()).isEqualTo(7);
    }

    @Test
    void update_lanzaNoEncontrado_siIdNoExiste() {
        assertThatThrownBy(() -> repo.update("FANTASMA", new Animal(null, null, "GATO", 1)))
                .isInstanceOf(RepositorioException.class)
                .extracting("tipoDeError")
                .isEqualTo(RepositorioException.TipoDeError.ANIMAL_NO_ENCONTRADO);
    }

    // -------------------- deleteById --------------------

    @Test
    void deleteById_devuelveAnimalEliminado_yLoQuita() {
        // GIVEN
        Animal creado = repo.create(new Animal(null, "Lucas", "GATO", 3));

        // WHEN
        Optional<Animal> borrado = repo.deleteById(creado.id());

        // THEN
        assertThat(borrado).isPresent().contains(creado);
        assertThat(repo.findById(creado.id())).isEmpty();
    }

    @Test
    void deleteById_devuelveEmpty_siNoExiste() {
        assertThat(repo.deleteById("FANTASMA")).isEmpty();
    }

    // -------------------- datos podridos --------------------
    //
    // El repositorio es una BARRERA: aunque la capa de servicio se salte la
    // validacion (o un futuro caller llame directo al repo), el repo debe
    // rechazar datos invalidos. No nos atamos a un tipo concreto de
    // excepcion (cada impl puede traducirla a su gusto), solo exigimos que
    // la operacion FALLE en lugar de persistir basura.

    @Test
    void create_rechazaAnimalConNombreInvalido() {
        assertThatThrownBy(() -> repo.create(new Animal(null, null, "GATO", 3)))
                .isInstanceOf(RuntimeException.class);
        assertThatThrownBy(() -> repo.create(new Animal(null, "", "GATO", 3)))
                .isInstanceOf(RuntimeException.class);
        assertThatThrownBy(() -> repo.create(new Animal(null, "a", "GATO", 3)))
                .isInstanceOf(RuntimeException.class);
        assertThatThrownBy(() -> repo.create(new Animal(null, "x".repeat(61), "GATO", 3)))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    void create_rechazaAnimalConEspecieInvalida() {
        assertThatThrownBy(() -> repo.create(new Animal(null, "Lucas", null, 3)))
                .isInstanceOf(RuntimeException.class);
        assertThatThrownBy(() -> repo.create(new Animal(null, "Lucas", "DRAGON", 3)))
                .isInstanceOf(RuntimeException.class);
        assertThatThrownBy(() -> repo.create(new Animal(null, "Lucas", "perro", 3)))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    void create_rechazaAnimalConEdadInvalida() {
        assertThatThrownBy(() -> repo.create(new Animal(null, "Lucas", "GATO", -1)))
                .isInstanceOf(RuntimeException.class);
        assertThatThrownBy(() -> repo.create(new Animal(null, "Lucas", "GATO", 101)))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    void create_noPersisteSiLosDatosSonInvalidos() {
        // Si el repo lanza al validar, no debe haber dejado nada guardado.
        try {
            repo.create(new Animal(null, "x", "DRAGON", 999));
        } catch (RuntimeException ignorada) {
            // esperado
        }
        assertThat(repo.findAll()).isEmpty();
    }

    @Test
    void findById_rechazaIdEnBlanco() {
        assertThatThrownBy(() -> repo.findById(null))
                .isInstanceOf(RuntimeException.class);
        assertThatThrownBy(() -> repo.findById(""))
                .isInstanceOf(RuntimeException.class);
        assertThatThrownBy(() -> repo.findById("   "))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    void update_rechazaIdEnBlanco() {
        assertThatThrownBy(() -> repo.update(null, new Animal(null, null, "GATO", 3)))
                .isInstanceOf(RuntimeException.class);
        assertThatThrownBy(() -> repo.update("", new Animal(null, null, "GATO", 3)))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    void deleteById_rechazaIdEnBlanco() {
        assertThatThrownBy(() -> repo.deleteById(null))
                .isInstanceOf(RuntimeException.class);
        assertThatThrownBy(() -> repo.deleteById(""))
                .isInstanceOf(RuntimeException.class);
    }
}
