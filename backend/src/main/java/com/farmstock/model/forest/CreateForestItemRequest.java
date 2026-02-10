package com.farmstock.model.forest;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreateForestItemRequest {
    private String name;
    private String category;
    private BigDecimal quantity;
    private String unit;
    private BigDecimal minStock;
    private String notes;
}
