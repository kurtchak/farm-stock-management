package com.farmstock.model.garden;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class UpdateGardenItemRequest {
    private String name;
    private String category;
    private String unit;
    private BigDecimal minStock;
    private String notes;
}
