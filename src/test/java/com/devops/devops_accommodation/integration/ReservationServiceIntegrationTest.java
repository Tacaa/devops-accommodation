package com.devops.devops_accommodation.integration;

import com.devops.devops_accommodation.dto.ReservationRequestDTO;
import com.devops.devops_accommodation.enumeration.PriceType;
import com.devops.devops_accommodation.enumeration.RequestApproval;
import com.devops.devops_accommodation.enumeration.RequestStatus;
import com.devops.devops_accommodation.exceptions.AvailabilityNotExists;
import com.devops.devops_accommodation.exceptions.NotFoundException;
import com.devops.devops_accommodation.exceptions.ReservationCanNotCancel;
import com.devops.devops_accommodation.exceptions.ReservationConflictException;
import com.devops.devops_accommodation.model.Accommodation;
import com.devops.devops_accommodation.model.Address;
import com.devops.devops_accommodation.model.Availability;
import com.devops.devops_accommodation.repository.AccommodationRepository;
import com.devops.devops_accommodation.repository.AvailabilityRepository;
import com.devops.devops_accommodation.repository.ReservationRepository;
import com.devops.devops_accommodation.services.ReservationService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;


import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class ReservationServiceIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private AccommodationRepository accommodationRepository;

    @Autowired
    private AvailabilityRepository availabilityRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    private Address address;
    private Accommodation accommodation;
    private Availability availability;
    private ReservationRequestDTO reservationRequestDTO;

    @BeforeEach
    void setUp() {
        reservationRepository.deleteAll();
        availabilityRepository.deleteAll();
        accommodationRepository.deleteAll();

        address = Address.builder().city("Main Street").number(1).street("New York").country("USA").build();

        accommodation = Accommodation.builder()
                .name("Seaside Villa")
                .minGuests(2)
                .maxGuests(6)
                .address(address)
                .priceType(PriceType.BY_ACCOMMODATION)
                .requestApproval(RequestApproval.AUTOMATIC)
                .hostId(16)
                .build();
        accommodation = accommodationRepository.save(accommodation);

        availability = Availability.builder()
                .accommodation(accommodation)
                .startDate(LocalDate.of(2025, 5, 1))
                .endDate(LocalDate.of(2025, 5, 7))
                .available(true)
                .price(150.0)
                .deleted(false)
                .build();

        availability = availabilityRepository.save(availability);

        reservationRequestDTO = ReservationRequestDTO.builder()
                .accommodationId(accommodation.getId())
                .startDate(LocalDate.of(2025, 5, 1))
                .endDate(LocalDate.of(2025, 5, 7))
                .numGuests(2)
                .userId(1)
                .build();
    }

    @Test
    void createReservationRequest_Success() {
        var result = reservationService.createReservationRequest(reservationRequestDTO);

        assertNotNull(result);
        assertEquals(accommodation.getId(), result.getAccommodation().getId());
        assertFalse(result.getCanceled());
        assertFalse(result.getDeleted());
    }

    @Test
    @Transactional
    void createAndCancelReservation_Success() {
        var reservation = reservationService.createReservationRequest(reservationRequestDTO);
        var cancelledReservation = reservationService.cancelOrDelete(reservation.getId());

        assertTrue(cancelledReservation.getCanceled());
    }

    @Test
    @Transactional
    void getAllPendingReservationsByHost_Success() {
        accommodation.setRequestApproval(RequestApproval.MANUALLY);
        accommodationRepository.save(accommodation);

        reservationService.createReservationRequest(reservationRequestDTO);

        var results = reservationService.getAllPendingReservationRequestsByHost(accommodation.getHostId());

        assertFalse(results.isEmpty());
        assertEquals(1, results.size());
    }

    @Test
    void createReservationRequest_ThrowsException_WhenAccommodationNotFound() {
        reservationRequestDTO.setAccommodationId(999);

        assertThrows(NotFoundException.class, () ->
                reservationService.createReservationRequest(reservationRequestDTO));
    }

    @Test
    void createReservationRequest_ThrowsException_WhenReservationConflict() {
        // Create first reservation
        reservationService.createReservationRequest(reservationRequestDTO);

        // Try to create overlapping reservation
        ReservationRequestDTO conflictingRequest = ReservationRequestDTO.builder()
                .accommodationId(accommodation.getId())
                .startDate(LocalDate.of(2025, 5, 3))
                .endDate(LocalDate.of(2025, 5, 5))
                .numGuests(2)
                .userId(2)
                .build();

        assertThrows(ReservationConflictException.class, () ->
                reservationService.createReservationRequest(conflictingRequest));
    }

    @Test
    void createReservationRequest_ThrowsException_WhenNoAvailability() {
        // Delete existing availability
        availabilityRepository.deleteAll();

        assertThrows(AvailabilityNotExists.class, () ->
                reservationService.createReservationRequest(reservationRequestDTO));
    }

    @Test
    @Transactional
    void cancelReservation_ThrowsException_WhenTooLateToCancel() {
        // Create reservation with past dates
        reservationRequestDTO.setStartDate(LocalDate.now());
        reservationRequestDTO.setEndDate(LocalDate.now().plusDays(5));

        availability.setStartDate(LocalDate.now());
        availability.setEndDate(LocalDate.now().plusDays(5));
        availabilityRepository.save(availability);

        var reservation = reservationService.createReservationRequest(reservationRequestDTO);

        assertThrows(ReservationCanNotCancel.class, () ->
                reservationService.cancelOrDelete(reservation.getId()));
    }

    @Test
    @Transactional
    void saveReservationsManually_Success() {
        // Set accommodation to manual approval
        accommodation.setRequestApproval(RequestApproval.MANUALLY);
        accommodationRepository.save(accommodation);

        // Create pending reservation
        var reservation = reservationService.createReservationRequest(reservationRequestDTO);
        assertEquals(RequestStatus.PENDING, reservation.getStatus());

        // Accept the reservation
        reservation.setStatus(RequestStatus.ACCEPTED);
        var results = reservationService.saveReservationsManually(List.of(reservation));

        assertFalse(results.isEmpty());
        assertEquals(RequestStatus.ACCEPTED, results.get(0).getStatus());

        // Verify availability was updated
        var updatedAvailability = availabilityRepository.findReservedAvailabilityToCancel(
                accommodation.getId(),
                reservationRequestDTO.getStartDate(),
                reservationRequestDTO.getEndDate()
        );
        assertNotNull(updatedAvailability);
        assertFalse(updatedAvailability.getAvailable());
    }

    @Test
    @Transactional
    void saveReservationsManually_DeclineReservation_Success() {
        // Set accommodation to manual approval
        accommodation.setRequestApproval(RequestApproval.MANUALLY);
        accommodationRepository.save(accommodation);

        // Create pending reservation
        var reservation = reservationService.createReservationRequest(reservationRequestDTO);

        // Decline the reservation
        reservation.setStatus(RequestStatus.DECLINED);
        var results = reservationService.saveReservationsManually(List.of(reservation));

        assertFalse(results.isEmpty());
        assertEquals(RequestStatus.DECLINED, results.get(0).getStatus());
        assertTrue(results.get(0).getCanceled());
    }

    @Test
    void didGuestHadReservationInAccommodation_Success() {
        reservationService.createReservationRequest(reservationRequestDTO);

        boolean result = reservationService.didGuestHadReservationInAccommodation(
                reservationRequestDTO.getUserId(),
                accommodation.getId()
        );

        assertTrue(result);
    }

    @Test
    void didGuestHadReservationInHostAccommodation_Success() {
        reservationService.createReservationRequest(reservationRequestDTO);

        boolean result = reservationService.didGuestHadReservationInHostAccommodation(
                reservationRequestDTO.getUserId(),
                accommodation.getHostId()
        );

        assertTrue(result);
    }
}