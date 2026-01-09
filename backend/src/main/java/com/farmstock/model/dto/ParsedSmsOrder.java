package com.farmstock.model;

import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ParsedSmsOrder {
    private boolean valid;
    private String errorMessage;
    private List<ParsedOrderItem> items;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ParsedOrderItem {
        private String productName;
        private BigDecimal quantity;
        private String unit;
    }
}