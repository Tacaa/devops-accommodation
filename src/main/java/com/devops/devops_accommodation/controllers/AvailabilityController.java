package com.devops.devops_accommodation.controllers;


import com.devops.devops_accommodation.dto.AvailabilityDTO;
import com.devops.devops_accommodation.dto.CreateAvailabilityDTO;
import com.devops.devops_accommodation.exceptions.PeriodNotAvailable;
import com.devops.devops_accommodation.exceptions.ResourceNotFoundException;
import com.devops.devops_accommodation.exceptions.WrongHostException;
import com.devops.devops_accommodation.model.Availability;
import com.devops.devops_accommodation.services.AvailabilityService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping(value = "api/availability")
public class AvailabilityController {

    @Autowired
    private AvailabilityService availabilityService;


    @GetMapping("/{accommodationId}")
    public ResponseEntity<List<AvailabilityDTO>> getAvailabilities(
            @PathVariable Integer accommodationId) {
        log.info("Fetching availabilities for accommodation with ID: {}", accommodationId);

        List<Availability> availabilities = availabilityService.getAllAvailabilitiesByAccommodationId(accommodationId);
        log.debug("Found {} availability entries for accommodation ID {}", availabilities.size(), accommodationId);

        List<AvailabilityDTO> availabilityDTOs = availabilities.stream()
                .map(AvailabilityDTO::from)
                .collect(Collectors.toList());
        return ResponseEntity.ok(availabilityDTOs);
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> createAvailability(@RequestBody CreateAvailabilityDTO createAvailabilityDTO,  @RequestHeader("X-User-Id") String userId) {
        log.info("Creating availability for user ID: {}", userId);
        log.debug("Request payload: {}", createAvailabilityDTO);

        try {
            Availability availability = availabilityService.createAvailability(createAvailabilityDTO, userId);
            log.info("Availability created with ID: {}", availability.getId());

            Map<String, Object> response = new HashMap<>();
            response.put("message", null);
            response.put("data", AvailabilityDTO.from(availability));
            return new ResponseEntity<>(response, HttpStatus.CREATED);

        } catch (PeriodNotAvailable | WrongHostException e) {
            log.warn("Business validation failed while creating availability: {} - {}", e.getClass().getSimpleName(), e.getMessage());
            Map<String, Object> response = new HashMap<>();
            response.put("message", e.getMessage());
            response.put("data", null);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        catch (ResourceNotFoundException e) {
            log.error("Resource not found: {} - {}", e.getClass().getSimpleName(), e.getMessage());
            Map<String, Object> response = new HashMap<>();
            response.put("message", e.getMessage());
            response.put("data", null);
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }


    @PutMapping("/{availabilityId}")
    public ResponseEntity<Map<String, Object>> updateAvailability(
            @PathVariable Integer availabilityId,
            @RequestBody CreateAvailabilityDTO createAvailabilityDTO,
            @RequestHeader("X-User-Id") String userId) {

        log.info("Updating availability ID: {} for user ID: {}", availabilityId, userId);
        log.debug("Update payload: {}", createAvailabilityDTO);

        try {
            Availability availability = availabilityService.updateAvailability(availabilityId, createAvailabilityDTO, userId);
            log.info("Availability with ID {} successfully updated", availabilityId);

            Map<String, Object> response = new HashMap<>();
            response.put("message", null);
            response.put("data", AvailabilityDTO.from(availability));
            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (PeriodNotAvailable  | WrongHostException e) {
            log.warn("Validation failed during availability update: {} - {}", e.getClass().getSimpleName(), e.getMessage());
            Map<String, Object> response = new HashMap<>();
            response.put("message", e.getMessage());
            response.put("data", null);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        catch (ResourceNotFoundException e) {
            log.error("Resource not found when updating availability: {} - {}", e.getClass().getSimpleName(), e.getMessage());
            Map<String, Object> response = new HashMap<>();
            response.put("message", e.getMessage());
            response.put("data", null);
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }


}
