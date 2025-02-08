package com.devops.devops_accommodation.unit;

import com.devops.devops_accommodation.controllers.AvailabilityController;
import com.devops.devops_accommodation.dto.AvailabilityDTO;
import com.devops.devops_accommodation.dto.CreateAvailabilityDTO;
import com.devops.devops_accommodation.exceptions.PeriodNotAvailable;
import com.devops.devops_accommodation.exceptions.ResourceNotFoundException;
import com.devops.devops_accommodation.model.Accommodation;
import com.devops.devops_accommodation.model.Availability;
import com.devops.devops_accommodation.services.AvailabilityService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AvailabilityControllerTest {

    @Mock
    private AvailabilityService availabilityService;

    @InjectMocks
    private AvailabilityController availabilityController;

    private CreateAvailabilityDTO createAvailabilityDTO;
    private Availability availability;
    private Accommodation accommodation;

    @BeforeEach
    void setUp() {
        accommodation = new Accommodation();
        accommodation.setId(1);
        accommodation.setName("Test Accommodation");

        createAvailabilityDTO = CreateAvailabilityDTO.builder()
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusDays(7))
                .price(100.0)
                .deleted(false)
                .accommodationId(1)
                .build();

        availability = new Availability();
        availability.setId(1);
        availability.setStartDate(createAvailabilityDTO.getStartDate());
        availability.setEndDate(createAvailabilityDTO.getEndDate());
        availability.setPrice(createAvailabilityDTO.getPrice());
        availability.setDeleted(false);
        availability.setAvailable(true);
        availability.setAccommodation(accommodation);
    }

    @Test
    void createAvailability_ShouldReturn201_WhenValidRequest() {
        when(availabilityService.createAvailability(any(CreateAvailabilityDTO.class)))
                .thenReturn(availability);

        ResponseEntity<Map<String, Object>> response = availabilityController.createAvailability(createAvailabilityDTO);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNull(response.getBody().get("message"));
        assertNotNull(response.getBody().get("data"));

        AvailabilityDTO responseDTO = (AvailabilityDTO) response.getBody().get("data");
        assertEquals(availability.getId(), responseDTO.getId());
        assertEquals(availability.getPrice(), responseDTO.getPrice());
    }

    @Test
    void createAvailability_ShouldReturn400_WhenPeriodNotAvailable() {
        when(availabilityService.createAvailability(any(CreateAvailabilityDTO.class)))
                .thenThrow(new PeriodNotAvailable("Period is not available"));

        ResponseEntity<Map<String, Object>> response = availabilityController.createAvailability(createAvailabilityDTO);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Period is not available", response.getBody().get("message"));
        assertNull(response.getBody().get("data"));
    }

    @Test
    void createAvailability_ShouldReturn404_WhenAccommodationNotFound() {
        when(availabilityService.createAvailability(any(CreateAvailabilityDTO.class)))
                .thenThrow(new ResourceNotFoundException("Accommodation not found"));

        ResponseEntity<Map<String, Object>> response = availabilityController.createAvailability(createAvailabilityDTO);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Accommodation not found", response.getBody().get("message"));
        assertNull(response.getBody().get("data"));
    }

    @Test
    void updateAvailability_ShouldReturn200_WhenValidRequest() {
        when(availabilityService.updateAvailability(eq(1), any(CreateAvailabilityDTO.class)))
                .thenReturn(availability);

        ResponseEntity<Map<String, Object>> response = availabilityController.updateAvailability(1, createAvailabilityDTO);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNull(response.getBody().get("message"));
        assertNotNull(response.getBody().get("data"));

        AvailabilityDTO responseDTO = (AvailabilityDTO) response.getBody().get("data");
        assertEquals(availability.getId(), responseDTO.getId());
        assertEquals(availability.getPrice(), responseDTO.getPrice());
    }

    @Test
    void updateAvailability_ShouldReturn404_WhenAvailabilityNotFound() {
        when(availabilityService.updateAvailability(eq(1), any(CreateAvailabilityDTO.class)))
                .thenThrow(new ResourceNotFoundException("Availability not found"));

        ResponseEntity<Map<String, Object>> response = availabilityController.updateAvailability(1, createAvailabilityDTO);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Availability not found", response.getBody().get("message"));
        assertNull(response.getBody().get("data"));
    }

    @Test
    void updateAvailability_ShouldReturn400_WhenPeriodNotAvailable() {
        when(availabilityService.updateAvailability(eq(1), any(CreateAvailabilityDTO.class)))
                .thenThrow(new PeriodNotAvailable("Cannot update availability for this interval"));

        ResponseEntity<Map<String, Object>> response = availabilityController.updateAvailability(1, createAvailabilityDTO);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Cannot update availability for this interval", response.getBody().get("message"));
        assertNull(response.getBody().get("data"));
    }

    @Test
    void getAvailabilities_ShouldReturnList_WhenAvailabilitiesExist() {
        List<Availability> availabilities = Arrays.asList(availability);
        when(availabilityService.getAllAvailabilitiesByAccommodationId(1))
                .thenReturn(availabilities);

        ResponseEntity<List<AvailabilityDTO>> response = availabilityController.getAvailabilities(1);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals(availability.getId(), response.getBody().get(0).getId());
    }
}