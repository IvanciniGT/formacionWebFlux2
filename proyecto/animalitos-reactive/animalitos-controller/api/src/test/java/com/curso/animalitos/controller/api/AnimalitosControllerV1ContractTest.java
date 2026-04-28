package com.curso.animalitos.controller.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Contrato REST (caja negra) para AnimalitosControllerV1.
 *
 * Subclases en *impl* deben proporcionar un {@link MockMvc} ya configurado
 * (con el controller real + servicio mockeado/stateful).
 */
public abstract class AnimalitosControllerV1ContractTest {

    protected static final String BASE = "/api/v1/animalitos";
    private final ObjectMapper json = new ObjectMapper();

    protected abstract MockMvc mockMvc();

    // -------------------- POST --------------------

    @Test
    void post_creaYDevuelve201() throws Exception {
        mockMvc().perform(post(BASE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json.writeValueAsString(Map.of(
                                "nombre", "Lucas",
                                "especie", "GATO",
                                "edad", 3))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.nombre").value("Lucas"))
                .andExpect(jsonPath("$.especie").value("GATO"))
                .andExpect(jsonPath("$.edad").value(3));
    }

    @Test
    void post_devuelve400_siDatosInvalidos() throws Exception {
        mockMvc().perform(post(BASE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json.writeValueAsString(Map.of(
                                "nombre", "X",
                                "especie", "DRAGON",
                                "edad", 999))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void post_devuelve409_siNombreYaExiste() throws Exception {
        crearAnimal("Lucas", "GATO", 3);

        mockMvc().perform(post(BASE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json.writeValueAsString(Map.of(
                                "nombre", "Lucas",
                                "especie", "PERRO",
                                "edad", 5))))
                .andExpect(status().isConflict());
    }

    // -------------------- GET --------------------

    @Test
    void getById_devuelve200_cuandoExiste() throws Exception {
        String id = crearAnimal("Lucas", "GATO", 3);

        mockMvc().perform(get(BASE + "/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.nombre").value("Lucas"));
    }

    @Test
    void getById_devuelve404_cuandoNoExiste() throws Exception {
        mockMvc().perform(get(BASE + "/INEXISTENTE"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAll_devuelve200ConLista() throws Exception {
        crearAnimal("Lucas", "GATO", 3);
        crearAnimal("Firulais", "PERRO", 5);

        mockMvc().perform(get(BASE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    // -------------------- PUT --------------------

    @Test
    void put_modifica_devuelve200() throws Exception {
        String id = crearAnimal("Lucas", "GATO", 3);

        mockMvc().perform(put(BASE + "/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json.writeValueAsString(Map.of(
                                "especie", "PERRO",
                                "edad", 7))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.especie").value("PERRO"))
                .andExpect(jsonPath("$.edad").value(7));
    }

    @Test
    void put_devuelve404_siNoExiste() throws Exception {
        mockMvc().perform(put(BASE + "/FANTASMA")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json.writeValueAsString(Map.of(
                                "especie", "GATO",
                                "edad", 1))))
                .andExpect(status().isNotFound());
    }

    @Test
    void put_devuelve400_siDatosInvalidos() throws Exception {
        String id = crearAnimal("Lucas", "GATO", 3);

        mockMvc().perform(put(BASE + "/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json.writeValueAsString(Map.of(
                                "especie", "DRAGON",
                                "edad", -1))))
                .andExpect(status().isBadRequest());
    }

    // -------------------- DELETE --------------------

    @Test
    void delete_devuelve200_cuandoExiste() throws Exception {
        String id = crearAnimal("Lucas", "GATO", 3);

        mockMvc().perform(delete(BASE + "/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id));

        mockMvc().perform(get(BASE + "/" + id))
                .andExpect(status().isNotFound());
    }

    @Test
    void delete_devuelve404_cuandoNoExiste() throws Exception {
        mockMvc().perform(delete(BASE + "/FANTASMA"))
                .andExpect(status().isNotFound());
    }

    // -------------------- Helpers --------------------

    private String crearAnimal(String nombre, String especie, int edad) throws Exception {
        MvcResult r = mockMvc().perform(post(BASE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json.writeValueAsString(Map.of(
                                "nombre", nombre,
                                "especie", especie,
                                "edad", edad))))
                .andExpect(status().isCreated())
                .andReturn();
        Map<?, ?> body = json.readValue(r.getResponse().getContentAsByteArray(), Map.class);
        String id = (String) body.get("id");
        assertThat(id).isNotBlank();
        return id;
    }
}
