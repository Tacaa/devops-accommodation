package com.devops.devops_accommodation.dto;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservationRequestDTO {
    private Integer accommodationId;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer numGuests;
    private Integer userId;
}
