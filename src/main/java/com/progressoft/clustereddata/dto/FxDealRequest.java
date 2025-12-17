package com.progressoft.clustereddata.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.progressoft.clustereddata.validation.ValidCurrencyCode;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FxDealRequest {

    @NotNull(message = "Deal unique ID is required")
    @NotBlank(message = "Deal unique ID cannot be blank")
    private String dealUniqueId;

    @NotNull(message = "From currency ISO code is required")
    @ValidCurrencyCode
    private String fromCurrencyIsoCode;

    @NotNull(message = "To currency ISO code is required")
    @ValidCurrencyCode
    private String toCurrencyIsoCode;

    @NotNull(message = "Deal timestamp is required")
    @PastOrPresent(message = "Deal timestamp cannot be in the future")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime dealTimestamp;

    @NotNull(message = "Deal amount is required")
    @Positive(message = "Deal amount must be a positive number")
    @DecimalMin(value = "0.0001", inclusive = true, message = "Deal amount must be at least 0.0001")
    @Digits(integer = 15, fraction = 4, message = "Deal amount must have at most 15 integer digits and 4 decimal places")
    private BigDecimal dealAmount;
}
