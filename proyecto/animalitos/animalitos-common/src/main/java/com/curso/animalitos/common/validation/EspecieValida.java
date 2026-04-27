package com.curso.animalitos.common.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.ReportAsSingleViolation;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Especie valida (lista cerrada).
 *
 * Constraint composition: se apoya en {@code @Pattern} estandar para
 * comprobar que la cadena pertenece al conjunto permitido. No hay
 * ConstraintValidator propio.
 */
@NotBlank
@Pattern(regexp = "^(PERRO|GATO|HAMSTER|CONEJO|LORO|PEZ|TORTUGA)$")
@ReportAsSingleViolation
@Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.METHOD, ElementType.RECORD_COMPONENT})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {})
public @interface EspecieValida {
    String message() default "La especie debe ser una de: PERRO, GATO, HAMSTER, CONEJO, LORO, PEZ, TORTUGA";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
