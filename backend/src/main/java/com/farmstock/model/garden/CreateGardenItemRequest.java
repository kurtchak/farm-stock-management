package com.farmstock.model.garden;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreateGardenItemRequest {
    private String name;
    private String category;
    private BigDecimal quantity;
    private String unit;
    private BigDecimal minStock;
    private String notes;
}
