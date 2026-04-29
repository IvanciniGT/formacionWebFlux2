package com.curso.animalitos.app;

import com.curso.animalitos.controller.api.models.AnimalRestDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * E2E reactivo: arranca el contexto Spring Boot completo en un puerto random
 * sobre WebFlux y golpea los endpoints con {@link WebTestClient}. Ademas
 * verifica con SQL directo (via R2DBC {@link DatabaseClient}) que la fila
 * acaba realmente persistida en H2, para no fiarnos solo de lo que la API
 * cuenta por HTTP.
 *
 * Recorre POST -> SELECT -> GET -> PUT -> SELECT -> DELETE -> SELECT vacio.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
class AnimalitosE2ETest {

    @Autowired
    private WebTestClient web;

    @Autowired
    private DatabaseClient db;

    @Test
    void e2e_cicloCompletoConVerificacionEnBaseDeDatos() {
        String nombre = "Lucas-" + System.nanoTime();

        // --- POST /api/v1/animalitos ---
        AnimalRestDTO creado = web.post().uri("/api/v1/animalitos")
                .bodyValue(Map.of("nombre", nombre, "especie", "GATO", "edad", 3))
                .exchange()
                .expectStatus().isCreated()
                .expectBody(AnimalRestDTO.class)
                .returnResult()
                .getResponseBody();

        assertThat(creado).isNotNull();
        String publicId = creado.id();
        assertThat(publicId).isNotBlank();

        // --- Verificacion en BBDD: la fila esta persistida con los datos correctos ---
        Map<String, Object> fila = db.sql("SELECT PUBLIC_ID, NOMBRE, ESPECIE, EDAD FROM ANIMALITOS WHERE PUBLIC_ID = :pid")
                .bind("pid", publicId)
                .fetch()
                .one()
                .block();
        assertThat(fila).isNotNull();
        assertThat(fila.get("PUBLIC_ID")).isEqualTo(publicId);
        assertThat(fila.get("NOMBRE")).isEqualTo(nombre);
        assertThat(fila.get("ESPECIE")).isEqualTo("GATO");
        assertThat(((Number) fila.get("EDAD")).intValue()).isEqualTo(3);

        // --- GET /api/v1/animalitos/{id} ---
        web.get().uri("/api/v1/animalitos/" + publicId)
                .exchange()
                .expectStatus().isOk()
                .expectBody(AnimalRestDTO.class)
                .value(a -> {
                    assertThat(a.id()).isEqualTo(publicId);
                    assertThat(a.nombre()).isEqualTo(nombre);
                });

        // --- PUT /api/v1/animalitos/{id}: cambia especie y edad ---
        web.put().uri("/api/v1/animalitos/" + publicId)
                .bodyValue(Map.of("especie", "PERRO", "edad", 7))
                .exchange()
                .expectStatus().isOk();

        // Verificamos en BBDD que el UPDATE fue de verdad y el nombre sigue intacto
        Map<String, Object> filaModif = db.sql("SELECT NOMBRE, ESPECIE, EDAD FROM ANIMALITOS WHERE PUBLIC_ID = :pid")
                .bind("pid", publicId)
                .fetch()
                .one()
                .block();
        assertThat(filaModif).isNotNull();
        assertThat(filaModif.get("NOMBRE")).isEqualTo(nombre);
        assertThat(filaModif.get("ESPECIE")).isEqualTo("PERRO");
        assertThat(((Number) filaModif.get("EDAD")).intValue()).isEqualTo(7);

        // --- DELETE /api/v1/animalitos/{id} ---
        web.delete().uri("/api/v1/animalitos/" + publicId)
                .exchange()
                .expectStatus().isOk();

        // Verificamos en BBDD que la fila ya no existe
        Long count = db.sql("SELECT COUNT(*) AS C FROM ANIMALITOS WHERE PUBLIC_ID = :pid")
                .bind("pid", publicId)
                .map((row, meta) -> ((Number) row.get("C")).longValue())
                .one()
                .block();
        assertThat(count).isZero();

        // Y que el GET por HTTP tambien devuelve 404
        web.get().uri("/api/v1/animalitos/" + publicId)
                .exchange()
                .expectStatus().isNotFound();
    }
}
