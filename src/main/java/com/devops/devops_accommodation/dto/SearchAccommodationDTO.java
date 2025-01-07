package com.devops.devops_accommodation.dto;


import lombok.*;
import org.springframework.cglib.core.Local;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SearchAccommodationDTO {
    private String city;
    private String country;
    private Integer numGuest; //obavezan unos
    private LocalDate startDate; //obavezan unos
    private LocalDate endDate; //obavezan unos
}
