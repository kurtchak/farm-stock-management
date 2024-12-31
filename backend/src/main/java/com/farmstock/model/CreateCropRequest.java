package com.farmstock.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateCropRequest {
    @NotBlank
    private String name;
    
    @NotBlank
    private String variety;
}