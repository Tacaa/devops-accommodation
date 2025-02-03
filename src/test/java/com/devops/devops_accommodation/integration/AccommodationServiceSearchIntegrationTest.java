package com.devops.devops_accommodation.integration;

import com.devops.devops_accommodation.dto.SearchAccommodationDTO;
import com.devops.devops_accommodation.dto.SearchAccommodationResultDTO;
import com.devops.devops_accommodation.enumeration.Benefits;
import com.devops.devops_accommodation.enumeration.PriceType;
import com.devops.devops_accommodation.enumeration.RequestApproval;
import com.devops.devops_accommodation.exceptions.AttributeNullException;
import com.devops.devops_accommodation.model.Accommodation;
import com.devops.devops_accommodation.model.Address;
import com.devops.devops_accommodation.model.Availability;
import com.devops.devops_accommodation.repository.AccommodationRepository;
import com.devops.devops_accommodation.services.AccommodationService;
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

@SpringBootTest
@ActiveProfiles("test")
public class AccommodationServiceSearchIntegrationTest extends BaseIntegrationTest {
    @Autowired
    private AccommodationService accommodationService;

    @Autowired
    private AccommodationRepository accommodationRepository;


    private Accommodation accommodation;
    private SearchAccommodationDTO searchDTO;

    @BeforeEach
    void setUp() {
        accommodationRepository.deleteAll();

        // Create test accommodation
        Address address = Address.builder()
                .street("Test Street")
                .number(123)
                .city("Test City")
                .country("Test Country")
                .build();

        accommodation = new Accommodation();
        accommodation.setName("Test Accommodation");
        accommodation.setAddress(address);
        accommodation.setBenefits(Set.of(Benefits.WIFI));
        accommodation.setPhotos(List.of("photo.jpg"));
        accommodation.setMinGuests(1);
        accommodation.setMaxGuests(4);
        accommodation.setPriceType(PriceType.BY_ACCOMMODATION);
        accommodation.setRequestApproval(RequestApproval.AUTOMATIC);
        accommodation.setHostId(1);
        //accommodation = accommodationRepository.save(accommodation);

        // Add availability
        Availability availability = new Availability();
        availability.setStartDate(LocalDate.now());
        availability.setEndDate(LocalDate.now().plusDays(10));
        availability.setPrice(100.0);
        availability.setAvailable(true);
        accommodation = accommodationRepository.save(accommodation);

        // Setup search DTO
        searchDTO = new SearchAccommodationDTO();
        searchDTO.setCity("Test City");
        searchDTO.setCountry("Test Country");
        searchDTO.setNumGuest(2);
        searchDTO.setStartDate(LocalDate.now().plusDays(1));
        searchDTO.setEndDate(LocalDate.now().plusDays(3));
    }

    @Test
    void searchAccommodations_ShouldReturnEmptyList_WhenNoMatchingCity() {
        searchDTO.setCity("Non-existent City");

        List<SearchAccommodationResultDTO> results = accommodationService.searchAccommodations(searchDTO);

        assertTrue(results.isEmpty());
    }

    @Test
    void searchAccommodations_ShouldReturnEmptyList_WhenTooManyGuests() {
        searchDTO.setNumGuest(10); // More than maxGuests

        List<SearchAccommodationResultDTO> results = accommodationService.searchAccommodations(searchDTO);

        assertTrue(results.isEmpty());
    }

    @Test
    void searchAccommodations_ShouldThrowException_WhenMissingRequiredFields() {
        searchDTO.setNumGuest(null);

        Exception exception = assertThrows(AttributeNullException.class, () -> {
            accommodationService.searchAccommodations(searchDTO);
        });

        assertEquals("Given number of guests or some of dates attributes are null", exception.getMessage());
    }

}
