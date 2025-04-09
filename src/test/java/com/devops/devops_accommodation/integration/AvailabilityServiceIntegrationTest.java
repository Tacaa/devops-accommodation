package com.devops.devops_accommodation.integration;

import com.devops.devops_accommodation.dto.CreateAvailabilityDTO;
import com.devops.devops_accommodation.enumeration.Benefits;
import com.devops.devops_accommodation.enumeration.PriceType;
import com.devops.devops_accommodation.enumeration.RequestApproval;
import com.devops.devops_accommodation.exceptions.PeriodNotAvailable;
import com.devops.devops_accommodation.exceptions.ResourceNotFoundException;
import com.devops.devops_accommodation.model.Accommodation;
import com.devops.devops_accommodation.model.Address;
import com.devops.devops_accommodation.model.Availability;
import com.devops.devops_accommodation.repository.AccommodationRepository;
import com.devops.devops_accommodation.repository.AvailabilityRepository;
import com.devops.devops_accommodation.repository.ReservationRepository;
import com.devops.devops_accommodation.services.AvailabilityService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
@ActiveProfiles("test")
public class AvailabilityServiceIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private AvailabilityService availabilityService;

    @Autowired
    private AvailabilityRepository availabilityRepository;

    @Autowired
    private AccommodationRepository accommodationRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    private CreateAvailabilityDTO availabilityDTO;
    private Accommodation accommodation;
    private Address address;
    private String hostId = "16";

    @BeforeEach
    void setUp() {
        availabilityRepository.deleteAll();
        accommodationRepository.deleteAll();
        reservationRepository.deleteAll();

        address = Address.builder()
                .street("Main Street")
                .number(123)
                .city("Sample City")
                .country("Sample Country")
                .build();


        // Create and save a test accommodation
        accommodation = Accommodation.builder()
                .name("Sample Accommodation")
                .address(address)
                .benefits(new HashSet<>(Set.of(Benefits.WIFI, Benefits.FREE_PARKING, Benefits.GYM_ACCESS)))
                .photos(List.of("https://example.com/photo1.jpg", "https://example.com/photo2.jpg"))
                .minGuests(1)
                .maxGuests(4)
                .priceType(PriceType.BY_ACCOMMODATION)
                .requestApproval(RequestApproval.AUTOMATIC)
                .hostId(Integer.valueOf(hostId))
                .build();
        accommodation = accommodationRepository.save(accommodation);

        // Create test availability DTO
        availabilityDTO = CreateAvailabilityDTO.builder()
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusDays(7))
                .price(100.0)
                .deleted(false)
                .accommodationId(accommodation.getId())
                .build();
    }

    @AfterEach
    void tearDown() {
        accommodationRepository.deleteAll();
    }

    @AfterAll
    static void afterAll() {
        if (postgres != null && postgres.isRunning()) {
            postgres.stop();
        }
    }

    @Test
    void createAvailability_ShouldPersistInDatabase_WhenValidInput() {
        Availability savedAvailability = availabilityService.createAvailability(availabilityDTO, hostId);

        assertNotNull(savedAvailability);
        assertEquals(1, availabilityRepository.count());

        Availability fetchedAvailability = availabilityRepository.findById(savedAvailability.getId()).orElse(null);
        assertNotNull(fetchedAvailability);
        assertEquals(availabilityDTO.getPrice(), fetchedAvailability.getPrice());
        assertEquals(availabilityDTO.getStartDate(), fetchedAvailability.getStartDate());
        assertEquals(availabilityDTO.getEndDate(), fetchedAvailability.getEndDate());
    }

    @Test
    void createAvailability_ShouldThrowException_WhenAccommodationNotFound() {
        availabilityDTO.setAccommodationId(-1);

        assertThrows(ResourceNotFoundException.class, () -> {
            availabilityService.createAvailability(availabilityDTO, hostId);
        });
    }

    @Test
    void createAvailability_ShouldThrowException_WhenPeriodOverlaps() {
        // Create first availability
        availabilityService.createAvailability(availabilityDTO, hostId);

        // Try to create overlapping availability
        CreateAvailabilityDTO overlappingDTO = CreateAvailabilityDTO.builder()
                .startDate(availabilityDTO.getStartDate().plusDays(1))
                .endDate(availabilityDTO.getEndDate().plusDays(1))
                .price(150.0)
                .deleted(false)
                .accommodationId(accommodation.getId())
                .build();

        assertThrows(PeriodNotAvailable.class, () -> {
            availabilityService.createAvailability(overlappingDTO, hostId);
        });
    }

    @Test
    void getAllAvailabilities_ShouldReturnList_WhenAccommodationExists() {
        availabilityService.createAvailability(availabilityDTO, hostId);

        List<Availability> availabilities = availabilityService.getAllAvailabilitiesByAccommodationId(accommodation.getId());

        assertFalse(availabilities.isEmpty());
        assertEquals(1, availabilities.size());
    }

    @Test
    void updateAvailability_ShouldUpdateDatabase_WhenValidInput() {
        Availability savedAvailability = availabilityService.createAvailability(availabilityDTO, hostId);

        CreateAvailabilityDTO updateDTO = CreateAvailabilityDTO.builder()
                .startDate(availabilityDTO.getStartDate())
                .endDate(availabilityDTO.getEndDate())
                .price(200.0)
                .deleted(false)
                .accommodationId(accommodation.getId())
                .build();

        Availability updatedAvailability = availabilityService.updateAvailability(savedAvailability.getId(), updateDTO, hostId);

        assertNotNull(updatedAvailability);
        assertEquals(200.0, updatedAvailability.getPrice());
    }
}