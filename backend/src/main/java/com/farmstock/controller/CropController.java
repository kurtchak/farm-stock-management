package com.farmstock.controller;

import com.farmstock.model.CreateCropRequest;
import com.farmstock.model.Crop;
import com.farmstock.service.CropService;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/crops")
public class CropController {
    
    private final CropService cropService;

    public CropController(CropService cropService) {
        this.cropService = cropService;
    }

    @GetMapping
    public List<Crop> getAllCrops() {
        return cropService.getAllCrops();
    }
    
    @PostMapping
    @Transactional
    public ResponseEntity<Crop> createCrop(@RequestBody @Valid CreateCropRequest request) {
        Crop crop = cropService.createCrop(request);
        return ResponseEntity.ok(crop);
    }
}