package br.com.apimarketplace.validator;

import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordConstraintValidator implements ConstraintValidator<ValidPassword,String> {
    private static final String Password_Pattern = "^(?=.*[0-9])"
                                                + "(?=.*[a-z])"
                                                + "(?=.*[A-Z])"
                                                + "(?=.*[@#$%^&+=])"
                                                + "(?=\\S+$).{8,20}$";

    @Override
    public void initialize(ValidPassword constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value ==null){
            return false;
        }
        return value.matches(Password_Pattern);
    }
}
