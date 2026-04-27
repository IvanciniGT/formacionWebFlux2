package com.curso.animalitos.service.api.models;

/**
 * Vista del Animal en la capa de servicio.
 */
public record AnimalDTO(String id, String nombre, String especie, int edad) {
}
