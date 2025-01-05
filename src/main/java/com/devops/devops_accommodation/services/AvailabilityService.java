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

        availability.setStartDate(createAvailabilityDTO.getStartDate());
        availability.setEndDate(createAvailabilityDTO.getEndDate());
        availability.setPrice(createAvailabilityDTO.getPrice());

        return availabilityRepository.save(availability);
    }
}
