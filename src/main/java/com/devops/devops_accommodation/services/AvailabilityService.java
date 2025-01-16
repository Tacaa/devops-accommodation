package com.devops.devops_accommodation.services;

import com.devops.devops_accommodation.dto.CreateAvailabilityDTO;
import com.devops.devops_accommodation.exceptions.PeriodNotAvailable;
import com.devops.devops_accommodation.exceptions.ResourceNotFoundException;
import com.devops.devops_accommodation.model.Accommodation;
import com.devops.devops_accommodation.model.Availability;
import com.devops.devops_accommodation.repository.AccommodationRepository;
import com.devops.devops_accommodation.repository.AvailabilityRepository;
import com.devops.devops_accommodation.repository.ReservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AvailabilityService {
    @Autowired
    AvailabilityRepository availabilityRepository;

    @Autowired
    AccommodationRepository accommodationRepository;

    @Autowired
    ReservationRepository reservationRepository;

    public List<Availability> getAllAvailabilitiesByAccommodationId(Integer accommodationId){
        return availabilityRepository.findByAccommodationId(accommodationId);
    }

    public Availability createAvailability(CreateAvailabilityDTO createAvailabilityDTO) {
        Accommodation accommodation = accommodationRepository.findById(createAvailabilityDTO.getAccommodationId())
                .orElseThrow(() -> new ResourceNotFoundException("Accommodation not found"));

        boolean hasReservations = reservationRepository.existsByAccommodationAndDateRange(accommodation, createAvailabilityDTO.getStartDate(), createAvailabilityDTO.getEndDate());
        if (hasReservations) {
            throw new PeriodNotAvailable("Cannot set availability for this interval, there are reservations.");
        }

        boolean hasAvailability = availabilityRepository.existsAvailabilityByAccommodationAndDateRange(accommodation, createAvailabilityDTO.getStartDate(), createAvailabilityDTO.getEndDate());
        if (hasAvailability) {
            throw new PeriodNotAvailable("Availability for this period exists");
        }

        Availability availability = new Availability();
        availability.setAccommodation(accommodation);
        availability.setStartDate(createAvailabilityDTO.getStartDate());
        availability.setEndDate(createAvailabilityDTO.getEndDate());
        availability.setAvailable(true);
        availability.setDeleted(false);
        availability.setPrice(createAvailabilityDTO.getPrice());
        return availabilityRepository.save(availability);
    }


    public Availability updateAvailability(Integer availabilityId, CreateAvailabilityDTO createAvailabilityDTO) {
        Availability availability = availabilityRepository.findById(availabilityId)
                .orElseThrow(() -> new ResourceNotFoundException("Availability not found"));

        boolean hasReservations = reservationRepository.existsByAccommodationAndDateRange(availability.getAccommodation(), createAvailabilityDTO.getStartDate(), createAvailabilityDTO.getEndDate());
        if (hasReservations) {
            throw new PeriodNotAvailable("Cannot update availability for this interval, there are reservations.");
        }

        if(!availability.getStartDate().isEqual(createAvailabilityDTO.getStartDate()) || !availability.getEndDate().isEqual(createAvailabilityDTO.getEndDate())){
            //provjera ako zeli promijeniti interval, moram vidjeti da li postoji taj interval vec, da li je zauzet
            boolean hasAvailability = availabilityRepository.existsAvailabilityByAvailabilityIdAndDateRange(availability.getId(), availability.getAccommodation(), createAvailabilityDTO.getStartDate(), createAvailabilityDTO.getEndDate());
            if (hasAvailability) {
                throw new PeriodNotAvailable("Availability with that interval already exixsts!");
            }
        }

        availability.setStartDate(createAvailabilityDTO.getStartDate());
        availability.setEndDate(createAvailabilityDTO.getEndDate());
        availability.setDeleted(createAvailabilityDTO.getDeleted());
        availability.setPrice(createAvailabilityDTO.getPrice());

        return availabilityRepository.save(availability);
    }
}
