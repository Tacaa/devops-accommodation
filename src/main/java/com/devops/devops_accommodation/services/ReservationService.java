package com.devops.devops_accommodation.services;

import com.devops.devops_accommodation.dto.ReservationRequestDTO;
import com.devops.devops_accommodation.dto.ReservationRequestResponseDTO;
import com.devops.devops_accommodation.enumeration.RequestApproval;
import com.devops.devops_accommodation.enumeration.RequestStatus;
import com.devops.devops_accommodation.exceptions.AvailabilityNotExists;
import com.devops.devops_accommodation.exceptions.NotFoundException;
import com.devops.devops_accommodation.exceptions.ReservationConflictException;
import com.devops.devops_accommodation.model.Accommodation;
import com.devops.devops_accommodation.model.Reservation;
import com.devops.devops_accommodation.repository.AccommodationRepository;
import com.devops.devops_accommodation.repository.ReservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ReservationService {

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private AccommodationRepository accommodationRepository;

    public ReservationRequestResponseDTO createReservationRequest(ReservationRequestDTO requestDTO) {
        Accommodation accommodation = accommodationRepository.findById(requestDTO.getAccommodationId())
                .orElseThrow(() -> new NotFoundException("Accommodation not found"));

        boolean isConflict = reservationRepository.existsReservation(
                accommodation.getId(), requestDTO.getStartDate(), requestDTO.getEndDate());

        if (isConflict) {
            throw new ReservationConflictException("This accommodation is already reserved for the given date range");
        }

        boolean exist = accommodationRepository.checkAccommodationsAvailability(accommodation.getId(), requestDTO.getStartDate(), requestDTO.getEndDate());

        if(!exist){
            throw new AvailabilityNotExists("There is no availability for this accommodation in this period of time!");
        }

        Reservation reservation = new Reservation();
        reservation.setAccommodation(accommodation);
        reservation.setStartDate(requestDTO.getStartDate());
        reservation.setEndDate(requestDTO.getEndDate());
        reservation.setGuestNum(requestDTO.getNumGuests());
        reservation.setDeleted(false);
        reservation.setCanceled(false);
        reservation.setGuestId(requestDTO.getUserId());

        reservation = this.updateRequestStatus(reservation);

        reservation = reservationRepository.save(reservation);
        return ReservationRequestResponseDTO.from(reservation);
    }


    public Reservation updateRequestStatus(Reservation reservation){
        if(reservation.getAccommodation().getRequestApproval() == RequestApproval.AUTOMATIC){
            reservation.setStatus(RequestStatus.ACCEPTED);
            return reservation;

        }else{
            //manuelno
            reservation.setStatus(RequestStatus.PENDING);
            return reservation;
        }
    }



}
