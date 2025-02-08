package com.devops.devops_accommodation.unit;
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
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AccommodationServiceSearchUnitTest {
    @Mock
    private AccommodationRepository accommodationRepository;

    @InjectMocks
    private AccommodationService accommodationService;

    private SearchAccommodationDTO searchDTO;
    private List<Accommodation> mockAccommodations;
    private Address address;
    private Accommodation mockAccommodation;

    @BeforeEach
    void setUp() {
        // Setup basic search DTO
        searchDTO = new SearchAccommodationDTO();
        searchDTO.setCity("Sample City");
        searchDTO.setCountry("Sample Country");
        searchDTO.setNumGuest(2);
        searchDTO.setStartDate(LocalDate.now());
        searchDTO.setEndDate(LocalDate.now().plusDays(2));

        address = Address.builder()
                .id(1)
                .street("Main Street")
                .number(123)
                .city("Sample City")
                .country("Sample Country")
                .build();

        mockAccommodation = Accommodation.builder()
                .name("Sample Accommodation")
                .address(address)
                .benefits(new HashSet<>(Set.of(Benefits.WIFI, Benefits.FREE_PARKING, Benefits.GYM_ACCESS)))
                .photos(List.of("https://example.com/photo1.jpg", "https://example.com/photo2.jpg"))
                .minGuests(1)
                .maxGuests(4)
                .priceType(PriceType.BY_ACCOMMODATION)
                .requestApproval(RequestApproval.AUTOMATIC)
                .hostId(16)
                .build();

        mockAccommodation.setId(1);
        mockAccommodation.setName("Test Accommodation");
        mockAccommodation.setPriceType(PriceType.BY_ACCOMMODATION);

        // Setup availability
        Availability availability = new Availability();
        availability.setStartDate(LocalDate.now().minusDays(1));
        availability.setEndDate(LocalDate.now().plusDays(3));
        availability.setPrice(100.0);
        availability.setAvailable(true);
        mockAccommodation.setAvailabilities(List.of(availability));

        mockAccommodations = List.of(mockAccommodation);
    }

    @Test
    void searchAccommodations_ShouldThrowException_WhenNumGuestIsNull() {
        searchDTO.setNumGuest(null);

        Exception exception = assertThrows(AttributeNullException.class, () -> {
            accommodationService.searchAccommodations(searchDTO);
        });

        assertEquals("Given number of guests or some of dates attributes are null", exception.getMessage());
    }

    @Test
    void searchAccommodations_ShouldThrowException_WhenStartDateIsNull() {
        searchDTO.setStartDate(null);

        Exception exception = assertThrows(AttributeNullException.class, () -> {
            accommodationService.searchAccommodations(searchDTO);
        });

        assertEquals("Given number of guests or some of dates attributes are null", exception.getMessage());
    }



    @Test
    void searchAccommodations_ShouldCalculateCorrectPrices_WhenAccommodationFoundByAccommodation() {
        when(accommodationRepository.searchAccommodations(any(), any(), any(), any(), any()))
                .thenReturn(mockAccommodations);
        when(accommodationRepository.searchReservedAccommodations(any(), any(), any(), any(), any()))
                .thenReturn(Collections.emptyList());

        List<SearchAccommodationResultDTO> results = accommodationService.searchAccommodations(searchDTO);

        assertFalse(results.isEmpty());
        SearchAccommodationResultDTO result = results.get(0);
        assertEquals(100.0, result.getUnitPrice()); // Price per night
        assertEquals(200.0, result.getTotalPrice()); // Total for 2 nights
    }

    @Test
    void searchAccommodations_ShouldCalculateCorrectPrices_WhenAccommodationFoundByPerson() {
        mockAccommodation.setPriceType(PriceType.BY_PERSON);

        when(accommodationRepository.searchAccommodations(any(), any(), any(), any(), any()))
                .thenReturn(mockAccommodations);
        when(accommodationRepository.searchReservedAccommodations(any(), any(), any(), any(), any()))
                .thenReturn(Collections.emptyList());

        List<SearchAccommodationResultDTO> results = accommodationService.searchAccommodations(searchDTO);

        assertFalse(results.isEmpty());
        SearchAccommodationResultDTO result = results.get(0);
        assertEquals(200.0, result.getUnitPrice()); // Price per night (100 * 2 guests)
        assertEquals(400.0, result.getTotalPrice()); // Total for 2 nights with 2 guests
    }
}
