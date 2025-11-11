package com.ufvjm.estagios.infra.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = DominioEspecificoValidator.class)
public @interface DominioEspecifico {

    String dominio();

    String message() default "O e-mail deve pertencer ao domínio institucional.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
