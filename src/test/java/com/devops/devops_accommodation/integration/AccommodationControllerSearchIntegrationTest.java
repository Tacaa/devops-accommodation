package com.devops.devops_accommodation.integration;

import com.devops.devops_accommodation.dto.SearchAccommodationDTO;
import com.devops.devops_accommodation.model.Accommodation;
import com.devops.devops_accommodation.model.Address;
import com.devops.devops_accommodation.model.Availability;
import com.devops.devops_accommodation.enumeration.Benefits;
import com.devops.devops_accommodation.enumeration.PriceType;
import com.devops.devops_accommodation.enumeration.RequestApproval;
import com.devops.devops_accommodation.repository.AccommodationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class AccommodationControllerSearchIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>("postgres:15.3")
            .withDatabaseName("accommodation-test")
            .withUsername("test")
            .withPassword("test");

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private AccommodationRepository accommodationRepository;

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgresContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgresContainer::getUsername);
        registry.add("spring.datasource.password", postgresContainer::getPassword);
    }

    @BeforeEach
    void cleanup() {
        accommodationRepository.deleteAll();
    }

    private Accommodation createAndSaveTestAccommodation() {
        Address address = Address.builder()
                .street("Test Street")
                .number(123)
                .city("Test City")
                .country("Test Country")
                .build();

        Availability availability = new Availability();
        availability.setStartDate(LocalDate.now());
        availability.setEndDate(LocalDate.now().plusDays(10));
        availability.setPrice(100.0);
        availability.setAvailable(true);

        Accommodation testAccommodation = new Accommodation();
        testAccommodation.setName("Test Accommodation");
        testAccommodation.setAddress(address);
        testAccommodation.setBenefits(Set.of(Benefits.WIFI));
        testAccommodation.setPhotos(List.of("photo.jpg"));
        testAccommodation.setMinGuests(1);
        testAccommodation.setMaxGuests(4);
        testAccommodation.setPriceType(PriceType.BY_ACCOMMODATION);
        testAccommodation.setRequestApproval(RequestApproval.AUTOMATIC);
        testAccommodation.setHostId(1);
        testAccommodation.setAvailabilities(List.of(availability));

        return accommodationRepository.save(testAccommodation);
    }


    @Test
    void shouldReturnEmptyResultsWhenNoMatchingAccommodations() {
        // Given
        SearchAccommodationDTO searchDTO = new SearchAccommodationDTO();
        searchDTO.setCity("Non-existent City");
        searchDTO.setCountry("Non-existent Country");
        searchDTO.setNumGuest(2);
        searchDTO.setStartDate(LocalDate.now().plusDays(1));
        searchDTO.setEndDate(LocalDate.now().plusDays(3));

        // When
        ParameterizedTypeReference<Map<String, Object>> responseType =
                new ParameterizedTypeReference<>() {};

        ResponseEntity<Map<String, Object>> searchResponse = restTemplate.exchange(
                "/api/accommodation/search",
                HttpMethod.POST,
                new HttpEntity<>(searchDTO),
                responseType
        );

        // Then
        assertThat(searchResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(searchResponse.getBody()).isNotNull();

        Map<String, Object> responseBody = searchResponse.getBody();
        assertThat(responseBody.get("message")).isEqualTo("There is no searched accommodation");

        List<Map<String, Object>> searchResults = (List<Map<String, Object>>) responseBody.get("data");
        assertThat(searchResults).isEmpty();
    }

    @Test
    void shouldReturnBadRequestWhenRequiredFieldsAreMissing() {
        // Given
        SearchAccommodationDTO searchDTO = new SearchAccommodationDTO();
        searchDTO.setCity("Test City"); // Intentionally missing required fields

        // When
        ParameterizedTypeReference<Map<String, Object>> responseType =
                new ParameterizedTypeReference<>() {};

        ResponseEntity<Map<String, Object>> searchResponse = restTemplate.exchange(
                "/api/accommodation/search",
                HttpMethod.POST,
                new HttpEntity<>(searchDTO),
                responseType
        );

        // Then
        assertThat(searchResponse.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(searchResponse.getBody()).isNotNull();

        Map<String, Object> responseBody = searchResponse.getBody();
        assertThat(responseBody.get("message"))
                .isEqualTo("Given number of guests or some of dates attributes are null");
        assertThat(responseBody.get("data")).isNull();
    }
}