package com.farmstock.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CreateStockRequest {
    @NotNull
    private Long cropId;

    @NotNull
    @Positive
    private Double quantity;

    @NotBlank
    private String unitOfMeasure;

    @NotNull
    private LocalDateTime harvestDate;

    private String notes;
}