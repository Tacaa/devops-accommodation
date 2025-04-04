package com.devops.devops_accommodation.services;

import com.devops.devops_accommodation.client.NotificationClient;
import com.devops.devops_accommodation.dto.CreateNotificationDTO;
import com.devops.devops_accommodation.dto.ReservationRequestDTO;
import com.devops.devops_accommodation.dto.ReservationRequestResponseDTO;
import com.devops.devops_accommodation.enumeration.NotificationType;
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
import org.springframework.web.bind.annotation.RequestParam;

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

    @Autowired
    private NotificationClient notificationClient;

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

        //posalji notifikaciju
        CreateNotificationDTO notificationDTO = CreateNotificationDTO.builder()
                .receiverId(accommodation.getHostId())
                .senderId(reservation.getGuestId())
                .title("Reservation Request")
                .content("Request for booking " + accommodation.getName())
                .notificationType(NotificationType.RESERVATION_REQUEST)
                .build();
        notificationClient.sendNotification(notificationDTO);

        return ReservationRequestResponseDTO.from(reservation);
    }


    public Reservation updateRequestStatus(Reservation reservation){
        if(reservation.getAccommodation().getRequestApproval() == RequestApproval.AUTOMATIC){
            reservation.setStatus(RequestStatus.ACCEPTED);
            //update availability
            this.updateAvailabilityAfterReservation(reservation);
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
            reservationRepository.save(reservation);

            //posalji notifikaciju
            CreateNotificationDTO notificationDTO = CreateNotificationDTO.builder()
                    .receiverId(reservation.getAccommodation().getHostId())
                    .senderId(reservation.getGuestId())
                    .title("Pending Reservation Cancel")
                    .content("Reservation Cancel for accommodation " + reservation.getAccommodation().getName())
                    .notificationType(NotificationType.RESERVATION_CANCELATION)
                    .build();
            notificationClient.sendNotification(notificationDTO);

            return ReservationRequestResponseDTO.from(reservation);
        }else if(reservation.getStatus() == RequestStatus.ACCEPTED){
            LocalDate today = LocalDate.now();
            if (today.equals(reservation.getStartDate().minusDays(1)) | today.isAfter(reservation.getStartDate().minusDays(1))) {
                throw new ReservationCanNotCancel("Too late to cancel reservation!");
            } else {
                reservation.setCanceled(true);

                Availability availability = availabilityRepository.findReservedAvailabilityToCancel(reservation.getAccommodation().getId(), reservation.getStartDate(), reservation.getEndDate());
                if(availability != null){
                    availability.setAvailable(true);
                    availabilityRepository.save(availability);
                }else{
                    throw new NotFoundException("Availability not found!");
                }

                //TODO: kod usera povecati broj otkaza, ili na frontu ili na bek
                reservationRepository.save(reservation);

                //posalji notifikaciju
                CreateNotificationDTO notificationDTO = CreateNotificationDTO.builder()
                        .receiverId(reservation.getAccommodation().getHostId())
                        .senderId(reservation.getGuestId())
                        .title("Accepted Reservation Cancel")
                        .content("Reservation Cancel for accommodation " + reservation.getAccommodation().getName())
                        .notificationType(NotificationType.RESERVATION_CANCELATION)
                        .build();
                notificationClient.sendNotification(notificationDTO);

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
                if(matchingReservation.getStatus() == RequestStatus.ACCEPTED){

                    //posalji notifikaciju
                    CreateNotificationDTO notificationDTO = CreateNotificationDTO.builder()
                            .receiverId(reservation.getGuestId())
                            .senderId(reservation.getAccommodation().getHostId())
                            .title("Host Response")
                            .content("Host accepted reservation for accommodation " + reservation.getAccommodation().getName())
                            .notificationType(NotificationType.HOST_RESPONSE)
                            .build();
                    notificationClient.sendNotification(notificationDTO);

                    this.updateAvailabilityAfterReservation(reservation);
                }else if(matchingReservation.getStatus() == RequestStatus.DECLINED){
                    reservation.setCanceled(true);

                    //posalji notifikaciju
                    CreateNotificationDTO notificationDTO = CreateNotificationDTO.builder()
                            .receiverId(reservation.getGuestId())
                            .senderId(reservation.getAccommodation().getHostId())
                            .title("Host Response")
                            .content("Host declined reservation for accommodation " + reservation.getAccommodation().getName())
                            .notificationType(NotificationType.HOST_RESPONSE)
                            .build();
                    notificationClient.sendNotification(notificationDTO);
                }
            }
        });

        reservationsDB = reservationRepository.saveAll(reservationsDB);

         return reservationsDB.stream()
                .map(ReservationRequestResponseDTO::from)
                .toList();
    }


    public void updateAvailabilityAfterReservation(Reservation reservation) {
        List<Availability> availabilities = availabilityRepository.findAvailabilitiesForReservation(reservation.getAccommodation().getId(), reservation.getStartDate(), reservation.getEndDate());

        for (Availability availability : availabilities) {
            // Ako postoji dio intervala prije rezervacije
            if (availability.getStartDate().isBefore(reservation.getStartDate())) {
                Availability before = new Availability();
                before.setAccommodation(availability.getAccommodation());
                before.setStartDate(availability.getStartDate());
                before.setEndDate(reservation.getStartDate().minusDays(1));
                before.setAvailable(true);
                before.setDeleted(false);
                before.setPrice(availability.getPrice());
                availabilityRepository.save(before);
            }

            // Ako postoji dio intervala poslije rezervacije
            if (availability.getEndDate().isAfter(reservation.getEndDate())) {
                Availability after = new Availability();
                after.setAccommodation(availability.getAccommodation());
                after.setStartDate(reservation.getEndDate().plusDays(1));
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

        //napravi availability za rezervaciju, ukoliko se otkaze da postane dostupan
        Availability availability = new Availability();
        availability.setAccommodation(reservation.getAccommodation());
        availability.setStartDate(reservation.getStartDate());
        availability.setEndDate(reservation.getEndDate());
        availability.setAvailable(false);
        availability.setDeleted(false);

        if(availabilities.isEmpty()){
            availability.setPrice(500.0);
        }else{
            double totalPrice = 0.0;
            for (Availability a : availabilities) {
                totalPrice += a.getPrice();
            }
            availability.setPrice(totalPrice / availabilities.size());
        }
        availabilityRepository.save(availability);
    }

    //za potrebe ocjene
    public boolean didGuestHadReservationInAccommodation(Integer guestId, Integer accommodationId){
        return reservationRepository.didGuestHadReservationInAccommodation(accommodationId, guestId);
    }

    public boolean didGuestHadReservationInHostAccommodation(Integer guestId, Integer hostId){
        return reservationRepository.didGuestHadReservationInHostAccommodation(hostId, guestId);
    }

    public boolean isGuestHavingReservationAtMoment(Integer guestId) {
        return reservationRepository.isGuestHavingReservationAtMoment(guestId, LocalDate.now());
    }

    public boolean isHostHavingReservationAtMoment(Integer hostId) {
       //pronadji sve smjestaje jednog hosta
        List<Accommodation> allAccommodationsOfHost = accommodationRepository.findAllByHostId(hostId);

        //nad svakim provjeri da li postoji rezervacija, prvi koji pronadjes vracas false
        for(Accommodation accommodation : allAccommodationsOfHost){
            if(reservationRepository.isAccommodationHavingReservationAtMoment(accommodation.getId(), LocalDate.now())){
                return true;
            }
        }
        return false;
    }

}
