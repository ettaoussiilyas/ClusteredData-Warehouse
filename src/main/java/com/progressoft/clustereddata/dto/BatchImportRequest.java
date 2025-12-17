package com.progressoft/clustereddata.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BatchImportRequest {

    @NotEmpty(message = "Deals list cannot be empty")
    @Valid
    private List<FxDealRequest> deals;
}
