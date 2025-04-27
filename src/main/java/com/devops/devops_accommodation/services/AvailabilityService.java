package com.devops.devops_accommodation.services;

import com.devops.devops_accommodation.dto.CreateAvailabilityDTO;
import com.devops.devops_accommodation.exceptions.PeriodNotAvailable;
import com.devops.devops_accommodation.exceptions.ResourceNotFoundException;
import com.devops.devops_accommodation.exceptions.WrongHostException;
import com.devops.devops_accommodation.model.Accommodation;
import com.devops.devops_accommodation.model.Availability;
import com.devops.devops_accommodation.repository.AccommodationRepository;
import com.devops.devops_accommodation.repository.AvailabilityRepository;
import com.devops.devops_accommodation.repository.ReservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

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

    public Availability createAvailability(CreateAvailabilityDTO createAvailabilityDTO, String hostId) {
        Accommodation accommodation = accommodationRepository.findById(createAvailabilityDTO.getAccommodationId())
                .orElseThrow(() -> new ResourceNotFoundException("Accommodation not found"));

        if(!hostId.equals(String.valueOf(accommodation.getHostId().intValue()))){
            throw new WrongHostException("The host cannot change the availability for someone else's accommodation.");
        }

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


    public Availability updateAvailability(Integer availabilityId, CreateAvailabilityDTO createAvailabilityDTO, String hostId) {
        Availability availability = availabilityRepository.findById(availabilityId)
                .orElseThrow(() -> new ResourceNotFoundException("Availability not found"));

        if(!hostId.equals(String.valueOf(availability.getAccommodation().getHostId().intValue()))){
            throw new WrongHostException("The host cannot change the availability for someone else's accommodation.");
        }

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
