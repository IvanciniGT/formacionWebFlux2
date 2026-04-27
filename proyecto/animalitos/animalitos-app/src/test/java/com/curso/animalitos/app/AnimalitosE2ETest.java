package com.curso.animalitos.app;

import com.curso.animalitos.controller.api.models.AnimalRestDTO;
import com.curso.animalitos.controller.api.models.CrearAnimalRequestDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AnimalitosE2ETest {

    @Autowired
    private TestRestTemplate rest;

    @Test
    void e2e_crearYRecuperarAnimal() {
        ResponseEntity<AnimalRestDTO> creado = rest.postForEntity(
                "/api/v1/animalitos",
                new CrearAnimalRequestDTO("Lucas-" + System.nanoTime(), "GATO", 3),
                AnimalRestDTO.class);

        assertThat(creado.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(creado.getBody()).isNotNull();
        String id = creado.getBody().id();

        ResponseEntity<AnimalRestDTO> hallado = rest.getForEntity(
                "/api/v1/animalitos/" + id, AnimalRestDTO.class);
        assertThat(hallado.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(hallado.getBody().id()).isEqualTo(id);
    }
}
