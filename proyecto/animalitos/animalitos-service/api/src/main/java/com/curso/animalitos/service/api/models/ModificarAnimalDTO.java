package com.curso.animalitos.service.api.models;

import com.curso.animalitos.common.validation.EdadValida;
import com.curso.animalitos.common.validation.EspecieValida;

/**
 * Datos de entrada para modificar un animal en la capa de servicio.
 * El nombre no se puede cambiar (es la clave funcional).
 */
public record ModificarAnimalDTO(
        @EspecieValida String especie,
        @EdadValida Integer edad) {
}
