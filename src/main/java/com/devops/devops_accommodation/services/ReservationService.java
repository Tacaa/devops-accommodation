package com.devops.devops_accommodation.services;

import com.devops.devops_accommodation.dto.ReservationRequestDTO;
import com.devops.devops_accommodation.dto.ReservationRequestResponseDTO;
import com.devops.devops_accommodation.enumeration.RequestApproval;
import com.devops.devops_accommodation.enumeration.RequestStatus;
import com.devops.devops_accommodation.exceptions.AvailabilityNotExists;
import com.devops.devops_accommodation.exceptions.NotFoundException;
import com.devops.devops_accommodation.exceptions.ReservationCanNotCancel;
import com.devops.devops_accommodation.exceptions.ReservationConflictException;
import com.devops.devops_accommodation.model.Accommodation;
import com.devops.devops_accommodation.model.Availability;
import com.devops.devops_accommodation.model.Reservation;
import com.devops.devops_accommodation.repository.AccommodationRepository;
import com.devops.devops_accommodation.repository.AvailabilityRepository;
import com.devops.devops_accommodation.repository.ReservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ReservationService {

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private AccommodationRepository accommodationRepository;

    @Autowired
    private AvailabilityRepository availabilityRepository;

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

    public ReservationRequestResponseDTO cancelOrDelete(Integer id){
        Reservation reservation = reservationRepository.findById(id).orElseThrow(() -> new NotFoundException("Reservation not found"));

        if(reservation.getStatus() == RequestStatus.PENDING){
            reservation.setDeleted(true);
            return ReservationRequestResponseDTO.from(reservation);
        }else if(reservation.getStatus() == RequestStatus.ACCEPTED){
            LocalDate today = LocalDate.now();

            if (today.equals(reservation.getStartDate().minusDays(1)) | today.isAfter(reservation.getStartDate().minusDays(1))) {
                throw new ReservationCanNotCancel("Too late to cancel reservation!");
            } else {
                reservation.setCanceled(true);

                //TODO: kod usera povecati broj otkaza, ili na frontu ili na beku
                return ReservationRequestResponseDTO.from(reservation);
            }
        }else{
            throw new ReservationCanNotCancel("Reservation is already declined");
        }
    }

    public List<ReservationRequestResponseDTO> getAllPendingReservationRequestsByHost(Integer id){
        List<Reservation> reservations = reservationRepository.getAllPendingReservationRequestsByHost(id);
        return reservations.stream()
                .map(ReservationRequestResponseDTO::from)
                .toList();
    }

    public List<ReservationRequestResponseDTO> saveReservationsManually(List<ReservationRequestResponseDTO> reservations) {
        List<Integer> ids = reservations.stream()
                .map(ReservationRequestResponseDTO::getId)
                .toList();

        List<Reservation> reservationsDB = reservationRepository.findListOfReservations(ids);

        Map<Integer, ReservationRequestResponseDTO> reservationsMap = reservations.stream()
                .collect(Collectors.toMap(ReservationRequestResponseDTO::getId, dto -> dto));

        reservationsDB.forEach(reservation -> {
            ReservationRequestResponseDTO matchingReservation = reservationsMap.get(reservation.getId());
            if (matchingReservation != null) {
                reservation.setStatus(matchingReservation.getStatus());
            }
        });

        reservationsDB = reservationRepository.saveAll(reservationsDB);

         return reservationsDB.stream()
                .map(ReservationRequestResponseDTO::from)
                .toList();
    }


    public void updateAvailabilityAfterReservation(Integer accommodationId, LocalDate startDate, LocalDate endDate) {
        List<Availability> availabilities = availabilityRepository.findAvailabilitiesForReservation(accommodationId, startDate, endDate);

        for (Availability availability : availabilities) {
            // Ako postoji dio intervala prije rezervacije
            if (availability.getStartDate().isBefore(startDate)) {
                Availability before = new Availability();
                before.setAccommodation(availability.getAccommodation());
                before.setStartDate(availability.getStartDate());
                before.setEndDate(startDate.minusDays(1));
                before.setAvailable(true);
                before.setDeleted(false);
                before.setPrice(availability.getPrice());
                availabilityRepository.save(before);
            }

            // Ako postoji dio intervala poslije rezervacije
            if (availability.getEndDate().isAfter(endDate)) {
                Availability after = new Availability();
                after.setAccommodation(availability.getAccommodation());
                after.setStartDate(endDate.plusDays(1));
                after.setEndDate(availability.getEndDate());
                after.setAvailable(true);
                after.setDeleted(false);
                after.setPrice(availability.getPrice());
                availabilityRepository.save(after);
            }

            // Obriši originalni interval jer ga rezervacija pokriva
            availability.setDeleted(true);
            availabilityRepository.save(availability);
        }
    }

}
