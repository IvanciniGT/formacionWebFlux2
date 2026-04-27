package com.curso.animalitos.repository.impl.entities;

import org.junit.jupiter.api.Test;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.lang.reflect.Field;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Pruebas unitarias del entity de JPA.
 *
 * Como un entity solo tiene anotaciones + getters/setters generados por Lombok,
 * comprobamos:
 *  - Que las anotaciones JPA estan en su sitio (mapeo a tabla y columnas).
 *  - Que el constructor por defecto y el de todos los argumentos funcionan.
 *  - Que getters/setters de Lombok se generan correctamente.
 *  - Que el campo publicId se inicializa con un UUID al crear con NoArgsConstructor.
 */
class AnimalEntityTest {

    @Test
    void laClaseEstaAnotadaComoEntityYMapeaALaTablaAnimalitos() {
        assertThat(AnimalEntity.class.isAnnotationPresent(Entity.class)).isTrue();

        Table table = AnimalEntity.class.getAnnotation(Table.class);
        assertThat(table).isNotNull();
        assertThat(table.name()).isEqualTo("ANIMALITOS");
    }

    @Test
    void elCampoIdEsLaClavePrimariaConGeneracionIdentity() throws NoSuchFieldException {
        Field id = AnimalEntity.class.getDeclaredField("id");

        assertThat(id.isAnnotationPresent(Id.class)).isTrue();

        GeneratedValue gv = id.getAnnotation(GeneratedValue.class);
        assertThat(gv).isNotNull();
        assertThat(gv.strategy()).isEqualTo(GenerationType.IDENTITY);

        Column col = id.getAnnotation(Column.class);
        assertThat(col).isNotNull();
        assertThat(col.name()).isEqualTo("ID");
        assertThat(col.nullable()).isFalse();
    }

    @Test
    void elCampoPublicIdSeMapeaAColumnaPublicIdNotNull() throws NoSuchFieldException {
        Column col = AnimalEntity.class.getDeclaredField("publicId").getAnnotation(Column.class);

        assertThat(col).isNotNull();
        assertThat(col.name()).isEqualTo("PUBLIC_ID");
        assertThat(col.nullable()).isFalse();
        assertThat(col.length()).isEqualTo(64);
    }

    @Test
    void elCampoNombreSeMapeaAColumnaNombreConLongitud60() throws NoSuchFieldException {
        Column col = AnimalEntity.class.getDeclaredField("nombre").getAnnotation(Column.class);
        // REQUISITOS: nombre NOT NULL, longitud 60, columna NOMBRE
        assertThat(col).isNotNull();
        assertThat(col.name()).isEqualTo("NOMBRE");
        assertThat(col.nullable()).isFalse();
        assertThat(col.length()).isEqualTo(60);
    }// SOC

    @Test
    void elCampoEspecieSeMapeaAColumnaEspecieConLongitud30() throws NoSuchFieldException {
        Column col = AnimalEntity.class.getDeclaredField("especie").getAnnotation(Column.class);

        assertThat(col).isNotNull();
        assertThat(col.name()).isEqualTo("ESPECIE");
        assertThat(col.nullable()).isFalse();
        assertThat(col.length()).isEqualTo(30);
    }

    @Test
    void elCampoEdadSeMapeaAColumnaEdadNotNull() throws NoSuchFieldException {
        Column col = AnimalEntity.class.getDeclaredField("edad").getAnnotation(Column.class);

        assertThat(col).isNotNull();
        assertThat(col.name()).isEqualTo("EDAD");
        assertThat(col.nullable()).isFalse();
    }

    @Test
    void elConstructorVacioInicializaPublicIdConUnUuidValido() {
        AnimalEntity entity = new AnimalEntity();

        assertThat(entity.getPublicId()).isNotBlank();
        // No debe lanzar: tiene que ser un UUID valido.
        UUID parsed = UUID.fromString(entity.getPublicId());
        assertThat(parsed).isNotNull();
    }

    @Test
    void dosEntidadesNuevasTienenPublicIdsDistintos() {
        AnimalEntity a = new AnimalEntity();
        AnimalEntity b = new AnimalEntity();

        assertThat(a.getPublicId()).isNotEqualTo(b.getPublicId());
    }

    @Test
    void elConstructorConTodosLosArgumentosAsignaCadaCampo() {
        AnimalEntity entity = new AnimalEntity(7L, "uuid-fijo", "Toby", "PERRO", 4);

        assertThat(entity.getId()).isEqualTo(7L);
        assertThat(entity.getPublicId()).isEqualTo("uuid-fijo");
        assertThat(entity.getNombre()).isEqualTo("Toby");
        assertThat(entity.getEspecie()).isEqualTo("PERRO");
        assertThat(entity.getEdad()).isEqualTo(4);
    }

    @Test
    void losSettersDeLombokModificanLosCampos() {
        AnimalEntity entity = new AnimalEntity();

        entity.setId(99L);
        entity.setPublicId("otro-uuid");
        entity.setNombre("Misi");
        entity.setEspecie("GATO");
        entity.setEdad(2);

        assertThat(entity.getId()).isEqualTo(99L);
        assertThat(entity.getPublicId()).isEqualTo("otro-uuid");
        assertThat(entity.getNombre()).isEqualTo("Misi");
        assertThat(entity.getEspecie()).isEqualTo("GATO");
        assertThat(entity.getEdad()).isEqualTo(2);
    }
}