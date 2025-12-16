package com.progressoft.clustereddata.validation;

import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CurrencyCodeValidatorTest {

    private CurrencyCodeValidator validator;

    @Mock
    private ConstraintValidatorContext context;

    @Mock
    private ConstraintValidatorContext.ConstraintViolationBuilder violationBuilder;

    @BeforeEach
    void setUp() {
        validator = new CurrencyCodeValidator();
        when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(violationBuilder);
    }

    @Test
    void shouldAcceptValidCurrencyCodes() {
        // Valid ISO 4217 currency codes
        assertThat(validator.isValid("USD", context)).isTrue();
        assertThat(validator.isValid("EUR", context)).isTrue();
        assertThat(validator.isValid("GBP", context)).isTrue();
        assertThat(validator.isValid("JPY", context)).isTrue();
        assertThat(validator.isValid("CHF", context)).isTrue();
        assertThat(validator.isValid("CAD", context)).isTrue();
        assertThat(validator.isValid("AUD", context)).isTrue();
    }

    @Test
    void shouldRejectNullCurrencyCode() {
        assertThat(validator.isValid(null, context)).isFalse();
    }

    @Test
    void shouldRejectEmptyCurrencyCode() {
        assertThat(validator.isValid("", context)).isFalse();
        assertThat(validator.isValid("   ", context)).isFalse();
    }

    @Test
    void shouldRejectCurrencyCodeWithIncorrectLength() {
        assertThat(validator.isValid("US", context)).isFalse();
        assertThat(validator.isValid("USDD", context)).isFalse();
        verify(context, atLeastOnce()).disableDefaultConstraintViolation();
        verify(context, atLeastOnce()).buildConstraintViolationWithTemplate(contains("exactly 3 characters"));
    }

    @Test
    void shouldRejectInvalidCurrencyCode() {
        assertThat(validator.isValid("XXX", context)).isFalse();
        assertThat(validator.isValid("ABC", context)).isFalse();
        assertThat(validator.isValid("ZZZ", context)).isFalse();
        verify(context, atLeastOnce()).disableDefaultConstraintViolation();
        verify(context, atLeastOnce()).buildConstraintViolationWithTemplate(contains("not a valid ISO 4217 code"));
    }

    @Test
    void shouldAcceptLowercaseCurrencyCodes() {
        // Validator should handle case conversion
        assertThat(validator.isValid("usd", context)).isTrue();
        assertThat(validator.isValid("eur", context)).isTrue();
    }

    @Test
    void shouldRejectCurrencyCodeWithSpecialCharacters() {
        assertThat(validator.isValid("US$", context)).isFalse();
        assertThat(validator.isValid("U-D", context)).isFalse();
    }

    @Test
    void shouldRejectCurrencyCodeWithNumbers() {
        assertThat(validator.isValid("US1", context)).isFalse();
        assertThat(validator.isValid("12D", context)).isFalse();
    }
}
