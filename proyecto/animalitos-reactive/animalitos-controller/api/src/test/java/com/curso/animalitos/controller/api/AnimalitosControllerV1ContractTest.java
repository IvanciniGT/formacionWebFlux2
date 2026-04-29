package com.curso.animalitos.controller.api;

import com.curso.animalitos.controller.api.models.AnimalRestDTO;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Contrato REST (caja negra) reactivo para AnimalitosControllerV1.
 *
 * Subclases en *impl* deben proporcionar un {@link WebTestClient} ya
 * configurado (con el controller real + servicio mockeado/stateful).
 */
public abstract class AnimalitosControllerV1ContractTest {

    protected static final String BASE = "/api/v1/animalitos";

    protected abstract WebTestClient webTestClient();

    // -------------------- POST --------------------

    @Test
    void post_creaYDevuelve201() {
        webTestClient().post().uri(BASE)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(Map.of("nombre", "Lucas", "especie", "GATO", "edad", 3))
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.id").exists()
                .jsonPath("$.nombre").isEqualTo("Lucas")
                .jsonPath("$.especie").isEqualTo("GATO")
                .jsonPath("$.edad").isEqualTo(3);
    }

    @Test
    void post_devuelve400_siDatosInvalidos() {
        webTestClient().post().uri(BASE)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(Map.of("nombre", "X", "especie", "DRAGON", "edad", 999))
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void post_devuelve409_siNombreYaExiste() {
        crearAnimal("Lucas", "GATO", 3);

        webTestClient().post().uri(BASE)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(Map.of("nombre", "Lucas", "especie", "PERRO", "edad", 5))
                .exchange()
                .expectStatus().isEqualTo(409);
    }

    // -------------------- GET --------------------

    @Test
    void getById_devuelve200_cuandoExiste() {
        String id = crearAnimal("Lucas", "GATO", 3);

        webTestClient().get().uri(BASE + "/" + id)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(id)
                .jsonPath("$.nombre").isEqualTo("Lucas");
    }

    @Test
    void getById_devuelve404_cuandoNoExiste() {
        webTestClient().get().uri(BASE + "/INEXISTENTE")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void getAll_devuelve200ConLista() {
        crearAnimal("Lucas", "GATO", 3);
        crearAnimal("Firulais", "PERRO", 5);

        webTestClient().get().uri(BASE)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(AnimalRestDTO.class)
                .hasSize(2);
    }

    // -------------------- PUT --------------------

    @Test
    void put_modifica_devuelve200() {
        String id = crearAnimal("Lucas", "GATO", 3);

        webTestClient().put().uri(BASE + "/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(Map.of("especie", "PERRO", "edad", 7))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.especie").isEqualTo("PERRO")
                .jsonPath("$.edad").isEqualTo(7);
    }

    @Test
    void put_devuelve404_siNoExiste() {
        webTestClient().put().uri(BASE + "/FANTASMA")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(Map.of("especie", "GATO", "edad", 1))
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void put_devuelve400_siDatosInvalidos() {
        String id = crearAnimal("Lucas", "GATO", 3);

        webTestClient().put().uri(BASE + "/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(Map.of("especie", "DRAGON", "edad", -1))
                .exchange()
                .expectStatus().isBadRequest();
    }

    // -------------------- DELETE --------------------

    @Test
    void delete_devuelve200_cuandoExiste() {
        String id = crearAnimal("Lucas", "GATO", 3);

        webTestClient().delete().uri(BASE + "/" + id)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(id);

        webTestClient().get().uri(BASE + "/" + id)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void delete_devuelve404_cuandoNoExiste() {
        webTestClient().delete().uri(BASE + "/FANTASMA")
                .exchange()
                .expectStatus().isNotFound();
    }

    // -------------------- Helpers --------------------

    private String crearAnimal(String nombre, String especie, int edad) {
        AnimalRestDTO creado = webTestClient().post().uri(BASE)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(Map.of("nombre", nombre, "especie", especie, "edad", edad))
                .exchange()
                .expectStatus().isCreated()
                .expectBody(AnimalRestDTO.class)
                .returnResult()
                .getResponseBody();
        assertThat(creado).isNotNull();
        assertThat(creado.id()).isNotBlank();
        return creado.id();
    }
}
