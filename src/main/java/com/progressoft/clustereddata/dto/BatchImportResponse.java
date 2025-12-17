package com.progressoft/clustereddata.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BatchImportResponse {

    private int totalProcessed;
    private int successCount;
    private int failedCount;
    private int duplicateCount;
    private List<String> successfulDeals;
    private List<FailedDeal> failedDeals;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class FailedDeal {
        private String dealUniqueId;
        private String reason;
        private FailureType type;
    }

    public enum FailureType {
        VALIDATION_ERROR,
        DUPLICATE,
        PROCESSING_ERROR
    }
}
