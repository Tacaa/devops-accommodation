package com.devops.devops_accommodation.integration;

import com.devops.devops_accommodation.dto.AccommodationDTO;
import com.devops.devops_accommodation.dto.AddressDTO;
import com.devops.devops_accommodation.dto.CreateAccommodationDTO;
import com.devops.devops_accommodation.dto.CreateAddressDTO;
import com.devops.devops_accommodation.enumeration.Benefits;
import com.devops.devops_accommodation.enumeration.PriceType;
import com.devops.devops_accommodation.enumeration.RequestApproval;
import com.devops.devops_accommodation.exceptions.AttributeNullException;
import com.devops.devops_accommodation.model.Accommodation;
import com.devops.devops_accommodation.model.Address;
import com.devops.devops_accommodation.repository.AccommodationRepository;
import com.devops.devops_accommodation.services.AccommodationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class AccommodationServiceCreateAccommodationIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private AccommodationService accommodationService;

    @Autowired
    private AccommodationRepository accommodationRepository;

    private CreateAddressDTO addressDTO;
    private CreateAccommodationDTO accommodationDTO;

    @BeforeEach
    void setUp() {
        accommodationRepository.deleteAll();

        addressDTO = CreateAddressDTO.builder()
                .street("Main Street")
                .number(123)
                .city("Sample City")
                .country("Sample Country")
                .build();

        accommodationDTO = CreateAccommodationDTO.builder()
                .name("Sample Accommodation")
                .address(addressDTO)
                .benefits(new HashSet<>(Set.of(Benefits.WIFI, Benefits.FREE_PARKING, Benefits.GYM_ACCESS)))
                .photos(List.of("https://example.com/photo1.jpg", "https://example.com/photo2.jpg"))
                .minGuests(1)
                .maxGuests(4)
                .priceType(PriceType.BY_ACCOMMODATION)
                .requestApproval(RequestApproval.AUTOMATIC)
                .hostId(16)
                .build();
    }


    @Test
    void createAccommodation_ShouldPersistInDatabase_WhenValidInput() {
        Accommodation savedAccommodation = accommodationService.create(accommodationDTO);

        assertNotNull(savedAccommodation);
        assertEquals(1, accommodationRepository.count());

        Accommodation fetchedAccommodation = accommodationRepository.findById(savedAccommodation.getId()).orElse(null);
        assertNotNull(fetchedAccommodation);
        assertEquals("Sample Accommodation", fetchedAccommodation.getName());
    }

    @Test
    void createAccommodation_ShouldThrowException_WhenMissingRequiredFields() {
        CreateAccommodationDTO dto = new CreateAccommodationDTO();

        Exception exception = assertThrows(AttributeNullException.class, () -> {
            accommodationService.create(dto);
        });

        assertEquals("Given accommodation attribute is null", exception.getMessage());
    }



}
