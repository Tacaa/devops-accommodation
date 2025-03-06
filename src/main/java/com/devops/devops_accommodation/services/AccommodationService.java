package com.devops.devops_accommodation.services;

import com.devops.devops_accommodation.dto.AvailabilityDTO;
import com.devops.devops_accommodation.dto.SearchAccommodationDTO;
import com.devops.devops_accommodation.dto.SearchAccommodationResultDTO;
import com.devops.devops_accommodation.enumeration.PriceType;
import com.devops.devops_accommodation.exceptions.AttributeNullException;
import com.devops.devops_accommodation.model.Accommodation;
import com.devops.devops_accommodation.model.Availability;
import com.devops.devops_accommodation.dto.CreateAccommodationDTO;
import com.devops.devops_accommodation.dto.CreateAddressDTO;
import com.devops.devops_accommodation.exceptions.AttributeNullException;
import com.devops.devops_accommodation.model.Address;
import com.devops.devops_accommodation.repository.AccommodationRepository;
import org.springframework.transaction.annotation.Transactional;
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

    @Transactional(readOnly = true)
    public Accommodation getById(Integer id){
        return accommodationRepository.findById(id).orElse(null);
    }

    @Transactional(readOnly = true)
    public List<Accommodation> getAllAccommodations(){
        return accommodationRepository.findAll();
    }

  @Transactional(readOnly = true)
    public List<SearchAccommodationResultDTO> searchAccommodations(SearchAccommodationDTO searchDTO) {
        if (searchDTO.getNumGuest() == null || searchDTO.getStartDate() == null || searchDTO.getEndDate() == null) {
            throw new AttributeNullException("Given number of guests or some of dates attributes are null");
        }

        List<Accommodation> accommodations = accommodationRepository.searchAccommodations(
                searchDTO.getCity(), searchDTO.getCountry(), searchDTO.getNumGuest(),
                searchDTO.getStartDate(), searchDTO.getEndDate());

        List<Accommodation> reservedAccommodations = accommodationRepository.searchReservedAccommodations(searchDTO.getCity(), searchDTO.getCountry(), searchDTO.getNumGuest(),
                searchDTO.getStartDate(), searchDTO.getEndDate());

        //izbaciti accommodations koji imaju rezervaciju za taj period tko da dobijem samo slobodne
        for (Accommodation reserved : reservedAccommodations) {
            accommodations.removeIf(a -> a.getId().equals(reserved.getId()));
        }

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

          long totalNights = ChronoUnit.DAYS.between(searchDTO.getStartDate(), searchDTO.getEndDate());
          double unitPrice = totalPrice / totalNights;

          // ✅ Convert Availability entities to DTOs before returning
          List<AvailabilityDTO> availabilityDTOs = availabilities.stream()
              .map(AvailabilityDTO::from)
              .collect(Collectors.toList());

          return SearchAccommodationResultDTO.from(
              accommodation,
              unitPrice,
              totalPrice
          );
        })
        .collect(Collectors.toList());

  }

    public Accommodation create(CreateAccommodationDTO createAccommodationDTO) {
        if(createAccommodationDTO.getName() == null
                || createAccommodationDTO.getBenefits() == null
                || createAccommodationDTO.getMinGuests() == null
                || createAccommodationDTO.getMaxGuests() == null
                || createAccommodationDTO.getPriceType() == null
                || createAccommodationDTO.getRequestApproval() == null
                || createAccommodationDTO.getHostId() == null
                ||  createAccommodationDTO.getPhotos() == null
                || createAccommodationDTO.getAddress() == null){
            throw new AttributeNullException("Given accommodation attribute is null");
        }

        if(createAccommodationDTO.getAddress().getCity() == null
            || createAccommodationDTO.getAddress().getNumber() == null
                || createAccommodationDTO.getAddress().getStreet() == null
                || createAccommodationDTO.getAddress().getCountry() == null){
            throw new AttributeNullException("Given address attribute is null");
        }

        Address address = CreateAddressDTO.from(createAccommodationDTO.getAddress());

        Accommodation accommodation = Accommodation.builder()
                .name(createAccommodationDTO.getName())
                .address(address)
                .benefits(createAccommodationDTO.getBenefits())
                .photos(createAccommodationDTO.getPhotos())
                .minGuests(createAccommodationDTO.getMinGuests())
                .maxGuests(createAccommodationDTO.getMaxGuests())
                .priceType(createAccommodationDTO.getPriceType())
                .requestApproval(createAccommodationDTO.getRequestApproval())
                .hostId(createAccommodationDTO.getHostId())
                .build();

        return accommodationRepository.save(accommodation);
    }

}
