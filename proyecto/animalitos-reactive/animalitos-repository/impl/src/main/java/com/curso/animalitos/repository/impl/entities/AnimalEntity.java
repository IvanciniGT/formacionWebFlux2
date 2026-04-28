package com.curso.animalitos.repository.impl.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;

@Entity
@Table(name = "ANIMALITOS")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AnimalEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false, length = 64)

    private Long id;

    @Column(name = "PUBLIC_ID", nullable = false, length = 64)
    private String publicId = UUID.randomUUID().toString();

    @Column(name = "NOMBRE", nullable = false, length = 60)
    private String nombre;

    @Column(name = "ESPECIE", nullable = false, length = 30)
    private String especie;

    @Column(name = "EDAD", nullable = false)
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
