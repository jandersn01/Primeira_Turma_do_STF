package br.edu.ifpb.pweb2.primeiraturmadostf.anotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import br.edu.ifpb.pweb2.primeiraturmadostf.validators.MatriculaValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Constraint(validatedBy= MatriculaValidator.class)
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface Matricula {
    String message() default "Matrícula inválida";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
