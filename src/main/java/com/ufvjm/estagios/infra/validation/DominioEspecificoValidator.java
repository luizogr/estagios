package com.ufvjm.estagios.infra.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class DominioEspecificoValidator implements ConstraintValidator<DominioEspecifico, String> {
    private String dominioPermitido;

    @Override
    public void initialize(DominioEspecifico constraintAnnotation) {
        this.dominioPermitido = constraintAnnotation.dominio();
    }

    @Override
    public boolean isValid(String email, ConstraintValidatorContext context) {
        if (email == null || email.isBlank()) {
            return true;
        }
        return email.endsWith(dominioPermitido);
    }
}
