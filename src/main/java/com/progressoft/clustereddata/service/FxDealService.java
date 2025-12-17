package com.progressoft.clustereddata.service;

import com.progressoft.clustereddata.dto.BatchImportRequest;
import com.progressoft.clustereddata.dto.BatchImportResponse;
import com.progressoft.clustereddata.dto.FxDealRequest;
import com.progressoft.clustereddata.dto.FxDealResponse;
import com.progressoft.clustereddata.entity.FxDeal;
import com.progressoft.clustereddata.exception.DealNotFoundException;
import com.progressoft.clustereddata.exception.DuplicateDealException;
import com.progressoft.clustereddata.mapper.FxDealMapper;
import com.progressoft.clustereddata.repository.FxDealRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class FxDealService {

    private final FxDealRepository fxDealRepository;
    private final FxDealValidationService validationService;
    private final FxDealMapper mapper;

    /**
     * Create a new FX deal
     *
     * @param request the FX deal request
     * @return the created FX deal response
     * @throws DuplicateDealException if deal with same ID already exists
     * @throws IllegalArgumentException if validation fails
     */
    @Transactional
    public FxDealResponse createDeal(FxDealRequest request) {
        log.info("Creating FX deal with ID: {}", request.getDealUniqueId());

        // Check for duplicate
        if (fxDealRepository.existsByDealUniqueId(request.getDealUniqueId())) {
            log.warn("Duplicate deal detected: {}", request.getDealUniqueId());
            throw new DuplicateDealException("Deal with ID '" + request.getDealUniqueId() + "' already exists");
        }

        // Convert to entity
        FxDeal deal = mapper.toEntity(request);

        // Validate
        validationService.validateOrThrow(deal);

        // Save
        FxDeal savedDeal = fxDealRepository.save(deal);
        log.info("Successfully created FX deal: {}", savedDeal.getDealUniqueId());

        return mapper.toResponse(savedDeal);
    }

    /**
     * Import multiple FX deals in batch
     *
     * @param request the batch import request
     * @return the batch import response with results
     */
    @Transactional
    public BatchImportResponse importDeals(BatchImportRequest request) {
        log.info("Starting batch import of {} deals", request.getDeals().size());

        List<String> successfulDeals = new ArrayList<>();
        List<BatchImportResponse.FailedDeal> failedDeals = new ArrayList<>();
        int duplicateCount = 0;

        for (FxDealRequest dealRequest : request.getDeals()) {
            try {
                // Check for duplicate
                if (fxDealRepository.existsByDealUniqueId(dealRequest.getDealUniqueId())) {
                    log.debug("Skipping duplicate deal: {}", dealRequest.getDealUniqueId());
                    duplicateCount++;
                    failedDeals.add(BatchImportResponse.FailedDeal.builder()
                            .dealUniqueId(dealRequest.getDealUniqueId())
                            .reason("Deal already exists")
                            .type(BatchImportResponse.FailureType.DUPLICATE)
                            .build());
                    continue;
                }

                // Convert to entity
                FxDeal deal = mapper.toEntity(dealRequest);

                // Validate
                List<String> validationErrors = validationService.validate(deal);
                if (!validationErrors.isEmpty()) {
                    log.debug("Validation failed for deal {}: {}", dealRequest.getDealUniqueId(), validationErrors);
                    failedDeals.add(BatchImportResponse.FailedDeal.builder()
                            .dealUniqueId(dealRequest.getDealUniqueId())
                            .reason(String.join(", ", validationErrors))
                            .type(BatchImportResponse.FailureType.VALIDATION_ERROR)
                            .build());
                    continue;
                }

                // Save
                fxDealRepository.save(deal);
                successfulDeals.add(dealRequest.getDealUniqueId());
                log.debug("Successfully imported deal: {}", dealRequest.getDealUniqueId());

            } catch (Exception e) {
                log.error("Error processing deal {}: {}", dealRequest.getDealUniqueId(), e.getMessage());
                failedDeals.add(BatchImportResponse.FailedDeal.builder()
                        .dealUniqueId(dealRequest.getDealUniqueId())
                        .reason("Processing error: " + e.getMessage())
                        .type(BatchImportResponse.FailureType.PROCESSING_ERROR)
                        .build());
            }
        }

        BatchImportResponse response = BatchImportResponse.builder()
                .totalProcessed(request.getDeals().size())
                .successCount(successfulDeals.size())
                .failedCount(failedDeals.size())
                .duplicateCount(duplicateCount)
                .successfulDeals(successfulDeals)
                .failedDeals(failedDeals)
                .build();

        log.info("Batch import completed: {} successful, {} failed, {} duplicates",
                successfulDeals.size(), failedDeals.size(), duplicateCount);

        return response;
    }
}
