package com.farmstock.service;

import com.farmstock.exception.BadRequestException;
import com.farmstock.model.CreateCropRequest;
import com.farmstock.model.Crop;
import com.farmstock.repository.CropRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CropService {
    
    private final CropRepository cropRepository;

    public CropService(CropRepository cropRepository) {
        this.cropRepository = cropRepository;
    }

    public List<Crop> getAllCrops() {
        return cropRepository.findAllByOrderByNameAscVarietyAsc();
    }
    
    public Crop createCrop(CreateCropRequest request) {
        // Check if crop with same name and variety already exists
        Optional<Crop> existing = cropRepository
            .findByNameAndVariety(request.getName(), request.getVariety());
            
        if (existing.isPresent()) {
            throw new BadRequestException("Crop already exists");
        }
        
        Crop crop = new Crop();
        crop.setName(request.getName());
        crop.setVariety(request.getVariety());
        
        return cropRepository.save(crop);
    }
}