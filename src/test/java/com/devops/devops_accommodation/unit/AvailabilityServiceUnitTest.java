package com.devops.devops_accommodation.unit;

import com.devops.devops_accommodation.dto.CreateAvailabilityDTO;
import com.devops.devops_accommodation.exceptions.PeriodNotAvailable;
import com.devops.devops_accommodation.exceptions.ResourceNotFoundException;
import com.devops.devops_accommodation.model.Accommodation;
import com.devops.devops_accommodation.model.Availability;
import com.devops.devops_accommodation.repository.AccommodationRepository;
import com.devops.devops_accommodation.repository.AvailabilityRepository;
import com.devops.devops_accommodation.repository.ReservationRepository;
import com.devops.devops_accommodation.services.AvailabilityService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AvailabilityServiceUnitTest {

    @Mock
    private AvailabilityRepository availabilityRepository;

    @Mock
    private AccommodationRepository accommodationRepository;

    @Mock
    private ReservationRepository reservationRepository;

    @InjectMocks
    private AvailabilityService availabilityService;

    private CreateAvailabilityDTO availabilityDTO;
    private Accommodation accommodation;
    private String hostId = "16";

    @BeforeEach
    void setUp() {
        accommodation = new Accommodation();
        accommodation.setId(1);
        accommodation.setName("Test Accommodation");
        accommodation.setHostId(Integer.valueOf(hostId));

        availabilityDTO = CreateAvailabilityDTO.builder()
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusDays(7))
                .price(100.0)
                .deleted(false)
                .accommodationId(accommodation.getId())
                .build();
    }

    @Test
    void createAvailability_ShouldSaveAndReturnAvailability_WhenValidInput() {
        when(accommodationRepository.findById(any())).thenReturn(Optional.of(accommodation));
        when(reservationRepository.existsByAccommodationAndDateRange(any(), any(), any())).thenReturn(false);
        when(availabilityRepository.existsAvailabilityByAccommodationAndDateRange(any(), any(), any())).thenReturn(false);

        Availability availability = new Availability();
        availability.setId(1);
        availability.setPrice(availabilityDTO.getPrice());
        when(availabilityRepository.save(any(Availability.class))).thenReturn(availability);

        Availability createdAvailability = availabilityService.createAvailability(availabilityDTO, hostId);

        assertNotNull(createdAvailability);
        assertEquals(availabilityDTO.getPrice(), createdAvailability.getPrice());
        verify(availabilityRepository, times(1)).save(any(Availability.class));
    }

    @Test
    void createAvailability_ShouldThrowException_WhenAccommodationNotFound() {
        when(accommodationRepository.findById(any())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            availabilityService.createAvailability(availabilityDTO, hostId);
        });

        verify(availabilityRepository, never()).save(any(Availability.class));
    }

    @Test
    void createAvailability_ShouldThrowException_WhenReservationExists() {
        when(accommodationRepository.findById(any())).thenReturn(Optional.of(accommodation));
        when(reservationRepository.existsByAccommodationAndDateRange(any(), any(), any())).thenReturn(true);

        assertThrows(PeriodNotAvailable.class, () -> {
            availabilityService.createAvailability(availabilityDTO, hostId);
        });

        verify(availabilityRepository, never()).save(any(Availability.class));
    }

    @Test
    void updateAvailability_ShouldUpdateAndReturnAvailability_WhenValidInput() {
        Availability existingAvailability = new Availability();
        existingAvailability.setId(1);
        existingAvailability.setAccommodation(accommodation);
        existingAvailability.setStartDate(LocalDate.now());
        existingAvailability.setEndDate(LocalDate.now().plusDays(7));

        when(availabilityRepository.findById(any())).thenReturn(Optional.of(existingAvailability));
        when(reservationRepository.existsByAccommodationAndDateRange(any(), any(), any())).thenReturn(false);
        when(availabilityRepository.save(any(Availability.class))).thenReturn(existingAvailability);

        Availability updatedAvailability = availabilityService.updateAvailability(1, availabilityDTO, hostId);

        assertNotNull(updatedAvailability);
        verify(availabilityRepository, times(1)).save(any(Availability.class));
    }

    @Test
    void updateAvailability_ShouldThrowException_WhenAvailabilityNotFound() {
        when(availabilityRepository.findById(any())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            availabilityService.updateAvailability(1, availabilityDTO, hostId);
        });

        verify(availabilityRepository, never()).save(any(Availability.class));
    }
}