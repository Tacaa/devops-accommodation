package com.devops.devops_accommodation.unit;


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
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AccommodationServiceTest {

  @InjectMocks
  private AccommodationService accommodationService;

  @Mock
  private AccommodationRepository accommodationRepository;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void create_shouldReturnAccommodation_whenAllFieldsAreValid() {
    // Arrange
    CreateAddressDTO addressDTO = new CreateAddressDTO( "Street", 123, "City", "Country");
    CreateAccommodationDTO createAccommodationDTO = new CreateAccommodationDTO(
        "Test Accommodation",
        addressDTO,
        Set.of(Benefits.WIFI, Benefits.FREE_PARKING),
        List.of("photo1.jpg", "photo2.jpg"),
        2,
        5,
        PriceType.BY_PERSON,
        RequestApproval.AUTOMATIC,
        1
    );

    Accommodation mockAccommodation = Accommodation.builder()
        .name(createAccommodationDTO.getName())
        .address(Address.builder()
            .city(addressDTO.getCity())
            .street(addressDTO.getStreet())
            .number(addressDTO.getNumber())
            .country(addressDTO.getCountry())
            .build())
        .benefits(createAccommodationDTO.getBenefits())
        .photos(createAccommodationDTO.getPhotos())
        .minGuests(createAccommodationDTO.getMinGuests())
        .maxGuests(createAccommodationDTO.getMaxGuests())
        .priceType(createAccommodationDTO.getPriceType())
        .requestApproval(createAccommodationDTO.getRequestApproval())
        .hostId(createAccommodationDTO.getHostId())
        .build();

    when(accommodationRepository.save(any(Accommodation.class))).thenReturn(mockAccommodation);

    // Act
    Accommodation result = accommodationService.create(createAccommodationDTO);

    // Assert
    assertNotNull(result);
    assertEquals(createAccommodationDTO.getName(), result.getName());
    verify(accommodationRepository, times(1)).save(any(Accommodation.class));
  }

  @Test
  void create_shouldThrowAttributeNullException_whenRequiredAddressFieldsAreNull() {
    // Arrange
    CreateAccommodationDTO createAccommodationDTO = new CreateAccommodationDTO(
        "Test Accommodation",
        new CreateAddressDTO("Street", 123,null,  "Country"), // Missing city
        Set.of(Benefits.WIFI),
        List.of("photo1.jpg"),
        2,
        5,
        PriceType.BY_PERSON,
        RequestApproval.AUTOMATIC,
        1
    );

    // Act & Assert
    Exception exception = assertThrows(AttributeNullException.class, () -> {
      accommodationService.create(createAccommodationDTO);
    });

    assertEquals("Given address attribute is null", exception.getMessage());
    verify(accommodationRepository, never()).save(any(Accommodation.class));
  }

  void create_shouldThrowAttributeNullException_whenBuildingNumberIsNull() {
    // Arrange
    CreateAccommodationDTO createAccommodationDTO = new CreateAccommodationDTO(
        "Test Accommodation",
        new CreateAddressDTO( "Street", null, "City","Country"), // Missing building number
        Set.of(Benefits.WIFI),
        List.of("photo1.jpg"),
        2,
        5,
        PriceType.BY_PERSON,
        RequestApproval.AUTOMATIC,
        1
    );

    // Act & Assert
    Exception exception = assertThrows(AttributeNullException.class, () -> {
      accommodationService.create(createAccommodationDTO);
    });

    assertEquals("Given address attribute is null", exception.getMessage());
    verify(accommodationRepository, never()).save(any(Accommodation.class));
  }

  @Test
  void create_shouldThrowAttributeNullException_whenAddressIsNull() {
    // Arrange
    CreateAccommodationDTO createAccommodationDTO = new CreateAccommodationDTO(
        "Test Accommodation",
        null, // Missing address
        Set.of(Benefits.WIFI),
        List.of("photo1.jpg"),
        2,
        5,
        PriceType.BY_PERSON,
        RequestApproval.AUTOMATIC,
        1
    );

    // Act & Assert
    Exception exception = assertThrows(AttributeNullException.class, () -> {
      accommodationService.create(createAccommodationDTO);
    });

    assertEquals("Given accommodation attribute is null", exception.getMessage());
    verify(accommodationRepository, never()).save(any(Accommodation.class));
  }
}

