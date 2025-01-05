package com.devops.devops_accommodation.dto;

import com.devops.devops_accommodation.model.Availability;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AvailabilityDTO {
    private Integer id;
    private LocalDate startDate;
    private LocalDate endDate;
    private Boolean available;
    private Double price;
    private Integer accommodationId;

    public static AvailabilityDTO from(Availability availability) {
        return AvailabilityDTO.builder()
                .id(availability.getId())
                .startDate(availability.getStartDate())
                .endDate(availability.getEndDate())
                .available(availability.getAvailable())
                .price(availability.getPrice())
                .accommodationId(availability.getAccommodation().getId())
                .build();
    }

}
