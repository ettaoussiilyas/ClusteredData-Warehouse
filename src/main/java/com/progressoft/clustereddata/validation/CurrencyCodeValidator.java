package com.progressoft.clustereddata.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Currency;
import java.util.Set;
import java.util.stream.Collectors;

public class CurrencyCodeValidator implements ConstraintValidator<ValidCurrencyCode, String> {

    private static final Set<String> VALID_CURRENCY_CODES = Currency.getAvailableCurrencies()
            .stream()
            .map(Currency::getCurrencyCode)
            .collect(Collectors.toSet());

    @Override
    public void initialize(ValidCurrencyCode constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String currencyCode, ConstraintValidatorContext context) {
        if (currencyCode == null || currencyCode.trim().isEmpty()) {
            return false;
        }

        // Check on 3 characters
        if (currencyCode.length() != 3) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                    "Currency code must be exactly 3 characters"
            ).addConstraintViolation();
            return false;
        }

        // Check if it's a valid ISO 4217 code
        if (!VALID_CURRENCY_CODES.contains(currencyCode.toUpperCase())) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                    "Currency code '" + currencyCode + "' is not a valid ISO 4217 code"
            ).addConstraintViolation();
            return false;
        }

        return true;
    }
}
