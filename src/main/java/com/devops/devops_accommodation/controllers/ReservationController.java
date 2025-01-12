package com.devops.devops_accommodation.controllers;

import com.devops.devops_accommodation.dto.ReservationRequestDTO;
import com.devops.devops_accommodation.dto.ReservationRequestResponseDTO;
import com.devops.devops_accommodation.exceptions.AvailabilityNotExists;
import com.devops.devops_accommodation.exceptions.NotFoundException;
import com.devops.devops_accommodation.exceptions.ReservationCanNotCancel;
import com.devops.devops_accommodation.exceptions.ReservationConflictException;
import com.devops.devops_accommodation.services.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(value = "api/reservation")
public class ReservationController {

    @Autowired
    private ReservationService reservationService;

    @PostMapping
    public ResponseEntity<Map<String, Object>> createAvailability(@RequestBody ReservationRequestDTO reservationRequestDTO) {
        try {
            ReservationRequestResponseDTO reservationRequestResponseDTO = reservationService.createReservationRequest(reservationRequestDTO);
            Map<String, Object> response = new HashMap<>();
            response.put("message", null);
            response.put("data", reservationRequestResponseDTO);
            return new ResponseEntity<>(response, HttpStatus.CREATED);

        } catch (NotFoundException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", e.getMessage());
            response.put("data", null);
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);

        }catch (AvailabilityNotExists | ReservationConflictException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", e.getMessage());
            response.put("data", null);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Map<String, Object>> delete(@PathVariable Integer id) {
        try {
            ReservationRequestResponseDTO reservationRequestResponseDTO = reservationService.cancelOrDelete(id);
            Map<String, Object> response = new HashMap<>();
            response.put("message", null);
            response.put("data", reservationRequestResponseDTO);
            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (NotFoundException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", e.getMessage());
            response.put("data", null);
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);

        }catch (ReservationCanNotCancel e) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", e.getMessage());
            response.put("data", null);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }
}
