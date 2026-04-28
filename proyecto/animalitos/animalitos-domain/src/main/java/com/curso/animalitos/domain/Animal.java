package com.curso.animalitos.domain;

import com.curso.animalitos.common.validation.EdadValida;
import com.curso.animalitos.common.validation.EspecieValida;
import com.curso.animalitos.common.validation.NombreValido;

/**
 * Animal a nivel de DOMINIO (puro, sin dependencias tecnologicas).
 *
 * Es la moneda de cambio entre la capa de servicio y la de persistencia:
 *  - El repositorio recibe y devuelve Animal.
 *  - El servicio mapea entre Animal (dominio) y sus propios DTOs publicos.
 *
 * Convencion: en operaciones de creacion el campo id puede ser null
 * (la persistencia lo generara y lo devolvera relleno). Por eso id NO
 * lleva validacion: el dominio admite "sin id todavia".
 *
 * Las anotaciones de validacion son las INVARIANTES del dominio: cualquier
 * Animal valido las cumple, da igual quien lo construya.
 */
public record Animal(
        String id,
        @NombreValido String nombre,
        @EspecieValida String especie,
        @EdadValida Integer edad
) {
}
