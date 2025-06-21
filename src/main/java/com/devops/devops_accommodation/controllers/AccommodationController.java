package com.devops.devops_accommodation.controllers;

import com.devops.devops_accommodation.dto.*;
import com.devops.devops_accommodation.exceptions.AttributeNotUniqueException;
import com.devops.devops_accommodation.exceptions.AttributeNullException;
import com.devops.devops_accommodation.model.Accommodation;
import com.devops.devops_accommodation.services.AccommodationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping(value = "api/accommodation")
public class AccommodationController  {

    @Autowired
    private AccommodationService accommodationService;

    @GetMapping(value = "/{id}")
    public ResponseEntity<AccommodationDTO> getAccommodationById(@PathVariable Integer id) {
        log.info("Fetching accommodation by id: {}", id);
        Accommodation accommodation = accommodationService.getById(id);

        if (accommodation == null) {
            log.warn("Accommodation with id {} not found", id);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        AccommodationDTO accommodationDTO = AccommodationDTO.from(accommodation);
        log.debug("Accommodation found: {}", accommodation.getName());
        return new ResponseEntity<>(accommodationDTO, HttpStatus.OK);
    }

    //za notifikacije u review servisu
    @GetMapping(value = "/data/{id}")
    public AccommodationNotificationData getAccommodationData(@PathVariable Integer id) {
        log.info("Fetching accommodation data for notification, id: {}", id);

        Accommodation accommodation = accommodationService.getById(id);
        if (accommodation == null) {
            log.warn("Accommodation with id {} not found for notification data", id);
            return null;
        }

        AccommodationNotificationData accommodationNotificationData = AccommodationNotificationData.builder()
                .accommodationName(accommodation.getName())
                .accommodationHostId(accommodation.getHostId())
                .build();
        return accommodationNotificationData;
    }


    @GetMapping
    public ResponseEntity<List<AccommodationDTO>> getAllAccommodations() {
        log.info("Fetching all accommodations");
        List<Accommodation> accommodations = accommodationService.getAllAccommodations();

        List<AccommodationDTO> accommodationDTOS = new ArrayList<>();
        for (Accommodation accommodation : accommodations) {
            accommodationDTOS.add(AccommodationDTO.from(accommodation));
        }

        log.debug("Total accommodations found: {}", accommodationDTOS.size());
        return new ResponseEntity<>(accommodationDTOS, HttpStatus.OK);
    }


    @PostMapping("/search")
    public ResponseEntity<Map<String, Object>>  searchAccommodations(@RequestBody SearchAccommodationDTO searchDTO) {
        log.info("Searching accommodations with filters: {}", searchDTO);

        try{
            List<SearchAccommodationResultDTO> searchAccommodationResultDTO = accommodationService.searchAccommodations(searchDTO);

        if(searchAccommodationResultDTO.isEmpty()){
            log.info("No accommodations matched the search criteria");
            Map<String, Object> response = new HashMap<>();
            response.put("message", "There is no searched accommodation");
            response.put("data", searchAccommodationResultDTO);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
            log.info("Search returned {} accommodations", searchAccommodationResultDTO.size());
            Map<String, Object> response = new HashMap<>();
            response.put("message", null);
            response.put("data", searchAccommodationResultDTO);
            return new ResponseEntity<>(response, HttpStatus.OK);

        }catch (AttributeNullException e){
            log.warn("Search failed due to missing attribute: {}", e.getMessage());
            Map<String, Object> response = new HashMap<>();
            response.put("message", e.getMessage());
            response.put("data", null);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);

        }
    }

  @PostMapping
  public ResponseEntity<Map<String, Object>> create(@RequestBody CreateAccommodationDTO createAccommodationDTO){
      log.info("Creating new accommodation for hostId: {}", createAccommodationDTO.getHostId());
      try {
          Accommodation accommodation = accommodationService.create(createAccommodationDTO);
          Map<String, Object> response = new HashMap<>();
          response.put("message", null);
          response.put("data", AccommodationDTO.from(accommodation));
          log.info("Accommodation created with id: {}", accommodation.getId());
          return new ResponseEntity<>(response, HttpStatus.CREATED);

      } catch (AttributeNotUniqueException | AttributeNullException e) {
          log.warn("Failed to create accommodation: {}", e.getMessage());
          Map<String, Object> response = new HashMap<>();
          response.put("message", e.getMessage());
          response.put("data", null);
          return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
      }
  }

    @DeleteMapping(value = "/delete-accommodations-of-host")
    @ResponseStatus(HttpStatus.OK)
    public void deleteAccommodationsOfHost(@RequestParam Integer hostId){
        log.info("Deleting all accommodations for host with id: {}", hostId);
        accommodationService.deleteAllHostAccommodations(hostId);
        log.info("Deleted accommodations for host id: {}", hostId);
    }

}
