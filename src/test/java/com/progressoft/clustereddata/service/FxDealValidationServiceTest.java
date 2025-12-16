package com.progressoft.clustereddata.service;

import com.progressoft.clustereddata.entity.FxDeal;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FxDealValidationServiceTest {

    private FxDealValidationService validationService;
    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        validationService = new FxDealValidationService(validator);
    }

    @Test
    void shouldValidateValidFxDeal() {
        FxDeal deal = createValidFxDeal();

        List<String> errors = validationService.validate(deal);

        assertThat(errors).isEmpty();
        assertThat(validationService.isValid(deal)).isTrue();
    }

    @Test
    void shouldDetectNullDealUniqueId() {
        FxDeal deal = createValidFxDeal();
        deal.setDealUniqueId(null);

        List<String> errors = validationService.validate(deal);

        assertThat(errors).isNotEmpty();
        assertThat(errors).anyMatch(error -> error.contains("Deal unique ID is required"));
        assertThat(validationService.isValid(deal)).isFalse();
    }

    @Test
    void shouldDetectBlankDealUniqueId() {
        FxDeal deal = createValidFxDeal();
        deal.setDealUniqueId("   ");

        List<String> errors = validationService.validate(deal);

        assertThat(errors).isNotEmpty();
        assertThat(errors).anyMatch(error -> error.contains("Deal unique ID cannot be blank"));
    }

    @Test
    void shouldDetectInvalidFromCurrency() {
        FxDeal deal = createValidFxDeal();
        deal.setFromCurrencyIsoCode("XXX");

        List<String> errors = validationService.validate(deal);

        assertThat(errors).isNotEmpty();
        assertThat(errors).anyMatch(error -> error.contains("not a valid ISO 4217 code"));
    }

    @Test
    void shouldDetectInvalidToCurrency() {
        FxDeal deal = createValidFxDeal();
        deal.setToCurrencyIsoCode("ABC");

        List<String> errors = validationService.validate(deal);

        assertThat(errors).isNotEmpty();
        assertThat(errors).anyMatch(error -> error.contains("not a valid ISO 4217 code"));
    }

    @Test
    void shouldDetectNullFromCurrency() {
        FxDeal deal = createValidFxDeal();
        deal.setFromCurrencyIsoCode(null);

        List<String> errors = validationService.validate(deal);

        assertThat(errors).isNotEmpty();
        assertThat(errors).anyMatch(error -> error.contains("From currency ISO code is required"));
    }

    @Test
    void shouldDetectNullToCurrency() {
        FxDeal deal = createValidFxDeal();
        deal.setToCurrencyIsoCode(null);

        List<String> errors = validationService.validate(deal);

        assertThat(errors).isNotEmpty();
        assertThat(errors).anyMatch(error -> error.contains("To currency ISO code is required"));
    }

    @Test
    void shouldDetectFutureTimestamp() {
        FxDeal deal = createValidFxDeal();
        deal.setDealTimestamp(LocalDateTime.now().plusDays(1));

        List<String> errors = validationService.validate(deal);

        assertThat(errors).isNotEmpty();
        assertThat(errors).anyMatch(error -> error.contains("Deal timestamp cannot be in the future"));
    }

    @Test
    void shouldDetectNullTimestamp() {
        FxDeal deal = createValidFxDeal();
        deal.setDealTimestamp(null);

        List<String> errors = validationService.validate(deal);

        assertThat(errors).isNotEmpty();
        assertThat(errors).anyMatch(error -> error.contains("Deal timestamp is required"));
    }

    @Test
    void shouldDetectNegativeAmount() {
        FxDeal deal = createValidFxDeal();
        deal.setDealAmount(new BigDecimal("-100.00"));

        List<String> errors = validationService.validate(deal);

        assertThat(errors).isNotEmpty();
        assertThat(errors).anyMatch(error -> error.contains("positive number"));
    }

    @Test
    void shouldDetectZeroAmount() {
        FxDeal deal = createValidFxDeal();
        deal.setDealAmount(BigDecimal.ZERO);

        List<String> errors = validationService.validate(deal);

        assertThat(errors).isNotEmpty();
        assertThat(errors).anyMatch(error -> error.contains("positive number") || error.contains("at least 0.0001"));
    }

    @Test
    void shouldDetectNullAmount() {
        FxDeal deal = createValidFxDeal();
        deal.setDealAmount(null);

        List<String> errors = validationService.validate(deal);

        assertThat(errors).isNotEmpty();
        assertThat(errors).anyMatch(error -> error.contains("Deal amount is required"));
    }

    @Test
    void shouldDetectAmountBelowMinimum() {
        FxDeal deal = createValidFxDeal();
        deal.setDealAmount(new BigDecimal("0.00001"));

        List<String> errors = validationService.validate(deal);

        assertThat(errors).isNotEmpty();
        assertThat(errors).anyMatch(error -> error.contains("at least 0.0001"));
    }

    @Test
    void shouldDetectMultipleValidationErrors() {
        FxDeal deal = new FxDeal();
        deal.setDealUniqueId(null);
        deal.setFromCurrencyIsoCode(null);
        deal.setToCurrencyIsoCode(null);
        deal.setDealTimestamp(null);
        deal.setDealAmount(null);

        List<String> errors = validationService.validate(deal);

        assertThat(errors).hasSize(5);
    }

    @Test
    void shouldThrowExceptionWhenValidateOrThrowCalledWithInvalidDeal() {
        FxDeal deal = createValidFxDeal();
        deal.setDealUniqueId(null);

        assertThatThrownBy(() -> validationService.validateOrThrow(deal))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("FX deal validation failed");
    }

    @Test
    void shouldNotThrowExceptionWhenValidateOrThrowCalledWithValidDeal() {
        FxDeal deal = createValidFxDeal();

        validationService.validateOrThrow(deal);
        // No exception should be thrown
    }

    @Test
    void shouldAcceptValidCurrencyCodes() {
        FxDeal deal = createValidFxDeal();
        deal.setFromCurrencyIsoCode("USD");
        deal.setToCurrencyIsoCode("EUR");

        assertThat(validationService.isValid(deal)).isTrue();

        deal.setFromCurrencyIsoCode("GBP");
        deal.setToCurrencyIsoCode("JPY");

        assertThat(validationService.isValid(deal)).isTrue();
    }

    @Test
    void shouldAcceptMinimumValidAmount() {
        FxDeal deal = createValidFxDeal();
        deal.setDealAmount(new BigDecimal("0.0001"));

        List<String> errors = validationService.validate(deal);

        assertThat(errors).isEmpty();
    }

    @Test
    void shouldAcceptPastAndPresentTimestamps() {
        FxDeal deal = createValidFxDeal();
        
        // Test past timestamp
        deal.setDealTimestamp(LocalDateTime.now().minusDays(1));
        assertThat(validationService.isValid(deal)).isTrue();

        // Test present timestamp
        deal.setDealTimestamp(LocalDateTime.now());
        assertThat(validationService.isValid(deal)).isTrue();
    }

    private FxDeal createValidFxDeal() {
        FxDeal deal = new FxDeal();
        deal.setDealUniqueId("DEAL-12345");
        deal.setFromCurrencyIsoCode("USD");
        deal.setToCurrencyIsoCode("EUR");
        deal.setDealTimestamp(LocalDateTime.now());
        deal.setDealAmount(new BigDecimal("1000.50"));
        return deal;
    }
}
