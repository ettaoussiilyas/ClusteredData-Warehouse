package com.progressoft.clustereddata.controller;

import com.progressoft.clustereddata.dto.*;
import com.progressoft.clustereddata.service.FxDealService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/fx-deals")
@RequiredArgsConstructor
@Slf4j
public class FxDealController {

    private final FxDealService fxDealService;

    @PostMapping
    public ResponseEntity<FxDealResponse> createDeal(@Valid @RequestBody FxDealRequest request) {
        log.info("Received request to create FX deal: {}", request.getDealUniqueId());
        FxDealResponse response = fxDealService.createDeal(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/batch")
    public ResponseEntity<BatchImportResponse> importDeals(@Valid @RequestBody BatchImportRequest request) {
        log.info("Received batch import request with {} deals", request.getDeals().size());
        BatchImportResponse response = fxDealService.importDeals(request);
        return ResponseEntity.ok(response);
    }
}
