package com.farmstock.model.garden;

import lombok.Data;

import java.util.List;

@Data
public class CreatePlantingSetRequest {
    private String name;
    private String description;
    private String color;
    private List<PlantingSetItemRequest> items;
}
