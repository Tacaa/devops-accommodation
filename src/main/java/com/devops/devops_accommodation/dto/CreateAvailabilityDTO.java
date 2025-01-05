package com.devops.devops_accommodation.dto;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateAvailabilityDTO {
    private LocalDate startDate;
    private LocalDate endDate;
    private Double price;
    private Integer accommodationId;
}
