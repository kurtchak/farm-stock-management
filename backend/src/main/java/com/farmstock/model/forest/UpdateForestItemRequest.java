package com.farmstock.model.forest;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class UpdateForestItemRequest {
    private String name;
    private String category;
    private String unit;
    private BigDecimal minStock;
    private String notes;
}
