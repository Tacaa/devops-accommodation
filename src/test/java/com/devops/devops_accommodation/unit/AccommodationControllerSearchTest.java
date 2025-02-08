package com.devops.devops_accommodation.unit;

import com.devops.devops_accommodation.controllers.AccommodationController;
import com.devops.devops_accommodation.dto.SearchAccommodationDTO;
import com.devops.devops_accommodation.dto.SearchAccommodationResultDTO;
import com.devops.devops_accommodation.exceptions.AttributeNullException;
import com.devops.devops_accommodation.services.AccommodationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AccommodationControllerSearchTest {

    @Mock
    private AccommodationService accommodationService;

    @InjectMocks
    private AccommodationController accommodationController;

    private SearchAccommodationDTO searchDTO;
    private List<SearchAccommodationResultDTO> mockSearchResults;

    @BeforeEach
    void setUp() {
        // Setup search DTO
        searchDTO = new SearchAccommodationDTO();
        searchDTO.setCity("Test City");
        searchDTO.setCountry("Test Country");
        searchDTO.setNumGuest(2);
        searchDTO.setStartDate(LocalDate.now().plusDays(1));
        searchDTO.setEndDate(LocalDate.now().plusDays(3));

        // Create mock search results
        mockSearchResults = new ArrayList<>();
        SearchAccommodationResultDTO mockResult = SearchAccommodationResultDTO.builder()
                .accommodationDTO(null)
                .unitPrice(100.0)
                .totalPrice(200.0)
                .build();
        mockSearchResults.add(mockResult);
    }

    @Test
    void searchAccommodations_ShouldReturnResultsWithMessage_WhenAccommodationsFound() {
        // Arrange
        when(accommodationService.searchAccommodations(searchDTO)).thenReturn(mockSearchResults);

        // Act
        ResponseEntity<Map<String, Object>> response = accommodationController.searchAccommodations(searchDTO);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Map<String, Object> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertNull(responseBody.get("message"));
        assertEquals(mockSearchResults, responseBody.get("data"));
        verify(accommodationService, times(1)).searchAccommodations(searchDTO);
    }

    @Test
    void searchAccommodations_ShouldReturnMessageForEmptyResults() {
        // Arrange
        when(accommodationService.searchAccommodations(searchDTO)).thenReturn(new ArrayList<>());

        // Act
        ResponseEntity<Map<String, Object>> response = accommodationController.searchAccommodations(searchDTO);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Map<String, Object> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals("There is no searched accommodation", responseBody.get("message"));
        assertEquals(new ArrayList<>(), responseBody.get("data"));
    }

    @Test
    void searchAccommodations_ShouldHandleAttributeNullException() {
        // Arrange
        when(accommodationService.searchAccommodations(searchDTO))
                .thenThrow(new AttributeNullException("Given number of guests or some of dates attributes are null"));

        // Act
        ResponseEntity<Map<String, Object>> response = accommodationController.searchAccommodations(searchDTO);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Map<String, Object> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals("Given number of guests or some of dates attributes are null", responseBody.get("message"));
        assertNull(responseBody.get("data"));
    }
}
