package com.progressoft.clustereddata.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.progressoft.clustereddata.dto.BatchImportRequest;
import com.progressoft.clustereddata.dto.FxDealRequest;
import com.progressoft.clustereddata.repository.FxDealRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class FxDealControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private FxDealRepository fxDealRepository;

    @BeforeEach
    void setUp() {
        fxDealRepository.deleteAll();
    }

    @Test
    void shouldCreateDealSuccessfully() throws Exception {
        FxDealRequest request = createValidRequest("DEAL-001");

        mockMvc.perform(post("/api/v1/fx-deals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.dealUniqueId").value("DEAL-001"))
                .andExpect(jsonPath("$.fromCurrencyIsoCode").value("USD"))
                .andExpect(jsonPath("$.toCurrencyIsoCode").value("EUR"))
                .andExpect(jsonPath("$.dealAmount").value(1000.50))
                .andExpect(jsonPath("$.createdAt").exists())
                .andExpect(jsonPath("$.updatedAt").exists());
    }

    @Test
    void shouldReturnBadRequestForInvalidData() throws Exception {
        FxDealRequest request = createValidRequest("DEAL-002");
        request.setFromCurrencyIsoCode("INVALID");

        mockMvc.perform(post("/api/v1/fx-deals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.details").isArray());
    }

    @Test
    void shouldReturnConflictForDuplicateDeal() throws Exception {
        FxDealRequest request = createValidRequest("DEAL-003");

        // Create first deal
        mockMvc.perform(post("/api/v1/fx-deals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        // Try to create duplicate
        mockMvc.perform(post("/api/v1/fx-deals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.error").value("Conflict"))
                .andExpect(jsonPath("$.message").value(containsString("already exists")));
    }

    @Test
    void shouldReturnBadRequestForNullDealUniqueId() throws Exception {
        FxDealRequest request = createValidRequest(null);

        mockMvc.perform(post("/api/v1/fx-deals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.details", hasItem(containsString("Deal unique ID is required"))));
    }

    @Test
    void shouldReturnBadRequestForInvalidCurrencyCode() throws Exception {
        FxDealRequest request = createValidRequest("DEAL-004");
        request.setFromCurrencyIsoCode("XXX");

        mockMvc.perform(post("/api/v1/fx-deals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.details", hasItem(containsString("not a valid ISO 4217 code"))));
    }

    @Test
    void shouldReturnBadRequestForNegativeAmount() throws Exception {
        FxDealRequest request = createValidRequest("DEAL-005");
        request.setDealAmount(new BigDecimal("-100.00"));

        mockMvc.perform(post("/api/v1/fx-deals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.details", hasItem(containsString("positive number"))));
    }

    @Test
    void shouldReturnBadRequestForFutureTimestamp() throws Exception {
        FxDealRequest request = createValidRequest("DEAL-006");
        request.setDealTimestamp(LocalDateTime.now().plusDays(1));

        mockMvc.perform(post("/api/v1/fx-deals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.details", hasItem(containsString("cannot be in the future"))));
    }

    @Test
    void shouldGetDealByIdSuccessfully() throws Exception {
        FxDealRequest request = createValidRequest("DEAL-007");

        // Create deal first
        mockMvc.perform(post("/api/v1/fx-deals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        // Get deal by ID
        mockMvc.perform(get("/api/v1/fx-deals/DEAL-007"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dealUniqueId").value("DEAL-007"))
                .andExpect(jsonPath("$.fromCurrencyIsoCode").value("USD"))
                .andExpect(jsonPath("$.toCurrencyIsoCode").value("EUR"));
    }

    @Test
    void shouldReturnNotFoundForNonExistentDeal() throws Exception {
        mockMvc.perform(get("/api/v1/fx-deals/NON-EXISTENT"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value(containsString("not found")));
    }

    @Test
    void shouldImportBatchDealsSuccessfully() throws Exception {
        FxDealRequest deal1 = createValidRequest("BATCH-001");
        FxDealRequest deal2 = createValidRequest("BATCH-002");
        FxDealRequest deal3 = createValidRequest("BATCH-003");

        BatchImportRequest batchRequest = new BatchImportRequest();
        batchRequest.setDeals(Arrays.asList(deal1, deal2, deal3));

        mockMvc.perform(post("/api/v1/fx-deals/batch")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(batchRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalProcessed").value(3))
                .andExpect(jsonPath("$.successCount").value(3))
                .andExpect(jsonPath("$.failedCount").value(0))
                .andExpect(jsonPath("$.duplicateCount").value(0))
                .andExpect(jsonPath("$.successfulDeals", hasSize(3)))
                .andExpect(jsonPath("$.failedDeals", hasSize(0)));
    }

    @Test
    void shouldHandleBatchWithValidationErrors() throws Exception {
        FxDealRequest validDeal = createValidRequest("BATCH-004");
        FxDealRequest invalidDeal = createValidRequest("BATCH-005");
        invalidDeal.setFromCurrencyIsoCode("XXX");

        BatchImportRequest batchRequest = new BatchImportRequest();
        batchRequest.setDeals(Arrays.asList(validDeal, invalidDeal));

        mockMvc.perform(post("/api/v1/fx-deals/batch")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(batchRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalProcessed").value(2))
                .andExpect(jsonPath("$.successCount").value(1))
                .andExpect(jsonPath("$.failedCount").value(1))
                .andExpect(jsonPath("$.successfulDeals", hasItem("BATCH-004")))
                .andExpect(jsonPath("$.failedDeals", hasSize(1)))
                .andExpect(jsonPath("$.failedDeals[0].dealUniqueId").value("BATCH-005"))
                .andExpect(jsonPath("$.failedDeals[0].type").value("VALIDATION_ERROR"));
    }

    @Test
    void shouldHandleBatchWithDuplicates() throws Exception {
        FxDealRequest deal1 = createValidRequest("BATCH-006");
        
        // Create first deal
        mockMvc.perform(post("/api/v1/fx-deals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(deal1)))
                .andExpect(status().isCreated());

        // Try to import batch with duplicate
        FxDealRequest deal2 = createValidRequest("BATCH-007");
        BatchImportRequest batchRequest = new BatchImportRequest();
        batchRequest.setDeals(Arrays.asList(deal1, deal2));

        mockMvc.perform(post("/api/v1/fx-deals/batch")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(batchRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalProcessed").value(2))
                .andExpect(jsonPath("$.successCount").value(1))
                .andExpect(jsonPath("$.failedCount").value(1))
                .andExpect(jsonPath("$.duplicateCount").value(1))
                .andExpect(jsonPath("$.failedDeals[0].type").value("DUPLICATE"));
    }

    @Test
    void shouldReturnBadRequestForEmptyBatch() throws Exception {
        BatchImportRequest batchRequest = new BatchImportRequest();
        batchRequest.setDeals(Arrays.asList());

        mockMvc.perform(post("/api/v1/fx-deals/batch")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(batchRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.details", hasItem(containsString("cannot be empty"))));
    }

    private FxDealRequest createValidRequest(String dealUniqueId) {
        FxDealRequest request = new FxDealRequest();
        request.setDealUniqueId(dealUniqueId);
        request.setFromCurrencyIsoCode("USD");
        request.setToCurrencyIsoCode("EUR");
        request.setDealTimestamp(LocalDateTime.now().minusHours(1));
        request.setDealAmount(new BigDecimal("1000.50"));
        return request;
    }
}
