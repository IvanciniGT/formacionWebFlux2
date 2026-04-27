package com.curso.animalitos.domain;

/**
 * Animal a nivel de DOMINIO (puro, sin dependencias tecnologicas).
 *
 * Es la moneda de cambio entre la capa de servicio y la de persistencia:
 *  - El repositorio recibe y devuelve Animal.
 *  - El servicio mapea entre Animal (dominio) y sus propios DTOs publicos.
 *
 * Convencion: en operaciones de creacion el campo id puede ser null
 * (la persistencia lo generara y lo devolvera relleno).
 */
public record Animal(
        String id,
        String nombre,
        String especie,
        int edad
) {
}
