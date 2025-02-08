package com.devops.devops_accommodation.unit;

import com.devops.devops_accommodation.controllers.ReservationController;
import com.devops.devops_accommodation.dto.AccommodationDTO;
import com.devops.devops_accommodation.dto.ReservationRequestDTO;
import com.devops.devops_accommodation.dto.ReservationRequestResponseDTO;
import com.devops.devops_accommodation.enumeration.RequestStatus;
import com.devops.devops_accommodation.exceptions.NotFoundException;
import com.devops.devops_accommodation.services.ReservationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReservationControllerTest {

    @Mock
    private ReservationService reservationService;

    @InjectMocks
    private ReservationController reservationController;

    private ReservationRequestDTO requestDTO;
    private ReservationRequestResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        requestDTO = ReservationRequestDTO.builder()
                .accommodationId(1)
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusDays(2))
                .numGuests(2)
                .userId(1)
                .build();

        responseDTO = ReservationRequestResponseDTO.builder()
                .id(1)
                .accommodation(AccommodationDTO.builder().id(1).build())
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusDays(2))
                .numGuests(2)
                .userId(1)
                .status(RequestStatus.PENDING)
                .canceled(false)
                .deleted(false)
                .build();
    }

    @Test
    void createReservation_Success() {
        when(reservationService.createReservationRequest(any(ReservationRequestDTO.class)))
                .thenReturn(responseDTO);

        ResponseEntity<Map<String, Object>> response = reservationController.createReservation(requestDTO);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNull(response.getBody().get("message"));
        assertEquals(responseDTO, response.getBody().get("data"));
        verify(reservationService).createReservationRequest(requestDTO);
    }

    @Test
    void createReservation_NotFound() {
        when(reservationService.createReservationRequest(any(ReservationRequestDTO.class)))
                .thenThrow(new NotFoundException("Accommodation not found"));

        ResponseEntity<Map<String, Object>> response = reservationController.createReservation(requestDTO);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Accommodation not found", response.getBody().get("message"));
        assertNull(response.getBody().get("data"));
    }

    @Test
    void delete_Success() {
        when(reservationService.cancelOrDelete(1)).thenReturn(responseDTO);

        ResponseEntity<Map<String, Object>> response = reservationController.delete(1);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNull(response.getBody().get("message"));
        assertEquals(responseDTO, response.getBody().get("data"));
        verify(reservationService).cancelOrDelete(1);
    }

    @Test
    void getAllPendingReservations_Success() {
        List<ReservationRequestResponseDTO> reservations = Arrays.asList(responseDTO);
        when(reservationService.getAllPendingReservationRequestsByHost(1)).thenReturn(reservations);

        ResponseEntity<List<ReservationRequestResponseDTO>> response = reservationController.getAllUsers(1);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(reservations, response.getBody());
        verify(reservationService).getAllPendingReservationRequestsByHost(1);
    }

    @Test
    void saveManuallyApprovedRequests_Success() {
        List<ReservationRequestResponseDTO> reservations = Arrays.asList(responseDTO);
        when(reservationService.saveReservationsManually(any())).thenReturn(reservations);

        ResponseEntity<List<ReservationRequestResponseDTO>> response =
                reservationController.saveManuallyApprovedRequests(reservations);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(reservations, response.getBody());
        verify(reservationService).saveReservationsManually(reservations);
    }

    @Test
    void didGuestHadReservationInAccommodation_Success() {
        when(reservationService.didGuestHadReservationInAccommodation(1, 1)).thenReturn(true);

        Boolean result = reservationController.didGuestHadReservationInAccommodation(1, 1);

        assertTrue(result);
        verify(reservationService).didGuestHadReservationInAccommodation(1, 1);
    }

    @Test
    void didGuestHadReservationInHostAccommodation_Success() {
        when(reservationService.didGuestHadReservationInHostAccommodation(1, 1)).thenReturn(true);

        Boolean result = reservationController.didGuestHadReservationInHostAccommodation(1, 1);

        assertTrue(result);
        verify(reservationService).didGuestHadReservationInHostAccommodation(1, 1);
    }
}