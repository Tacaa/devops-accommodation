package com.devops.devops_accommodation.unit;

import com.devops.devops_accommodation.controllers.AccommodationController;
import com.devops.devops_accommodation.dto.CreateAccommodationDTO;
import com.devops.devops_accommodation.dto.CreateAddressDTO;
import com.devops.devops_accommodation.enumeration.Benefits;
import com.devops.devops_accommodation.enumeration.PriceType;
import com.devops.devops_accommodation.enumeration.RequestApproval;
import com.devops.devops_accommodation.exceptions.AttributeNullException;
import com.devops.devops_accommodation.model.Accommodation;
import com.devops.devops_accommodation.model.Address;
import com.devops.devops_accommodation.services.AccommodationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class AccommodationControllerCreateSuccessTest {

    @Mock
    private AccommodationService accommodationService;

    @InjectMocks
    private AccommodationController accommodationController;



    @Test
    void createAccommodation_ShouldReturn201_WhenValidRequest() throws Exception {
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

        Address address = Address.builder()
                .id(1)
                .street("Main Street")
                .city("Sample City")
                .country("Sample Country")
                .build();

        Accommodation accommodation = Accommodation.builder()
                .id(1)
                .build();
        accommodation.setName("Sample Accommodation");
        accommodation.setAddress(address);
        accommodation.setBenefits(Set.of(Benefits.WIFI, Benefits.FREE_PARKING));
        accommodation.setPhotos(List.of("https://example.com/photo.jpg"));
        accommodation.setMinGuests(1);
        accommodation.setMaxGuests(4);
        accommodation.setPriceType(PriceType.BY_ACCOMMODATION);
        accommodation.setRequestApproval(RequestApproval.AUTOMATIC);
        accommodation.setHostId(16);

        when(accommodationService.create(any(CreateAccommodationDTO.class))).thenReturn(accommodation);

        ResponseEntity<Map<String, Object>> response = accommodationController.create(dto);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNull(response.getBody().get("message"));
    }

    @Test
    void createAccommodation_ShouldReturn400_WhenAttributeIsNull() throws Exception {
        CreateAccommodationDTO dto = new CreateAccommodationDTO();
        dto.setName("Sample Accommodation");
        dto.setAddress(new CreateAddressDTO("Main Street", 123, "Sample City", "Sample Country"));
        dto.setBenefits(Set.of(Benefits.WIFI, Benefits.FREE_PARKING));
        dto.setPhotos(List.of("https://example.com/photo.jpg"));
        dto.setMinGuests(1);
        dto.setPriceType(PriceType.BY_ACCOMMODATION);
        dto.setRequestApproval(RequestApproval.AUTOMATIC);
        dto.setHostId(16);

        when(accommodationService.create(any(CreateAccommodationDTO.class)))
                .thenThrow(new AttributeNullException("Given accommodation attribute is null"));

        ResponseEntity<Map<String, Object>> response = accommodationController.create(dto);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Given accommodation attribute is null", response.getBody().get("message"));
        assertNull(response.getBody().get("data"));
    }
}