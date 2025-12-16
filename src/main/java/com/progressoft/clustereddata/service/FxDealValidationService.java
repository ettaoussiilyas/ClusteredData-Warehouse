package com.progressoft.clustereddata.service;

import com.progressoft.clustereddata.entity.FxDeal;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class FxDealValidationService {

    private final Validator validator;

    /**
     * Validates an FX deal and returns a list of validation error messages
     *
     * @param fxDeal the FX deal to validate
     * @return list of validation error messages, empty if valid
     */
    public List<String> validate(FxDeal fxDeal) {
        log.debug("Validating FX deal: {}", fxDeal);

        Set<ConstraintViolation<FxDeal>> violations = validator.validate(fxDeal);

        if (violations.isEmpty()) {
            log.debug("FX deal validation successful");
            return List.of();
        }

        List<String> errors = violations.stream()
                .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
                .sorted()
                .collect(Collectors.toList());

        log.warn("FX deal validation failed with {} errors: {}", errors.size(), errors);

        return errors;
    }

    /**
     * Validates an FX deal and returns true if valid
     *
     * @param fxDeal the FX deal to validate
     * @return true if valid, false otherwise
     */
    public boolean isValid(FxDeal fxDeal) {
        return validate(fxDeal).isEmpty();
    }

    /**
     * Validates an FX deal and throws an exception if invalid
     *
     * @param fxDeal the FX deal to validate
     * @throws IllegalArgumentException if validation fails
     */
    public void validateOrThrow(FxDeal fxDeal) {
        List<String> errors = validate(fxDeal);
        if (!errors.isEmpty()) {
            String errorMessage = "FX deal validation failed: " + String.join(", ", errors);
            log.error(errorMessage);
            throw new IllegalArgumentException(errorMessage);
        }
    }
}
