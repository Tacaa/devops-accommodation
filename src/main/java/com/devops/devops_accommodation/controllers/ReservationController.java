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
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "api/reservation")
public class ReservationController {

    @Autowired
    private ReservationService reservationService;

    @PostMapping({"", "/"})
    public ResponseEntity<Map<String, Object>> createReservation(@RequestBody ReservationRequestDTO reservationRequestDTO) {
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

    @GetMapping(value = "/all-host-pending-accommodation/{hostId}")
    public ResponseEntity<List<ReservationRequestResponseDTO>> getAllUsers(@PathVariable Integer hostId) {
        List<ReservationRequestResponseDTO> reservations = reservationService.getAllPendingReservationRequestsByHost(hostId);
        return new ResponseEntity<>(reservations, HttpStatus.OK);
    }

    @GetMapping(value = "/guest/{guestId}")
    public ResponseEntity<Map<String, Object>> getGuestReservations(@PathVariable Integer guestId) {
        List<ReservationRequestResponseDTO> reservations = reservationService.getReservationsByGuestId(guestId);
        Map<String, Object> response = new HashMap<>();
        response.put("message", null);
        response.put("data", reservations);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping(value = "/save-manually-approved")
    public ResponseEntity<List<ReservationRequestResponseDTO>> saveManuallyApprovedRequests(@RequestBody List<ReservationRequestResponseDTO> reservationRequestResponseDTO){
        List<ReservationRequestResponseDTO> savedReservations = reservationService.saveReservationsManually(reservationRequestResponseDTO);
        return new ResponseEntity<>(savedReservations, HttpStatus.OK);
    }

    @GetMapping(value = "/did-guest-had-reservation-in-accommodation")
    @ResponseStatus(HttpStatus.OK)
    public Boolean didGuestHadReservationInAccommodation(@RequestParam Integer guestId, @RequestParam Integer accommodationId) {
        System.out.println("Tatjana");
        return reservationService.didGuestHadReservationInAccommodation(guestId, accommodationId);
    }


    @GetMapping(value = "/did-guest-had-reservation-in-host-accommodation")
    @ResponseStatus(HttpStatus.OK)
    public Boolean didGuestHadReservationInHostAccommodation(@RequestParam Integer guestId, @RequestParam Integer hostId) {
        return reservationService.didGuestHadReservationInHostAccommodation(guestId, hostId);
    }

    @GetMapping(value = "/is-guest-having-reservation-at-moment")
    @ResponseStatus(HttpStatus.OK)
    public Boolean isGuestHavingReservationAtMoment(@RequestParam Integer guestId){
        return reservationService.isGuestHavingReservationAtMoment(guestId);
    }

    @GetMapping(value = "/is-host-having-reservation-at-moment")
    @ResponseStatus(HttpStatus.OK)
    public Boolean isHostHavingReservationAtMoment(@RequestParam Integer hostId){
        return reservationService.isHostHavingReservationAtMoment(hostId);
    }
}
