package com.curso.animalitos.repository.impl;

import com.curso.animalitos.repository.api.AnimalitosRepository;
import com.curso.animalitos.repository.api.AnimalitosRepositoryContractTest;
import com.curso.animalitos.repository.impl.mappers.AnimalRepositoryMapper;
import org.junit.jupiter.api.BeforeEach;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;

/**
 * Ejecuta el contrato (caja negra) heredado del test-jar de animalitos-repository-api,
 * contra la implementacion JPA real, sobre H2 en memoria.
 */
@DataJpaTest
@ComponentScan(basePackageClasses = AnimalitosRepositoryJpaImpl.class)
class AnimalitosRepositoryJpaImplContractIT extends AnimalitosRepositoryContractTest {

    @Autowired
    private AnimalEntityJpaRepository jpa;

    private AnimalitosRepository repositorio;

    @BeforeEach
    void initRepo() {
        jpa.deleteAll();
        AnimalRepositoryMapper mapper = Mappers.getMapper(AnimalRepositoryMapper.class);
        this.repositorio = new AnimalitosRepositoryJpaImpl(jpa, mapper);
    }

    @Override
    protected AnimalitosRepository crearRepositorio() {
        return this.repositorio;
    }
}
