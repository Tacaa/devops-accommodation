package com.devops.devops_accommodation.services;

import com.devops.devops_accommodation.dto.SearchAccommodationDTO;
import com.devops.devops_accommodation.dto.SearchAccommodationResultDTO;
import com.devops.devops_accommodation.enumeration.PriceType;
import com.devops.devops_accommodation.exceptions.AttributeNullException;
import com.devops.devops_accommodation.model.Accommodation;
import com.devops.devops_accommodation.repository.AccommodationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
        if(searchDTO.getNumGuest() == null ||
        searchDTO.getStartDate() == null || searchDTO.getEndDate() == null){
            throw new AttributeNullException("Given number of guests or some of dates attributes are null");
        }

        List<Accommodation> accommodations = accommodationRepository.searchAccommodations(
                searchDTO.getCity(), searchDTO.getCountry(), searchDTO.getNumGuest(),
                searchDTO.getStartDate(), searchDTO.getEndDate());

        return accommodations.stream()
                .map(accommodation -> {
                    double unitPrice = accommodation.getPriceType() == PriceType.BY_PERSON
                            ? accommodation.getAvailabilities().get(0).getPrice() / accommodation.getMaxGuests()
                            : accommodation.getAvailabilities().get(0).getPrice();

                    long nights = ChronoUnit.DAYS.between(searchDTO.getStartDate(), searchDTO.getEndDate());
                    double totalPrice = accommodation.getPriceType() == PriceType.BY_PERSON
                            ? unitPrice * searchDTO.getNumGuest() * nights
                            : unitPrice * nights;

                    return SearchAccommodationResultDTO.from(
                            accommodation,
                            unitPrice,
                            totalPrice
                    );
                })
                .collect(Collectors.toList());
    }
}