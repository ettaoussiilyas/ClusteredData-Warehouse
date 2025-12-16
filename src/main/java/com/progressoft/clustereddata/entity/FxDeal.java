package com.progressoft.clustereddata.entity;

import com.progressoft.clustereddata.validation.ValidCurrencyCode;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "fx_deals", uniqueConstraints = {
    @UniqueConstraint(columnNames = "deal_unique_id")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FxDeal {

    @Id
    @NotNull(message = "Deal unique ID is required")
    @NotBlank(message = "Deal unique ID cannot be blank")
    @Column(name = "deal_unique_id", nullable = false, unique = true)
    private String dealUniqueId;

    @NotNull(message = "From currency ISO code is required")
    @ValidCurrencyCode
    @Column(name = "from_currency_iso_code", nullable = false, length = 3)
    private String fromCurrencyIsoCode;

    @NotNull(message = "To currency ISO code is required")
    @ValidCurrencyCode
    @Column(name = "to_currency_iso_code", nullable = false, length = 3)
    private String toCurrencyIsoCode;

    @NotNull(message = "Deal timestamp is required")
    @PastOrPresent(message = "Deal timestamp cannot be in the future")
    @Column(name = "deal_timestamp", nullable = false)
    private LocalDateTime dealTimestamp;

    @NotNull(message = "Deal amount is required")
    @Positive(message = "Deal amount must be a positive number")
    @DecimalMin(value = "0.0001", inclusive = true, message = "Deal amount must be at least 0.0001")
    @Digits(integer = 15, fraction = 4, message = "Deal amount must have at most 15 integer digits and 4 decimal places")
    @Column(name = "deal_amount", nullable = false, precision = 19, scale = 4)
    private BigDecimal dealAmount;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}