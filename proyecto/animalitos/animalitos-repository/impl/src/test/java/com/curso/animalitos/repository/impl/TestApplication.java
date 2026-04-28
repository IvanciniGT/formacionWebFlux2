package com.curso.animalitos.repository.impl;

import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;

/**
 * Configuracion minima para que {@code @DataJpaTest} encuentre un
 * {@code @SpringBootConfiguration} al subir por los paquetes en los tests
 * de este modulo (que no tiene clase main).
 */
@SpringBootConfiguration
@EnableAutoConfiguration
public class TestApplication {
}
