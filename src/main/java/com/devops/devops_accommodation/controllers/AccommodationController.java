package com.devops.devops_accommodation.controllers;

import com.devops.devops_accommodation.dto.AccommodationDTO;
import com.devops.devops_accommodation.dto.SearchAccommodationDTO;
import com.devops.devops_accommodation.dto.SearchAccommodationResultDTO;
import com.devops.devops_accommodation.dto.CreateAccommodationDTO;
import com.devops.devops_accommodation.exceptions.AttributeNotUniqueException;
import com.devops.devops_accommodation.exceptions.AttributeNullException;
import com.devops.devops_accommodation.model.Accommodation;
import com.devops.devops_accommodation.services.AccommodationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "api/accommodation")
public class AccommodationController  {

    @Autowired
    private AccommodationService accommodationService;

    @GetMapping(value = "/{id}")
    public ResponseEntity<AccommodationDTO> getAccommodationById(@PathVariable Integer id) {
        Accommodation accommodation = accommodationService.getById(id);

        if (accommodation == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        AccommodationDTO accommodationDTO = AccommodationDTO.from(accommodation);
        return new ResponseEntity<>(accommodationDTO, HttpStatus.OK);
    }


    @GetMapping
    public ResponseEntity<List<AccommodationDTO>> getAllAccommodations() {
        List<Accommodation> accommodations = accommodationService.getAllAccommodations();

        List<AccommodationDTO> accommodationDTOS = new ArrayList<>();
        for (Accommodation accommodation : accommodations) {
            accommodationDTOS.add(AccommodationDTO.from(accommodation));
        }

        return new ResponseEntity<>(accommodationDTOS, HttpStatus.OK);
    }


    @PostMapping("/search")
    public ResponseEntity<Map<String, Object>>  searchAccommodations(@RequestBody SearchAccommodationDTO searchDTO) {
        try{
            List<SearchAccommodationResultDTO> searchAccommodationResultDTO = accommodationService.searchAccommodations(searchDTO);

        if(searchAccommodationResultDTO.isEmpty()){
            Map<String, Object> response = new HashMap<>();
            response.put("message", "There is no searched accommodation");
            response.put("data", searchAccommodationResultDTO);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }

            Map<String, Object> response = new HashMap<>();
            response.put("message", null);
            response.put("data", searchAccommodationResultDTO);
            return new ResponseEntity<>(response, HttpStatus.OK);

        }catch (AttributeNullException e){
            Map<String, Object> response = new HashMap<>();
            response.put("message", e.getMessage());
            response.put("data", null);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);

        }
    }

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

    @DeleteMapping(value = "/delete-accommodations-of-host")
    @ResponseStatus(HttpStatus.OK)
    public void deleteAccommodationsOfHost(@RequestParam Integer hostId){
        accommodationService.deleteAllHostAccommodations(hostId);
    }

}
