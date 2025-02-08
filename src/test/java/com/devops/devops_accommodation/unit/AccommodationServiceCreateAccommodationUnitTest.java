package com.devops.devops_accommodation.unit;

import com.devops.devops_accommodation.dto.CreateAccommodationDTO;
import com.devops.devops_accommodation.dto.CreateAddressDTO;
import com.devops.devops_accommodation.enumeration.Benefits;
import com.devops.devops_accommodation.enumeration.PriceType;
import com.devops.devops_accommodation.enumeration.RequestApproval;
import com.devops.devops_accommodation.exceptions.AttributeNullException;
import com.devops.devops_accommodation.model.Accommodation;
import com.devops.devops_accommodation.repository.AccommodationRepository;
import com.devops.devops_accommodation.services.AccommodationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.List;
import java.util.Set;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AccommodationServiceCreateAccommodationUnitTest {

    @Mock
    private AccommodationRepository accommodationRepository;

    @InjectMocks
    private AccommodationService accommodationService;

    @Test
    void createAccommodation_ShouldThrowException_WhenAnyAttributeIsNull() {
        CreateAccommodationDTO dto = new CreateAccommodationDTO();

        Exception exception = assertThrows(AttributeNullException.class, () -> {
            accommodationService.create(dto);
        });

        assertEquals("Given accommodation attribute is null", exception.getMessage());
    }

    @Test
    void createAccommodation_ShouldThrowException_WhenAddressAttributeIsNull() {
        CreateAccommodationDTO dto = CreateAccommodationDTO.builder()
                .name("Sample Accommodation")
                .benefits(new HashSet<>(Set.of(Benefits.WIFI, Benefits.FREE_PARKING, Benefits.GYM_ACCESS)))
                .photos(List.of("https://example.com/photo1.jpg", "https://example.com/photo2.jpg"))
                .minGuests(1)
                .maxGuests(4)
                .priceType(PriceType.BY_ACCOMMODATION)
                .requestApproval(RequestApproval.AUTOMATIC)
                .hostId(16)
                .build();
        dto.setAddress(new CreateAddressDTO());

        Exception exception = assertThrows(AttributeNullException.class, () -> {
            accommodationService.create(dto);
        });

        assertEquals("Given address attribute is null", exception.getMessage());
    }

    @Test
    void createAccommodation_ShouldSaveAndReturnAccommodation_WhenValidInput() {
        CreateAccommodationDTO dto = new CreateAccommodationDTO();
        dto.setName("Sample Accommodation");
        dto.setAddress(new CreateAddressDTO("Main Street", 123, "Sample City", "Sample Country"));
        dto.setBenefits(Set.of(Benefits.WIFI, Benefits.FREE_PARKING));
        dto.setPhotos(List.of("https://example.com/photo.jpg"));
        dto.setMinGuests(1);
        dto.setMaxGuests(4);
        dto.setPriceType(PriceType.BY_ACCOMMODATION);
        dto.setRequestApproval(RequestApproval.AUTOMATIC);
        dto.setHostId(16);

        Accommodation accommodation = new Accommodation();
        accommodation.setId(1);
        accommodation.setName(dto.getName());

        when(accommodationRepository.save(any(Accommodation.class))).thenReturn(accommodation);

        Accommodation createdAccommodation = accommodationService.create(dto);

        assertNotNull(createdAccommodation);
        assertEquals("Sample Accommodation", createdAccommodation.getName());
        verify(accommodationRepository, times(1)).save(any(Accommodation.class));
    }
}
