package com.curso.animalitos.repository.impl.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;


@Table(name = "ANIMALITOS")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AnimalEntity {

    @Id
    @Column("ID")

    private Long id;

    @Column("PUBLIC_ID")
    private String publicId = UUID.randomUUID().toString();

    @Column("NOMBRE")
    private String nombre;

    @Column("ESPECIE")
    private String especie;

    @Column("EDAD")
    private int edad;

}

/**
 * Antes haciamos un script SQL para crear las tablas
 * CREATE TABLE ANIMALITOS (
 *     ID VARCHAR(64) PRIMARY KEY,
 *     NOMBRE VARCHAR(60) NOT NULL,
 *     ESPECIE VARCHAR(30) NOT NULL,
 *     EDAD INT NOT NULL
 * );   
 */
