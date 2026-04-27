package com.curso.animalitos.common.validation;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Verifica que las anotaciones custom funcionan a traves de constraint
 * composition: ningun ConstraintValidator propio, solo apilado de
 * anotaciones estandar de Jakarta Bean Validation evaluadas por el
 * Validator real (Hibernate Validator).
 */
class ValidacionesCompuestasTest {

    private static ValidatorFactory factory;
    private static Validator validator;

    @BeforeAll
    static void init() {
        factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @AfterAll
    static void close() {
        factory.close();
    }

    // ---------- objetos auxiliares con UNA sola constraint cada uno ----------

    private record ConNombre(@NombreValido String v) {}
    private record ConEspecie(@EspecieValida String v) {}
    private record ConEdad(@EdadValida Integer v) {}

    // ---------- @NombreValido ----------

    @Test
    void nombreValido_aceptaValoresEnRango() {
        assertThat(validator.validate(new ConNombre("Lucas"))).isEmpty();
        assertThat(validator.validate(new ConNombre("Bo"))).isEmpty();
    }

    @Test
    void nombreValido_rechazaNuloVacioOFueraDeRango() {
        assertThat(validator.validate(new ConNombre(null))).isNotEmpty();
        assertThat(validator.validate(new ConNombre(""))).isNotEmpty();
        assertThat(validator.validate(new ConNombre("a"))).isNotEmpty();
        assertThat(validator.validate(new ConNombre("x".repeat(61)))).isNotEmpty();
    }

    @Test
    void nombreValido_reportaUnaSolaViolacion_aunqueFallenVariasComposiciones() {
        // null falla @NotBlank y @Size; gracias a @ReportAsSingleViolation se
        // reporta solo una con el message de @NombreValido.
        Set<ConstraintViolation<ConNombre>> violaciones = validator.validate(new ConNombre(null));
        assertThat(violaciones).hasSize(1);
        assertThat(violaciones.iterator().next().getMessage())
                .contains("nombre no es valido");
    }

    // ---------- @EspecieValida ----------

    @Test
    void especieValida_aceptaConocidas() {
        assertThat(validator.validate(new ConEspecie("PERRO"))).isEmpty();
        assertThat(validator.validate(new ConEspecie("GATO"))).isEmpty();
        assertThat(validator.validate(new ConEspecie("TORTUGA"))).isEmpty();
    }

    @Test
    void especieValida_rechazaDesconocidaOMinusculaONula() {
        // La regex es estricta en mayusculas (es la convencion en API publica).
        assertThat(validator.validate(new ConEspecie("perro"))).isNotEmpty();
        assertThat(validator.validate(new ConEspecie("DRAGON"))).isNotEmpty();
        assertThat(validator.validate(new ConEspecie(null))).isNotEmpty();
    }

    // ---------- @EdadValida ----------

    @Test
    void edadValida_aceptaRangoCompleto() {
        assertThat(validator.validate(new ConEdad(0))).isEmpty();
        assertThat(validator.validate(new ConEdad(50))).isEmpty();
        assertThat(validator.validate(new ConEdad(100))).isEmpty();
    }

    @Test
    void edadValida_rechazaFueraDeRangoONulo() {
        assertThat(validator.validate(new ConEdad(-1))).isNotEmpty();
        assertThat(validator.validate(new ConEdad(101))).isNotEmpty();
        assertThat(validator.validate(new ConEdad(null))).isNotEmpty();
    }
}
