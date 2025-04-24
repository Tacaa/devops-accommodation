package com.devops.devops_accommodation.unit;

import com.devops.devops_accommodation.client.NotificationClient;
import com.devops.devops_accommodation.dto.*;
import com.devops.devops_accommodation.enumeration.RequestApproval;
import com.devops.devops_accommodation.enumeration.RequestStatus;
import com.devops.devops_accommodation.exceptions.NotFoundException;
import com.devops.devops_accommodation.model.Accommodation;
import com.devops.devops_accommodation.model.Address;
import com.devops.devops_accommodation.model.Availability;
import com.devops.devops_accommodation.model.Reservation;
import com.devops.devops_accommodation.repository.AccommodationRepository;
import com.devops.devops_accommodation.repository.AvailabilityRepository;
import com.devops.devops_accommodation.repository.ReservationRepository;
import com.devops.devops_accommodation.services.ReservationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private AccommodationRepository accommodationRepository;

    @Mock
    private AvailabilityRepository availabilityRepository;

    @InjectMocks
    private ReservationService reservationService;

    @Mock
    private NotificationClient notificationClient;

    private Address address;
    private Accommodation accommodation;
    private ReservationRequestDTO reservationRequestDTO;

    private Reservation reservation;
    private Availability availability;

    @BeforeEach
    void setUp() {
        address = Address.builder().id(1).build();

        accommodation = Accommodation.builder()
                .id(1)
                .address(address)
                .requestApproval(RequestApproval.AUTOMATIC)
                .build();

        reservationRequestDTO = ReservationRequestDTO.builder()
                .accommodationId(1)
                .startDate(LocalDate.of(2025, 5, 1))
                .endDate(LocalDate.of(2025, 5, 7))
                .numGuests(2)
                .userId(1)
                .build();

        reservation = Reservation.builder()
                .id(26)
                .accommodation(accommodation)
                .startDate(reservationRequestDTO.getStartDate())
                .endDate(reservationRequestDTO.getEndDate())
                .guestNum(2)
                .guestId(1)
                .status(RequestStatus.ACCEPTED)
                .canceled(false)
                .deleted(false)
                .build();

        availability = Availability.builder()
                .id(1)
                .accommodation(accommodation)
                .startDate(reservationRequestDTO.getStartDate())
                .endDate(reservationRequestDTO.getEndDate())
                .available(true)
                .price(100.0)
                .build();
    }

        @Test
    void createReservationRequest_Success() {
        when(accommodationRepository.findById(1)).thenReturn(Optional.of(accommodation));
        when(accommodationRepository.checkAccommodationsAvailability(anyInt(), any(), any())).thenReturn(true);
        when(reservationRepository.existsReservation(anyInt(), any(), any())).thenReturn(false);
        when(reservationRepository.save(any(Reservation.class))).thenReturn(reservation);

        ReservationRequestResponseDTO result = reservationService.createReservationRequest(reservationRequestDTO);

        assertNotNull(result);
        assertEquals(reservation.getId(), result.getId());
        assertEquals(RequestStatus.ACCEPTED, result.getStatus());
        verify(reservationRepository).save(any(Reservation.class));
    }

    @Test
    void createReservationRequest_ThrowsNotFoundException() {
        when(accommodationRepository.findById(anyInt())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () ->
                reservationService.createReservationRequest(reservationRequestDTO));
    }

    @Test
    void cancelOrDelete_SuccessForPendingReservation() {
        reservation.setStatus(RequestStatus.PENDING);
        when(reservationRepository.findById(1)).thenReturn(Optional.of(reservation));
        when(reservationRepository.save(any(Reservation.class))).thenReturn(reservation);

        ReservationRequestResponseDTO result = reservationService.cancelOrDelete(1);

        assertTrue(result.getDeleted());
        verify(reservationRepository).save(any(Reservation.class));
    }

    @Test
    void cancelOrDelete_SuccessForAcceptedReservation() {
        reservation.setStartDate(LocalDate.now().plusDays(5));
        when(reservationRepository.findById(1)).thenReturn(Optional.of(reservation));
        when(availabilityRepository.findReservedAvailabilityToCancel(anyInt(), any(), any()))
                .thenReturn(availability);
        when(reservationRepository.save(any(Reservation.class))).thenReturn(reservation);

        ReservationRequestResponseDTO result = reservationService.cancelOrDelete(1);

        assertTrue(result.getCanceled());
        verify(availabilityRepository).save(any(Availability.class));
    }

    @Test
    void getAllPendingReservationRequestsByHost_Success() {
        List<Reservation> reservationList = Arrays.asList(reservation);
        when(reservationRepository.getAllPendingReservationRequestsByHost(anyInt())).thenReturn(reservationList);

        List<ReservationRequestResponseDTO> result = reservationService.getAllPendingReservationRequestsByHost(1);

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(reservation.getId(), result.get(0).getId());
    }

    @Test
    void saveReservationsManually_Success() {
        List<ReservationRequestResponseDTO> dtoList = Arrays.asList(ReservationRequestResponseDTO.from(reservation));
        List<Reservation> reservationList = Arrays.asList(reservation);

        when(reservationRepository.findListOfReservations(any())).thenReturn(reservationList);
        when(reservationRepository.saveAll(any())).thenReturn(reservationList);

        List<ReservationRequestResponseDTO> result = reservationService.saveReservationsManually(dtoList);

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        verify(reservationRepository).saveAll(any());
    }
}