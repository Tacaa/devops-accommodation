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
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class AccommodationService {

    @Autowired
    private AccommodationRepository accommodationRepository;

    public Accommodation getById(Integer id){
        log.info("Fetching accommodation with ID: {}", id);
        return accommodationRepository.findById(id).orElse(null);
    }

    public List<Accommodation> getAllAccommodations(){
        log.info("Fetching all accommodations");
        return accommodationRepository.findAll();
    }

    public List<SearchAccommodationResultDTO> searchAccommodations(SearchAccommodationDTO searchDTO) {
        log.info("Searching accommodations with parameters: {}", searchDTO);

        if (searchDTO.getNumGuest() == null || searchDTO.getStartDate() == null || searchDTO.getEndDate() == null) {
            log.warn("Missing required search attributes: {}", searchDTO);
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

        log.debug("Available accommodations after filtering: {}", accommodations.size());
    return accommodations.stream()
        .map(accommodation -> {
          List<Availability> availabilities = accommodation.getAvailabilities().stream()
              .filter(av -> av.getAvailable() &&
                  !av.getEndDate().isBefore(searchDTO.getStartDate()) &&
                  !av.getStartDate().isAfter(searchDTO.getEndDate()))
              .collect(Collectors.toList());

          if (availabilities.isEmpty()) {
              log.warn("No availabilities found for accommodation ID {} in requested period", accommodation.getId());
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

          return SearchAccommodationResultDTO.from(
              accommodation,
              unitPrice,
              totalPrice
          );
        })
        .collect(Collectors.toList());

  }

    public Accommodation create(CreateAccommodationDTO createAccommodationDTO) {
        log.info("Creating accommodation for host ID: {}", createAccommodationDTO.getHostId());

        if(createAccommodationDTO.getName() == null
                || createAccommodationDTO.getBenefits() == null
                || createAccommodationDTO.getMinGuests() == null
                || createAccommodationDTO.getMaxGuests() == null
                || createAccommodationDTO.getPriceType() == null
                || createAccommodationDTO.getRequestApproval() == null
                || createAccommodationDTO.getHostId() == null
                ||  createAccommodationDTO.getPhotos() == null
                || createAccommodationDTO.getAddress() == null){
            log.error("Accommodation creation failed due to missing attributes: {}", createAccommodationDTO);
            throw new AttributeNullException("Given accommodation attribute is null");
        }

        if(createAccommodationDTO.getAddress().getCity() == null
            || createAccommodationDTO.getAddress().getNumber() == null
                || createAccommodationDTO.getAddress().getStreet() == null
                || createAccommodationDTO.getAddress().getCountry() == null){
            log.error("Accommodation creation failed due to missing address attributes: {}", createAccommodationDTO.getAddress());
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
                .deleted(false)
                .build();
        return accommodationRepository.save(accommodation);
    }

    public void deleteAllHostAccommodations(Integer hostId){
        log.info("Deleting all accommodations for host ID: {}", hostId);

        List<Accommodation> allAccommodationsOfHost = accommodationRepository.findAllByHostId(hostId);
        for(Accommodation accommodation : allAccommodationsOfHost){
            accommodation.setDeleted(true);
        }
        accommodationRepository.saveAll(allAccommodationsOfHost);
        log.info("Marked {} accommodations as deleted for host ID: {}", allAccommodationsOfHost.size(), hostId);
    }

}
