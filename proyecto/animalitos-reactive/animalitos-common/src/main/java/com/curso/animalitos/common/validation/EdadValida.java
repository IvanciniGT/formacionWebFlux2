package com.curso.animalitos.common.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.ReportAsSingleViolation;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Edad razonable de un animal (0..100).
 *
 * Constraint composition: combina {@code @NotNull}, {@code @Min} y {@code @Max}
 * estandar. Sin ConstraintValidator propio.
 */
@NotNull
@Min(0)
@Max(100)
@ReportAsSingleViolation
@Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.METHOD, ElementType.RECORD_COMPONENT})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {})
public @interface EdadValida {
    String message() default "La edad no es valida (debe estar entre 0 y 100)";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
