package com.progressoft.clustereddata.mapper;

import com.progressoft.clustereddata.dto.FxDealRequest;
import com.progressoft.clustereddata.dto.FxDealResponse;
import com.progressoft.clustereddata.entity.FxDeal;
import org.springframework.stereotype.Component;

@Component
public class FxDealMapper {

    public FxDeal toEntity(FxDealRequest request) {
        FxDeal deal = new FxDeal();
        deal.setDealUniqueId(request.getDealUniqueId());
        deal.setFromCurrencyIsoCode(request.getFromCurrencyIsoCode().toUpperCase());
        deal.setToCurrencyIsoCode(request.getToCurrencyIsoCode().toUpperCase());
        deal.setDealTimestamp(request.getDealTimestamp());
        deal.setDealAmount(request.getDealAmount());
        return deal;
    }

    public FxDealResponse toResponse(FxDeal deal) {
        return FxDealResponse.builder()
                .dealUniqueId(deal.getDealUniqueId())
                .fromCurrencyIsoCode(deal.getFromCurrencyIsoCode())
                .toCurrencyIsoCode(deal.getToCurrencyIsoCode())
                .dealTimestamp(deal.getDealTimestamp())
                .dealAmount(deal.getDealAmount())
                .createdAt(deal.getCreatedAt())
                .updatedAt(deal.getUpdatedAt())
                .build();
    }
}
