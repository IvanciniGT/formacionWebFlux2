package com.curso.animalitos.controller.impl;

import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

/**
 * Stub para que @WebMvcTest encuentre un @SpringBootConfiguration en este modulo
 * (no hay clase main aqui, vive en animalitos-app). Necesita @ComponentScan
 * para que @WebMvcTest tenga sobre que aplicar su filtro de controladores.
 */
@SpringBootConfiguration
@EnableAutoConfiguration
@ComponentScan
class TestApplication {
}
