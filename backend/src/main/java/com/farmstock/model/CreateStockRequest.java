package com.farmstock.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class CreateStockRequest {
    @NotBlank
    private String cropName;
    private String variety;
    private String harvestSeason;
    private String description;
    private String storageConditions;

    @NotBlank
    private String batchCode;
    @NotNull
    @Positive
    private BigDecimal quantity;
    @NotBlank
    private String unitOfMeasure;
    @NotBlank
    private String storageLocation;
    @NotNull
    private LocalDateTime harvestDate;
    private String qualityGrade;
    private String notes;
}