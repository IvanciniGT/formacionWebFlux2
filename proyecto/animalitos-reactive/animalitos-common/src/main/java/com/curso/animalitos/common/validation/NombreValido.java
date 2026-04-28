package com.curso.animalitos.common.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.ReportAsSingleViolation;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Nombre valido de un animal.
 *
 * Implementada como CONSTRAINT COMPOSITION: no tiene un ConstraintValidator
 * propio (validatedBy = {}); en su lugar combina las anotaciones estandar
 * de Jakarta Bean Validation. Hibernate Validator las evalua todas y, gracias
 * a {@link ReportAsSingleViolation}, reporta una unica violacion con este
 * mensaje cuando alguna falla.
 */
@NotBlank
@Size(min = 2, max = 60)
@ReportAsSingleViolation
@Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.METHOD, ElementType.RECORD_COMPONENT})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {})
public @interface NombreValido {
    String message() default "El nombre no es valido (debe tener entre 2 y 60 caracteres)";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
