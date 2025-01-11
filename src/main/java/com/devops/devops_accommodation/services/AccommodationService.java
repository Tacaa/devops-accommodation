package com.devops.devops_accommodation.services;

import com.devops.devops_accommodation.dto.SearchAccommodationDTO;
import com.devops.devops_accommodation.dto.SearchAccommodationResultDTO;
import com.devops.devops_accommodation.enumeration.PriceType;
import com.devops.devops_accommodation.exceptions.AttributeNullException;
import com.devops.devops_accommodation.model.Accommodation;
import com.devops.devops_accommodation.model.Availability;
import com.devops.devops_accommodation.repository.AccommodationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AccommodationService {

    @Autowired
    private AccommodationRepository accommodationRepository;

    public Accommodation getById(Integer id){
        return accommodationRepository.findById(id).orElse(null);
    }

    public List<Accommodation> getAllAccommodations(){
        return accommodationRepository.findAll();
    }

    public List<SearchAccommodationResultDTO> searchAccommodations(SearchAccommodationDTO searchDTO) {
        if (searchDTO.getNumGuest() == null || searchDTO.getStartDate() == null || searchDTO.getEndDate() == null) {
            throw new AttributeNullException("Given number of guests or some of dates attributes are null");
        }

        List<Accommodation> accommodations = accommodationRepository.searchAccommodations(
                searchDTO.getCity(), searchDTO.getCountry(), searchDTO.getNumGuest(),
                searchDTO.getStartDate(), searchDTO.getEndDate());

        return accommodations.stream()
                .map(accommodation -> {
                    List<Availability> availabilities = accommodation.getAvailabilities().stream()
                            .filter(av -> av.getAvailable() &&
                                    !av.getEndDate().isBefore(searchDTO.getStartDate()) &&
                                    !av.getStartDate().isAfter(searchDTO.getEndDate()))
                            .collect(Collectors.toList());

                    if (availabilities.isEmpty()) {
                        throw new RuntimeException("No availabilities found for the specified period");
                    }

                    // Calculate total price
                    double totalPrice = 0.0;
                    for (Availability availability : availabilities) {
                        LocalDate intervalStart = availability.getStartDate().isBefore(searchDTO.getStartDate())
                                ? searchDTO.getStartDate()
                                : availability.getStartDate();
                        LocalDate intervalEnd = availability.getEndDate().isAfter(searchDTO.getEndDate())
                                ? searchDTO.getEndDate()
                                : availability.getEndDate();

                        long nights = ChronoUnit.DAYS.between(intervalStart, intervalEnd);
                        totalPrice += accommodation.getPriceType() == PriceType.BY_PERSON
                                ? availability.getPrice() * searchDTO.getNumGuest() * nights
                                : availability.getPrice() * nights;
                    }

                    // Calculate unit price (average price per night)
                    long totalNights = ChronoUnit.DAYS.between(searchDTO.getStartDate(), searchDTO.getEndDate());
                    double unitPrice = totalPrice / totalNights;

                    return SearchAccommodationResultDTO.from(
                            accommodation,
                            unitPrice,
                            totalPrice
                    );
                })
                .collect(Collectors.toList());
    }
}