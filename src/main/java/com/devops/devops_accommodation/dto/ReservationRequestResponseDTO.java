package com.devops.devops_accommodation.dto;

import com.devops.devops_accommodation.enumeration.RequestStatus;
import com.devops.devops_accommodation.model.Reservation;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservationRequestResponseDTO {
    private Integer id;
    private AccommodationDTO accommodation;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer numGuests;
    private Integer userId;
    private RequestStatus status;
    private Boolean canceled;
    private Boolean deleted;

    public static ReservationRequestResponseDTO from(Reservation reservation) {
        return ReservationRequestResponseDTO.builder()
                .id(reservation.getId())
                .accommodation(AccommodationDTO.from(reservation.getAccommodation()))
                .startDate(reservation.getStartDate())
                .endDate(reservation.getEndDate())
                .numGuests(reservation.getGuestNum())
                .userId(reservation.getGuestId())
                .status(reservation.getStatus())
                .canceled(reservation.isCanceled())
                .deleted(reservation.isDeleted())
                .build();
    }
}
