package com.curso.animalitos.service.api.models;

import com.curso.animalitos.common.validation.EdadValida;
import com.curso.animalitos.common.validation.EspecieValida;
import com.curso.animalitos.common.validation.NombreValido;

/**
 * Datos de entrada para crear un animal en la capa de servicio.
 *
 * Las validaciones aplican a CUALQUIER cliente del servicio (controller v1,
 * un futuro controller v2, un endpoint SOAP, un job batch...). La capa
 * controlador puede repetirlas como cortesia para fallar antes, pero las
 * reglas de negocio viven aqui.
 */
public record CrearAnimalDTO(
        @NombreValido String nombre,
        @EspecieValida String especie,
        @EdadValida Integer edad) {
}
