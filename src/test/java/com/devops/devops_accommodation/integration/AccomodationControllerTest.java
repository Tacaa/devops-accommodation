package com.devops.devops_accommodation.integration;

import com.devops.devops_accommodation.controllers.AccommodationController;
import com.devops.devops_accommodation.dto.AccommodationDTO;
import com.devops.devops_accommodation.dto.CreateAccommodationDTO;
import com.devops.devops_accommodation.dto.CreateAddressDTO;
import com.devops.devops_accommodation.enumeration.PriceType;
import com.devops.devops_accommodation.enumeration.RequestApproval;
import com.devops.devops_accommodation.exceptions.AttributeNotUniqueException;
import com.devops.devops_accommodation.exceptions.AttributeNullException;
import com.devops.devops_accommodation.model.Accommodation;
import com.devops.devops_accommodation.model.Address;
import com.devops.devops_accommodation.services.AccommodationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class AccommodationControllerTest {

  @Mock
  private AccommodationService accommodationService;

  @InjectMocks
  private AccommodationController accommodationController;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }



  @Test
  void testHome() {
    String response = accommodationController.home();
    assertEquals("Hello, World!", response);
  }


  @Test
  void testCreateAccommodation_Success_WithAddress() throws AttributeNotUniqueException, AttributeNullException {
    // Arrange
    CreateAccommodationDTO createAccommodationDTO = new CreateAccommodationDTO();
    createAccommodationDTO.setName("Name");

    CreateAddressDTO addressDTO = new CreateAddressDTO();
    addressDTO.setStreet("Street123");
    addressDTO.setNumber(12);
    addressDTO.setCity("City New");
    addressDTO.setCountry("Serbia");

    createAccommodationDTO.setAddress(addressDTO);

    Accommodation accommodation = new Accommodation();
    accommodation.setName("Name");

    Address address = new Address();
    address.setStreet("Street123");
    address.setNumber(12);
    address.setCity("City New");
    address.setCountry("Serbia");
    accommodation.setAddress(address);

    when(accommodationService.create(createAccommodationDTO)).thenReturn(accommodation);

    // Act
    ResponseEntity<Map<String, Object>> response = accommodationController.create(createAccommodationDTO);

    // Assert
    assertEquals(201, response.getStatusCodeValue());
    assertEquals(null, response.getBody().get("message"));

    // Extract response data and validate specific fields
    Map<String, Object> responseBody = response.getBody();
    AccommodationDTO responseDTO = (AccommodationDTO) responseBody.get("data");

    assertEquals("Name", responseDTO.getName());
    assertEquals("Street123", responseDTO.getAddress().getStreet());
    assertEquals(12, responseDTO.getAddress().getNumber());
    assertEquals("City New", responseDTO.getAddress().getCity());
    assertEquals("Serbia", responseDTO.getAddress().getCountry());
    verify(accommodationService, times(1)).create(createAccommodationDTO);
  }


  @Test
  void testCreateAccommodation_AttributeNotUniqueException() throws AttributeNotUniqueException, AttributeNullException {
    // Arrange
    CreateAccommodationDTO createAccommodationDTO = new CreateAccommodationDTO();
    createAccommodationDTO.setName("DuplicateName");

    when(accommodationService.create(createAccommodationDTO)).thenThrow(new AttributeNotUniqueException("Name must be unique"));

    // Act
    ResponseEntity<Map<String, Object>> response = accommodationController.create(createAccommodationDTO);

    // Assert
    assertEquals(400, response.getStatusCodeValue());
    assertEquals("Name must be unique", response.getBody().get("message"));
    assertEquals(null, response.getBody().get("data"));
    verify(accommodationService, times(1)).create(createAccommodationDTO);
  }

  @Test
  void testCreateAccommodation_AttributeNullException() throws AttributeNotUniqueException, AttributeNullException {
    // Arrange
    CreateAccommodationDTO createAccommodationDTO = new CreateAccommodationDTO();
    createAccommodationDTO.setName(null);

    when(accommodationService.create(createAccommodationDTO)).thenThrow(new AttributeNullException("Name cannot be null"));

    // Act
    ResponseEntity<Map<String, Object>> response = accommodationController.create(createAccommodationDTO);

    // Assert
    assertEquals(400, response.getStatusCodeValue());
    assertEquals("Name cannot be null", response.getBody().get("message"));
    assertEquals(null, response.getBody().get("data"));
    verify(accommodationService, times(1)).create(createAccommodationDTO);
  }
}