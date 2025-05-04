package com.devops.devops_accommodation.controllers;

import com.devops.devops_accommodation.dto.ReservationRequestDTO;
import com.devops.devops_accommodation.dto.ReservationRequestResponseDTO;
import com.devops.devops_accommodation.exceptions.AvailabilityNotExists;
import com.devops.devops_accommodation.exceptions.NotFoundException;
import com.devops.devops_accommodation.exceptions.ReservationCanNotCancel;
import com.devops.devops_accommodation.exceptions.ReservationConflictException;
import com.devops.devops_accommodation.services.ReservationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping(value = "api/reservation")
public class ReservationController {

    @Autowired
    private ReservationService reservationService;

    @PostMapping({"", "/"})
    public ResponseEntity<Map<String, Object>> createReservation(@RequestBody ReservationRequestDTO reservationRequestDTO) {
        log.info("Received request to create reservation: {}", reservationRequestDTO);

        try {
            ReservationRequestResponseDTO reservationRequestResponseDTO = reservationService.createReservationRequest(reservationRequestDTO);
            log.info("Reservation successfully created with ID: {}", reservationRequestResponseDTO.getId());

            Map<String, Object> response = new HashMap<>();
            response.put("message", null);
            response.put("data", reservationRequestResponseDTO);
            return new ResponseEntity<>(response, HttpStatus.CREATED);

        } catch (NotFoundException e) {
            log.error("NotFoundException while creating reservation: {}", e.getMessage());
            Map<String, Object> response = new HashMap<>();
            response.put("message", e.getMessage());
            response.put("data", null);
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);

        }catch (AvailabilityNotExists | ReservationConflictException e) {
            log.warn("Business rule violation during reservation creation: {}", e.getMessage());
            Map<String, Object> response = new HashMap<>();
            response.put("message", e.getMessage());
            response.put("data", null);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Map<String, Object>> delete(@PathVariable Integer id) {
        log.info("Attempting to delete/cancel reservation with ID: {}", id);

        try {
            ReservationRequestResponseDTO reservationRequestResponseDTO = reservationService.cancelOrDelete(id);
            log.info("Reservation with ID {} successfully canceled or deleted", id);

            Map<String, Object> response = new HashMap<>();
            response.put("message", null);
            response.put("data", reservationRequestResponseDTO);
            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (NotFoundException e) {
            log.error("Reservation not found: {}", e.getMessage());
            Map<String, Object> response = new HashMap<>();
            response.put("message", e.getMessage());
            response.put("data", null);
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);

        }catch (ReservationCanNotCancel e) {
            log.warn("Reservation can't be canceled: {}", e.getMessage());
            Map<String, Object> response = new HashMap<>();
            response.put("message", e.getMessage());
            response.put("data", null);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(value = "/all-host-pending-accommodation/{hostId}")
    public ResponseEntity<List<ReservationRequestResponseDTO>> getAllUsers(@PathVariable Integer hostId) {
        log.info("Fetching all pending reservations for host ID: {}", hostId);
        List<ReservationRequestResponseDTO> reservations = reservationService.getAllPendingReservationRequestsByHost(hostId);
        return new ResponseEntity<>(reservations, HttpStatus.OK);
    }

    @GetMapping(value = "/guest/{guestId}")
    public ResponseEntity<Map<String, Object>> getGuestReservations(@PathVariable Integer guestId) {
        log.info("Fetching reservations for guest ID: {}", guestId);
        List<ReservationRequestResponseDTO> reservations = reservationService.getReservationsByGuestId(guestId);
        Map<String, Object> response = new HashMap<>();
        response.put("message", null);
        response.put("data", reservations);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping(value = "/save-manually-approved")
    public ResponseEntity<List<ReservationRequestResponseDTO>> saveManuallyApprovedRequests(@RequestBody List<ReservationRequestResponseDTO> reservationRequestResponseDTO){
        log.info("Saving manually approved reservations: {} entries", reservationRequestResponseDTO.size());
        List<ReservationRequestResponseDTO> savedReservations = reservationService.saveReservationsManually(reservationRequestResponseDTO);
        return new ResponseEntity<>(savedReservations, HttpStatus.OK);
    }

    @GetMapping(value = "/did-guest-had-reservation-in-accommodation")
    @ResponseStatus(HttpStatus.OK)
    public Boolean didGuestHadReservationInAccommodation(@RequestParam Integer guestId, @RequestParam Integer accommodationId) {
        log.info("Checking if guest ID {} had reservation in accommodation ID {}", guestId, accommodationId);
        return reservationService.didGuestHadReservationInAccommodation(guestId, accommodationId);
    }


    @GetMapping(value = "/did-guest-had-reservation-in-host-accommodation")
    @ResponseStatus(HttpStatus.OK)
    public Boolean didGuestHadReservationInHostAccommodation(@RequestParam Integer guestId, @RequestParam Integer hostId) {
        log.info("Checking if guest ID {} had reservation with host ID {}", guestId, hostId);
        return reservationService.didGuestHadReservationInHostAccommodation(guestId, hostId);
    }

    @GetMapping(value = "/is-guest-having-reservation-at-moment")
    @ResponseStatus(HttpStatus.OK)
    public Boolean isGuestHavingReservationAtMoment(@RequestParam Integer guestId){
        log.info("Checking if guest ID {} currently has a reservation", guestId);
        return reservationService.isGuestHavingReservationAtMoment(guestId);
    }

    @GetMapping(value = "/is-host-having-reservation-at-moment")
    @ResponseStatus(HttpStatus.OK)
    public Boolean isHostHavingReservationAtMoment(@RequestParam Integer hostId){
        log.info("Checking if host ID {} currently has a reservation", hostId);
        return reservationService.isHostHavingReservationAtMoment(hostId);
    }
}
