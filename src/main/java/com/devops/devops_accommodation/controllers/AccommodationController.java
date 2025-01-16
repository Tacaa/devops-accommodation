package com.devops.devops_accommodation.controllers;

import com.devops.devops_accommodation.dto.AccommodationDTO;
import com.devops.devops_accommodation.dto.CreateAccommodationDTO;
import com.devops.devops_accommodation.exceptions.AttributeNotUniqueException;
import com.devops.devops_accommodation.exceptions.AttributeNullException;
import com.devops.devops_accommodation.model.Accommodation;
import com.devops.devops_accommodation.services.AccommodationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(value = "api/accommodation/")
public class AccommodationController  {

    @GetMapping
    public String home() {
    return "Hello, World!";
}

    @Autowired
    private AccommodationService accommodationService;

    @PostMapping
    public ResponseEntity<Map<String, Object>> create(@RequestBody CreateAccommodationDTO createAccommodationDTO){
        try {
            Accommodation accommodation = accommodationService.create(createAccommodationDTO);
            Map<String, Object> response = new HashMap<>();
            response.put("message", null);
            response.put("data", AccommodationDTO.from(accommodation));
            return new ResponseEntity<>(response, HttpStatus.CREATED);

        } catch (AttributeNotUniqueException | AttributeNullException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", e.getMessage());
            response.put("data", null);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }
}
