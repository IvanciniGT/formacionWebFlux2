package com.curso.animalitos.repository.impl;

import com.curso.animalitos.repository.api.AnimalitosRepository;
import com.curso.animalitos.repository.api.AnimalitosRepositoryContractTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.validation.ValidationAutoConfiguration;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;

/**
 * Ejecuta el contrato (caja negra) heredado del test-jar de animalitos-repository-api,
 * contra la implementacion JPA real, sobre H2 en memoria.
 *
 * El bean del repositorio se obtiene por INYECCION (no se construye con
 * {@code new}), asi llega decorado con los proxies de Spring, incluido el
 * de {@code @Validated} que es el que hace cumplir las anotaciones de
 * bean validation del contrato (los tests de "datos podridos").
 */
@DataJpaTest
@ImportAutoConfiguration(ValidationAutoConfiguration.class)
@ComponentScan(basePackageClasses = AnimalitosRepositoryJpaImpl.class)
class AnimalitosRepositoryJpaImplContractTest extends AnimalitosRepositoryContractTest {

    @Autowired
    private AnimalEntityCrudRepository jpa;

    @Autowired
    private AnimalitosRepository repositorio;

    @Override
    protected AnimalitosRepository crearRepositorio() {
        // Limpia la BD entre tests; el bean (con su proxy) es el mismo.
        jpa.deleteAll();
        return repositorio;
    }
}
