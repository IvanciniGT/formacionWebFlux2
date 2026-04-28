package com.curso.animalitos.app;

import com.curso.animalitos.controller.api.models.AnimalRestDTO;
import com.curso.animalitos.controller.api.models.CrearAnimalRequestDTO;
import com.curso.animalitos.controller.api.models.ModificarAnimalRequestDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * E2E de verdad: golpea el endpoint REST con el contexto Spring Boot completo
 * arrancado en un puerto random Y, ademas, comprueba con SQL directo contra la
 * BBDD H2 que la fila acaba realmente persistida (con sus columnas correctas).
 *
 * Recorre el ciclo POST -> SELECT -> GET -> PUT -> SELECT -> DELETE -> SELECT vacio,
 * para no fiarnos solo de lo que la API nos cuenta por HTTP.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AnimalitosE2ETest {

    @Autowired
    private TestRestTemplate rest;

    @Autowired
    private JdbcTemplate jdbc;

    @Test
    void e2e_cicloCompletoConVerificacionEnBaseDeDatos() {
        String nombre = "Lucas-" + System.nanoTime();

        // --- POST /api/v1/animalitos ---
        ResponseEntity<AnimalRestDTO> creado = rest.postForEntity(
                "/api/v1/animalitos",
                new CrearAnimalRequestDTO(nombre, "GATO", 3),
                AnimalRestDTO.class);

        assertThat(creado.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(creado.getBody()).isNotNull();
        String publicId = creado.getBody().id();
        assertThat(publicId).isNotBlank();

        // --- Verificacion en BBDD: la fila esta persistida con los datos correctos ---
        Map<String, Object> fila = jdbc.queryForMap(
                "SELECT PUBLIC_ID, NOMBRE, ESPECIE, EDAD FROM ANIMALITOS WHERE PUBLIC_ID = ?",
                publicId);
        assertThat(fila.get("PUBLIC_ID")).isEqualTo(publicId);
        assertThat(fila.get("NOMBRE")).isEqualTo(nombre);
        assertThat(fila.get("ESPECIE")).isEqualTo("GATO");
        assertThat(((Number) fila.get("EDAD")).intValue()).isEqualTo(3);

        // --- GET /api/v1/animalitos/{id} ---
        ResponseEntity<AnimalRestDTO> hallado = rest.getForEntity(
                "/api/v1/animalitos/" + publicId, AnimalRestDTO.class);
        assertThat(hallado.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(hallado.getBody()).isNotNull();
        assertThat(hallado.getBody().id()).isEqualTo(publicId);
        assertThat(hallado.getBody().nombre()).isEqualTo(nombre);

        // --- PUT /api/v1/animalitos/{id}: cambia especie y edad ---
        rest.put("/api/v1/animalitos/" + publicId,
                new ModificarAnimalRequestDTO("PERRO", 7));

        // Verificamos en BBDD que el UPDATE fue de verdad y el nombre sigue intacto
        Map<String, Object> filaModif = jdbc.queryForMap(
                "SELECT NOMBRE, ESPECIE, EDAD FROM ANIMALITOS WHERE PUBLIC_ID = ?",
                publicId);
        assertThat(filaModif.get("NOMBRE")).isEqualTo(nombre);
        assertThat(filaModif.get("ESPECIE")).isEqualTo("PERRO");
        assertThat(((Number) filaModif.get("EDAD")).intValue()).isEqualTo(7);

        // --- DELETE /api/v1/animalitos/{id} ---
        rest.delete("/api/v1/animalitos/" + publicId);

        // Verificamos en BBDD que la fila ya no existe
        Integer count = jdbc.queryForObject(
                "SELECT COUNT(*) FROM ANIMALITOS WHERE PUBLIC_ID = ?",
                Integer.class, publicId);
        assertThat(count).isZero();

        // Y que el GET por HTTP tambien devuelve 404
        ResponseEntity<String> tras = rest.getForEntity(
                "/api/v1/animalitos/" + publicId, String.class);
        assertThat(tras.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}
